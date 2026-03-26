package dev.sobhy.gameya.data.mapper

import dev.sobhy.gameya.data.local.entity.PaymentEntity
import dev.sobhy.gameya.domain.enums.PaymentStatus
import dev.sobhy.gameya.domain.model.Payment

fun Payment.toEntity() = PaymentEntity(
    id = id,
    cycleId = cycleId,
    memberId = memberId,
    amount = amount,
    status = status.name,
    paidAt = paidAt
)

fun PaymentEntity.toDomain() = Payment(
    id = id,
    cycleId = cycleId,
    memberId = memberId,
    amount = amount,
    status = PaymentStatus.valueOf(status),
    paidAt = paidAt
)