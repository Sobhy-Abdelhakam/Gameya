package dev.sobhy.gameya.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import dev.sobhy.gameya.data.local.entity.ShareEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ShareDao {

    @Insert
    suspend fun insertShares(shares: List<ShareEntity>)

    @Query("SELECT * FROM shares WHERE groupId = :groupId ORDER BY orderIndex")
    fun getShares(groupId: Long): Flow<List<ShareEntity>>

    @Query("UPDATE shares SET orderIndex = :newIndex WHERE id = :shareId")
    suspend fun updateOrder(shareId: Long, newIndex: Int)
}