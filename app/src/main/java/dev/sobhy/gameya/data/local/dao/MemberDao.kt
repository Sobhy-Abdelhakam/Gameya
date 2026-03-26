package dev.sobhy.gameya.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import dev.sobhy.gameya.data.local.entity.MemberEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface MemberDao {

    @Insert
    suspend fun insertMembers(members: List<MemberEntity>)

    @Query("SELECT * FROM members WHERE groupId = :groupId")
    fun getMembers(groupId: Long): Flow<List<MemberEntity>>
}