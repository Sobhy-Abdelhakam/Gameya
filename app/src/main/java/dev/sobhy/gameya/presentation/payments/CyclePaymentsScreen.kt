package dev.sobhy.gameya.presentation.payments

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import dev.sobhy.gameya.domain.enums.PaymentStatus

@Composable
fun CyclePaymentsScreen(
    modifier: Modifier = Modifier,
            viewModel: CyclePaymentsViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()

    val membersMap = remember(state.members) {
        state.members.associateBy { it.id }
    }

    if (state.isLoading) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator()
        }
        return
    }

    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(8.dp)
    ) {

        items(state.payments) { payment ->

            val member = membersMap[payment.memberId]

            PaymentItem(
                memberName = member?.name ?: "Unknown",
                amount = payment.amount,
                status = payment.status,
                onMarkPaid = {
                    viewModel.onMarkPaid(payment.id)
                }
            )
        }
    }
}