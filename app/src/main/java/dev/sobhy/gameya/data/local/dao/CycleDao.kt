package dev.sobhy.gameya.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import dev.sobhy.gameya.data.local.entity.CycleEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface CycleDao {

    @Insert
    suspend fun insertCycles(cycles: List<CycleEntity>): List<Long>

    @Query("SELECT * FROM cycles WHERE groupId = :groupId ORDER BY cycleIndex")
    fun getCycles(groupId: Long): Flow<List<CycleEntity>>
    @Query("SELECT * FROM cycles WHERE id = :cycleId")
    suspend fun getCycleById(cycleId: Long): CycleEntity
}