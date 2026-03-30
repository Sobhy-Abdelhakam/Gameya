package dev.sobhy.gameya.presentation.payments

import dev.sobhy.gameya.domain.model.Member
import dev.sobhy.gameya.domain.model.Payment

data class CyclePaymentsState(
    val isLoading: Boolean = true,
    val payments: List<Payment> = emptyList(),
    val members: List<Member> = emptyList(), // 👈 مهم
    val error: String? = null
)
