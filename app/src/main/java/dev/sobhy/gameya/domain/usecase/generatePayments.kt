package dev.sobhy.gameya.domain.usecase

import dev.sobhy.gameya.domain.enums.PaymentStatus
import dev.sobhy.gameya.domain.model.Member
import dev.sobhy.gameya.domain.model.Payment

fun generatePayments(
    cycleId: Long,
    members: List<Member>,
    contributionPerShare: Double
): List<Payment> {

    return members.map { member ->

        val amount = member.shares * contributionPerShare

        Payment(
            cycleId = cycleId,
            memberId = member.id,
            amount = amount,
            status = PaymentStatus.UNPAID,
            paidAt = null
        )
    }
}