package dev.sobhy.gameya.domain.usecase

import dev.sobhy.gameya.domain.repository.GroupRepository

class RecordPaymentUseCase(
    private val repository: GroupRepository
) {
    suspend operator fun invoke(paymentId: Long) {
        repository.markPaymentAsPaid(paymentId)
    }
}