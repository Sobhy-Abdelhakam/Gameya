package dev.sobhy.gameya.domain.model

import dev.sobhy.gameya.domain.enums.CycleType

data class Group(
    val id: Long = 0,
    val name: String,
    val contributionPerShare: Double,
    val cycleType: CycleType,
    val startDate: Long,
)
