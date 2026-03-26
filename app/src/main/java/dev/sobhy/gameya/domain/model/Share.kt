package dev.sobhy.gameya.domain.model

data class Share(
    val id: Long = 0,
    val groupId: Long,
    val memberId: Long,
    val fraction: Double,
    val orderIndex: Int,
)
