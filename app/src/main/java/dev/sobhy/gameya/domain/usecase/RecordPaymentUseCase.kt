package dev.sobhy.gameya.domain.usecase

import dev.sobhy.gameya.domain.repository.GroupRepository
import javax.inject.Inject

class RecordPaymentUseCase @Inject constructor(
    private val repository: GroupRepository
) {
    suspend operator fun invoke(paymentId: Long) {
        repository.markPaymentAsPaid(paymentId)
    }
}