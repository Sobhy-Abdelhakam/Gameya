package dev.sobhy.gameya.domain.repository

import dev.sobhy.gameya.domain.model.Group
import dev.sobhy.gameya.domain.model.GroupDetails
import dev.sobhy.gameya.domain.model.Member
import kotlinx.coroutines.flow.Flow

interface GroupRepository {

    suspend fun createGroup(
        group: Group,
        members: List<Member>
    )

    fun getGroups(): Flow<List<Group>>

    fun getGroupDetails(groupId: Long): Flow<GroupDetails>
}