package dev.sobhy.gameya.presentation.common

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import dev.sobhy.gameya.R
import dev.sobhy.gameya.ui.theme.AppSpacing
import dev.sobhy.gameya.ui.theme.StatusNeutral
import dev.sobhy.gameya.ui.theme.StatusSuccess
import dev.sobhy.gameya.ui.theme.StatusWarning
import java.text.DecimalFormat

enum class StatusTone {
    SUCCESS,
    WARNING,
    NEUTRAL
}

@Composable
fun SectionHeader(
    title: String,
    subtitle: String? = null
) {
    Column(verticalArrangement = Arrangement.spacedBy(AppSpacing.xxs)) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium
        )
        AnimatedVisibility(visible = subtitle != null) {
            subtitle?.let {
                Text(
                    text = it,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
fun EmptyStateCard(
    title: String,
    description: String
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(
            modifier = Modifier
                .padding(AppSpacing.md)
                .heightIn(min = 48.dp),
            verticalArrangement = Arrangement.spacedBy(AppSpacing.xs)
        ) {
            Text(title, style = MaterialTheme.typography.titleMedium)
            Text(
                text = description,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
fun StatusChip(
    label: String,
    tone: StatusTone,
    modifier: Modifier = Modifier
) {
    val toneColor by animateColorAsState(
        targetValue = when (tone) {
            StatusTone.SUCCESS -> StatusSuccess
            StatusTone.WARNING -> StatusWarning
            StatusTone.NEUTRAL -> StatusNeutral
        },
        animationSpec = tween(durationMillis = 320),
        label = "status_tone_color"
    )

    Text(
        text = label,
        modifier = modifier
            .background(
                color = toneColor.copy(alpha = 0.18f),
                shape = RoundedCornerShape(999.dp)
            )
            .padding(horizontal = 14.dp, vertical = 8.dp)
            .heightIn(min = 40.dp),
        color = toneColor,
        style = MaterialTheme.typography.labelLarge,
        fontWeight = FontWeight.Medium
    )
}

@Composable
fun MoneyText(
    amount: Double,
    modifier: Modifier = Modifier,
    emphasized: Boolean = false,
    prefix: String? = null
) {
    val currency = stringResource(R.string.currency_suffix)
    val text = buildString {
        if (prefix != null) {
            append(prefix)
            append(": ")
        }
        append(formatMoney(amount))
        append(" ")
        append(currency)
    }
    Text(
        text = text,
        modifier = modifier,
        style = if (emphasized) MaterialTheme.typography.titleLarge else MaterialTheme.typography.bodyLarge,
        fontWeight = if (emphasized) FontWeight.Bold else FontWeight.Medium
    )
}

@Composable
fun LedgerTotalsRow(
    total: Double,
    collected: Double,
    remaining: Double,
    progress: Float,
    progressPercent: Int,
    paidCount: Int,
    lateCount: Int,
    pendingCount: Int,
    title: String,
    subtitle: String,
    collectedPrefix: String,
    expectedPrefix: String,
    collectedPercentText: String,
    remainingText: String,
    paidChipText: String,
    lateChipText: String,
    pendingChipText: String,
    progressContentDescription: String,
    modifier: Modifier = Modifier
) {
    val trackColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.55f)
    val progressAnim by animateFloatAsState(
        targetValue = progress.coerceIn(0f, 1f),
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessMediumLow
        ),
        label = "collection_progress"
    )

    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier.padding(AppSpacing.sm),
            verticalArrangement = Arrangement.spacedBy(AppSpacing.xs)
        ) {
            SectionHeader(title = title, subtitle = subtitle)
            MoneyText(amount = collected, emphasized = true, prefix = collectedPrefix)
            MoneyText(amount = total, emphasized = false, prefix = expectedPrefix)
            Text(
                text = collectedPercentText,
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.onPrimaryContainer,
                fontWeight = FontWeight.SemiBold
            )
            LinearProgressIndicator(
                progress = { progressAnim },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(10.dp)
                    .semantics { contentDescription = progressContentDescription },
                strokeCap = StrokeCap.Round,
                color = MaterialTheme.colorScheme.primary,
                trackColor = trackColor
            )
            Text(
                text = remainingText,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )
            Row(
                horizontalArrangement = Arrangement.spacedBy(AppSpacing.xs),
                modifier = Modifier.fillMaxWidth()
            ) {
                StatusChip(label = paidChipText, tone = StatusTone.SUCCESS)
                StatusChip(label = lateChipText, tone = StatusTone.WARNING)
                StatusChip(label = pendingChipText, tone = StatusTone.NEUTRAL)
            }
        }
    }
}

private val formatter = DecimalFormat("#,##0.##")

fun formatMoney(amount: Double): String = formatter.format(amount)
