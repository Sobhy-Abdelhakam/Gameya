package dev.sobhy.gameya.domain.usecase

import dev.sobhy.gameya.domain.enums.PaymentStatus
import dev.sobhy.gameya.domain.model.Cycle
import dev.sobhy.gameya.domain.model.Payment
import javax.inject.Inject
import kotlin.collections.map

class GetPaymentsWithStatusUseCase @Inject constructor() {

    operator fun invoke(
        payments: List<Payment>,
        cycle: Cycle
    ): List<Payment> {

        val now = System.currentTimeMillis()

        return payments.map { payment ->

            val isLate =
                now > cycle.date &&
                        payment.status != PaymentStatus.PAID

            if (isLate) {
                payment.copy(status = PaymentStatus.LATE)
            } else {
                payment
            }
        }
    }
}