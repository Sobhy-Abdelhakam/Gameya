package dev.sobhy.gameya.presentation.payments

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.sobhy.gameya.domain.usecase.GetCyclePaymentsUseCase
import dev.sobhy.gameya.domain.usecase.RecordPaymentUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CyclePaymentsViewModel @Inject constructor(
    private val getCyclePayments: GetCyclePaymentsUseCase,
    private val recordPayment: RecordPaymentUseCase,
    savedStateHandle: SavedStateHandle
) : ViewModel() {
    private val cycleId: Long = checkNotNull(
        savedStateHandle["cycleId"]
    )

    private val _state = MutableStateFlow(CyclePaymentsState())
    val state = _state.asStateFlow()

    init {
        loadPayments()
    }

    private fun loadPayments() {
        viewModelScope.launch {
            getCyclePayments(cycleId).collect { payments ->
                _state.update {
                    it.copy(
                        isLoading = false,
                        payments = payments
                    )
                }
            }
        }
    }
    fun onMarkPaid(paymentId: Long) {
        viewModelScope.launch {
            recordPayment(paymentId)
        }
    }
}