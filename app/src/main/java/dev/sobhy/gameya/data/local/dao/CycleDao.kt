package dev.sobhy.gameya.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import dev.sobhy.gameya.data.local.entity.CycleEntity
import kotlinx.coroutines.flow.Flow

data class CyclePayoutHistoryRow(
    val cycleId: Long,
    val groupId: Long,
    val groupName: String,
    val cycleIndex: Int,
    val cycleDate: Long,
    val payoutMemberName: String,
    val isClosed: Boolean
)

@Dao
interface CycleDao {

    @Insert
    suspend fun insertCycles(cycles: List<CycleEntity>): List<Long>

    /** Single-row insert (e.g. restore with explicit id). */
    @Insert
    suspend fun insertCycle(cycle: CycleEntity): Long

    @Query("SELECT * FROM cycles WHERE groupId = :groupId ORDER BY cycleIndex")
    fun getCycles(groupId: Long): Flow<List<CycleEntity>>
    @Query("SELECT * FROM cycles WHERE id = :cycleId")
    suspend fun getCycleById(cycleId: Long): CycleEntity

    @Query("SELECT * FROM cycles ORDER BY id ASC")
    suspend fun getAllCycles(): List<CycleEntity>

    @Query(
        """
        SELECT c.id AS cycleId, c.groupId AS groupId, g.name AS groupName, c.cycleIndex AS cycleIndex,
               c.date AS cycleDate, m.name AS payoutMemberName, c.isClosed AS isClosed
        FROM cycles c
        INNER JOIN shares s ON s.id = c.payoutShareId
        INNER JOIN members m ON m.id = s.memberId
        INNER JOIN `groups` g ON g.id = c.groupId
        ORDER BY c.groupId ASC, c.cycleIndex ASC
        """
    )
    suspend fun getCyclePayoutHistory(): List<CyclePayoutHistoryRow>
}