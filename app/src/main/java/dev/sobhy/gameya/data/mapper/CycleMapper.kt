package dev.sobhy.gameya.data.mapper

import dev.sobhy.gameya.data.local.entity.CycleEntity
import dev.sobhy.gameya.domain.model.Cycle

fun Cycle.toEntity() = CycleEntity(
    id = id,
    groupId = groupId,
    cycleIndex = cycleIndex,
    date = date,
    payoutShareId = payoutShareId,
    isClosed = isClosed
)

fun CycleEntity.toDomain() = Cycle(
    id = id,
    groupId = groupId,
    cycleIndex = cycleIndex,
    date = date,
    payoutShareId = payoutShareId,
    isClosed = isClosed
)