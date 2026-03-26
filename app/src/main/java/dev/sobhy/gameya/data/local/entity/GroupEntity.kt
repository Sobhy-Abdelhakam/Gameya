package dev.sobhy.gameya.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "groups")
data class GroupEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val contributionPerShare: Double,
    val cycleType: String,
    val startDate: Long,
    val createdAt: Long
)