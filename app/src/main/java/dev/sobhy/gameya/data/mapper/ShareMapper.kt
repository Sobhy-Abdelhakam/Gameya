package dev.sobhy.gameya.data.mapper

import dev.sobhy.gameya.data.local.entity.ShareEntity
import dev.sobhy.gameya.domain.model.Share

fun Share.toEntity() = ShareEntity(
    id = id,
    groupId = groupId,
    memberId = memberId,
    fraction = fraction,
    orderIndex = orderIndex
)

fun ShareEntity.toDomain() = Share(
    id = id,
    groupId = groupId,
    memberId = memberId,
    fraction = fraction,
    orderIndex = orderIndex
)