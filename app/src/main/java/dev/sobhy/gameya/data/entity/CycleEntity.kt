package dev.sobhy.gameya.data.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "cycles",
    foreignKeys = [
        ForeignKey(
            entity = ShareEntity::class,
            parentColumns = ["id"],
            childColumns = ["payoutShareId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("groupId"), Index("payoutShareId")]
)
data class CycleEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val groupId: Long,
    val cycleIndex: Int,
    val date: Long,
    val payoutShareId: Long,
    val isClosed: Boolean
)
