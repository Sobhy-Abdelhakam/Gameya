package dev.sobhy.gameya.data.mapper

import dev.sobhy.gameya.data.local.entity.GroupEntity
import dev.sobhy.gameya.domain.enums.CycleType
import dev.sobhy.gameya.domain.model.Group

fun Group.toEntity() = GroupEntity(
    id = id,
    name = name,
    contributionPerShare = contributionPerShare,
    cycleType = cycleType.name,
    startDate = startDate,
    createdAt = System.currentTimeMillis()
)

fun GroupEntity.toDomain() = Group(
    id = id,
    name = name,
    contributionPerShare = contributionPerShare,
    cycleType = CycleType.valueOf(cycleType),
    startDate = startDate
)