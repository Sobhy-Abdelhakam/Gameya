package dev.sobhy.gameya.data.mapper

import dev.sobhy.gameya.data.local.entity.MemberEntity
import dev.sobhy.gameya.domain.model.Member

fun Member.toEntity() = MemberEntity(
    id = id,
    groupId = groupId,
    name = name,
    phone = phone,
    shares = shares
)

fun MemberEntity.toDomain() = Member(
    id = id,
    groupId = groupId,
    name = name,
    phone = phone,
    shares = shares
)