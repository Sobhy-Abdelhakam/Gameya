package dev.sobhy.gameya.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import dev.sobhy.gameya.data.local.entity.GroupEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface GroupDao {

    @Insert
    suspend fun insertGroup(group: GroupEntity): Long

    @Query("SELECT * FROM `groups`")
    fun getAllGroups(): Flow<List<GroupEntity>>

    @Query("SELECT * FROM `groups` WHERE id = :groupId")
    fun getGroupById(groupId: Long): Flow<GroupEntity>

    @Delete
    suspend fun deleteGroup(group: GroupEntity)
}