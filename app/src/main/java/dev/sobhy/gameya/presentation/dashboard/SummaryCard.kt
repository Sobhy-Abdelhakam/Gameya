package dev.sobhy.gameya.presentation.dashboard

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import dev.sobhy.gameya.R
import dev.sobhy.gameya.presentation.common.MoneyText
import dev.sobhy.gameya.presentation.common.SectionHeader
import dev.sobhy.gameya.presentation.common.formatMoney
import dev.sobhy.gameya.ui.theme.AppSpacing
import androidx.compose.ui.unit.dp

@Composable
fun SummaryCard(summary: DashboardSummary) {
    Card(
        modifier = Modifier
            .fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        )
    ) {
        Column(modifier = Modifier.padding(AppSpacing.sm)) {
            val currency = stringResource(R.string.currency_suffix)
            SectionHeader(title = stringResource(R.string.dashboard_summary_title))
            MoneyText(
                amount = summary.totalMoney,
                emphasized = true,
                prefix = stringResource(R.string.prefix_monthly_inflow)
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = AppSpacing.xs),
                horizontalArrangement = Arrangement.spacedBy(AppSpacing.xs)
            ) {
                MetricCard(
                    title = stringResource(R.string.dashboard_summary_metric_groups),
                    value = "${summary.totalGroups}",
                    modifier = Modifier.weight(1f)
                )
                MetricCard(
                    title = stringResource(R.string.dashboard_summary_metric_inflow),
                    value = "${formatMoney(summary.totalMoney)} $currency",
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Composable
private fun MetricCard(
    title: String,
    value: String,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Column(modifier = Modifier.padding(AppSpacing.sm)) {
            Text(
                text = title,
                style = MaterialTheme.typography.labelMedium
            )
            Text(
                text = value,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}