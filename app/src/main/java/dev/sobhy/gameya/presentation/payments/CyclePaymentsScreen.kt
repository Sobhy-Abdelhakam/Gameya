package dev.sobhy.gameya.presentation.payments

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import dev.sobhy.gameya.R
import dev.sobhy.gameya.domain.enums.PaymentStatus
import dev.sobhy.gameya.presentation.common.EmptyStateCard
import dev.sobhy.gameya.presentation.common.LedgerTotalsRow
import dev.sobhy.gameya.presentation.common.formatMoney
import dev.sobhy.gameya.ui.theme.AppSpacing
import androidx.compose.animation.core.tween

@Composable
fun CyclePaymentsScreen(
    modifier: Modifier = Modifier,
    viewModel: CyclePaymentsViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()

    val membersMap = remember(state.members) {
        state.members.associateBy { it.id }
    }

    val paymentLabels = PaymentItemLabels(
        memberContributionSubtitle = stringResource(R.string.payment_member_subtitle),
        contributionPrefix = stringResource(R.string.prefix_contribution),
        recordPayment = stringResource(R.string.payment_record),
        paymentRecorded = stringResource(R.string.payment_recorded),
        paid = stringResource(R.string.payment_status_paid),
        pending = stringResource(R.string.payment_status_pending),
        late = stringResource(R.string.payment_status_late),
    )

    if (state.isLoading) {
        val loadingLabel = stringResource(R.string.payments_loading)
        Box(
            modifier = Modifier
                .fillMaxSize()
                .semantics { contentDescription = loadingLabel },
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator()
        }
        return
    }

    if (state.error != null) {
        Box(
            modifier = modifier
                .fillMaxSize()
                .padding(AppSpacing.md),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = state.error ?: stringResource(R.string.payments_error_load),
                color = MaterialTheme.colorScheme.error
            )
        }
        return
    }

    if (state.payments.isEmpty()) {
        Box(
            modifier = modifier
                .fillMaxSize()
                .padding(AppSpacing.md),
            contentAlignment = Alignment.Center
        ) {
            EmptyStateCard(
                title = stringResource(R.string.payments_empty_title),
                description = stringResource(R.string.payments_empty_body)
            )
        }
        return
    }

    val total = state.payments.sumOf { it.amount }
    val collected = state.payments
        .filter { it.status == PaymentStatus.PAID }
        .sumOf { it.amount }
    val remaining = (total - collected).coerceAtLeast(0.0)
    val paidCount = state.payments.count { it.status == PaymentStatus.PAID }
    val lateCount = state.payments.count { it.status == PaymentStatus.LATE }
    val pendingCount = state.payments.count { it.status == PaymentStatus.UNPAID }
    val progress = if (total > 0) (collected / total).toFloat() else 0f
    val progressPercent = (progress * 100).toInt().coerceIn(0, 100)
    val allPaid = paidCount == state.payments.size && state.payments.isNotEmpty()
    val hasLate = lateCount > 0

    val currency = stringResource(R.string.currency_suffix)
    val progressA11y = stringResource(R.string.payments_progress_a11y, progressPercent)

    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(AppSpacing.sm),
        verticalArrangement = Arrangement.spacedBy(AppSpacing.sm)
    ) {
        if (hasLate && !allPaid) {
            item(key = "banner_late") {
                LatePaymentsBanner(
                    title = stringResource(R.string.payments_late_banner_title),
                    body = stringResource(R.string.payments_late_banner_body),
                    modifier = Modifier.animateItem()
                )
            }
        }

        if (allPaid) {
            item(key = "banner_complete") {
                CycleCompleteBanner(
                    title = stringResource(R.string.payments_cycle_complete_title),
                    body = stringResource(R.string.payments_cycle_complete_body),
                    modifier = Modifier.animateItem()
                )
            }
        }

        item(key = "ledger") {
            LedgerTotalsRow(
                modifier = Modifier.animateItem(),
                total = total,
                collected = collected,
                remaining = remaining,
                progress = progress,
                progressPercent = progressPercent,
                paidCount = paidCount,
                lateCount = lateCount,
                pendingCount = pendingCount,
                title = stringResource(R.string.payments_ledger_title),
                subtitle = stringResource(R.string.payments_ledger_subtitle),
                collectedPrefix = stringResource(R.string.payments_collected_prefix),
                expectedPrefix = stringResource(R.string.payments_expected_prefix),
                collectedPercentText = stringResource(R.string.payments_collected_percent, progressPercent),
                remainingText = stringResource(
                    R.string.payments_remaining,
                    formatMoney(remaining),
                    currency
                ),
                paidChipText = stringResource(R.string.payments_chip_paid, paidCount),
                lateChipText = stringResource(R.string.payments_chip_late, lateCount),
                pendingChipText = stringResource(R.string.payments_chip_pending, pendingCount),
                progressContentDescription = progressA11y
            )
        }

        items(
            items = state.payments,
            key = { it.id }
        ) { payment ->
            val member = membersMap[payment.memberId]
            PaymentItem(
                modifier = Modifier.animateItem(
                    fadeInSpec = tween(280),
                    fadeOutSpec = tween(200)
                ),
                memberName = member?.name ?: stringResource(R.string.unknown_member),
                amount = payment.amount,
                status = payment.status,
                labels = paymentLabels,
                onMarkPaid = { viewModel.onMarkPaid(payment.id) }
            )
        }
    }
}

@Composable
private fun LatePaymentsBanner(
    title: String,
    body: String,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.errorContainer
        ),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(
            modifier = Modifier.padding(AppSpacing.sm),
            verticalArrangement = Arrangement.spacedBy(AppSpacing.xxs)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onErrorContainer
            )
            Text(
                text = body,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onErrorContainer
            )
        }
    }
}

@Composable
private fun CycleCompleteBanner(
    title: String,
    body: String,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.tertiaryContainer
        ),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(
            modifier = Modifier.padding(AppSpacing.sm),
            verticalArrangement = Arrangement.spacedBy(AppSpacing.xxs)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onTertiaryContainer
            )
            Text(
                text = body,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onTertiaryContainer
            )
        }
    }
}
