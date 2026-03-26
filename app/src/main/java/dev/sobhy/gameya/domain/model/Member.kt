package dev.sobhy.gameya.domain.model

data class Member(
    val id: Long = 0,
    val groupId: Long,
    val name: String,
    val phone: String,
    val shares: Double,
)
