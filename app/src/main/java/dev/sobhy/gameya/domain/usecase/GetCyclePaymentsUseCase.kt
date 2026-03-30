package dev.sobhy.gameya.domain.usecase

import dev.sobhy.gameya.domain.model.Payment
import dev.sobhy.gameya.domain.repository.GroupRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetCyclePaymentsUseCase @Inject constructor(
    private val repository: GroupRepository
) {
    operator fun invoke(cycleId: Long): Flow<List<Payment>> {
        return repository.getCyclePayments(cycleId)
    }
}