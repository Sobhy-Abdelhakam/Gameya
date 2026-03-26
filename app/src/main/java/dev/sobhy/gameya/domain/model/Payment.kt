package dev.sobhy.gameya.domain.model

import dev.sobhy.gameya.domain.enums.PaymentStatus

data class Payment(
    val id: Long = 0,
    val cycleId: Long,
    val memberId: Long,
    val amount: Double,
    val status: PaymentStatus,
    val paidAt: Long?
)
