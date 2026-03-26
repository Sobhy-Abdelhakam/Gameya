package dev.sobhy.gameya.domain.model

data class GroupDetails(
    val group: Group,
    val members: List<Member>,
    val shares: List<Share>,
    val cycles: List<Cycle>
)