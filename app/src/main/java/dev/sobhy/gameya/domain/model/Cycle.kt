package dev.sobhy.gameya.domain.model

data class Cycle(
    val id: Long = 0,
    val groupId: Long,
    val cycleIndex: Int,
    val date: Long,
    val payoutShareId: Long,
    val isClosed: Boolean,
)
