package dev.sobhy.gameya.data.repository_impl

import androidx.room.Transaction
import dev.sobhy.gameya.data.local.AppDatabase
import dev.sobhy.gameya.data.mapper.toDomain
import dev.sobhy.gameya.data.mapper.toEntity
import dev.sobhy.gameya.domain.model.Group
import dev.sobhy.gameya.domain.model.Member
import dev.sobhy.gameya.domain.model.GroupDetails
import dev.sobhy.gameya.domain.repository.GroupRepository
import dev.sobhy.gameya.domain.usecase.generateCycles
import dev.sobhy.gameya.domain.usecase.generateShares
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class GroupRepositoryImpl @Inject constructor(
    private val db: AppDatabase
) : GroupRepository {
    @Transaction
    override suspend fun createGroup(
        group: Group,
        members: List<Member>
    ) {

        // 1. Insert Group
        val groupId = db.groupDao().insertGroup(group.toEntity())

        // 2. Insert Members
        val memberEntities = members.map {
            it.copy(groupId = groupId).toEntity()
        }
        db.memberDao().insertMembers(memberEntities)

        // 3. Fetch Members with IDs
        val insertedMembers = db.memberDao()
            .getMembers(groupId)
            .first()

        // 4. Generate Shares
        val shares = generateShares(insertedMembers.map { it.toDomain() })

        db.shareDao().insertShares(shares.map { it.toEntity() })

        // 5. Fetch Shares with IDs
        val insertedShares = db.shareDao()
            .getShares(groupId)
            .first()

        // 6. Generate Cycles
        val cycles = generateCycles(
            group.copy(id = groupId),
            insertedShares.map { it.toDomain() }
        )

        db.cycleDao().insertCycles(cycles.map { it.toEntity() })
    }

    override fun getGroups(): Flow<List<Group>> {
        return db.groupDao()
            .getAllGroups()
            .map { list -> list.map { it.toDomain() } }
    }

    override fun getGroupDetails(groupId: Long): Flow<GroupDetails> {
        return combine(
            db.groupDao().getGroupById(groupId),
            db.memberDao().getMembers(groupId),
            db.shareDao().getShares(groupId),
            db.cycleDao().getCycles(groupId)
        ) { group, members, shares, cycles ->

            GroupDetails(
                group = group.toDomain(),
                members = members.map { it.toDomain() },
                shares = shares.map { it.toDomain() },
                cycles = cycles.map { it.toDomain() }
            )
        }
    }

    @Transaction
    override suspend fun updateSharesOrderBulk(updates: List<Pair<Long, Int>>) {
        updates.forEach { (id, orderIndex) ->
            db.shareDao().updateOrder(id, orderIndex)
        }
    }
}