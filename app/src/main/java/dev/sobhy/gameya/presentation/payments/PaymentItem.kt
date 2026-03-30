package dev.sobhy.gameya.presentation.payments

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.SizeTransform
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.BorderStroke
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import dev.sobhy.gameya.domain.enums.PaymentStatus
import dev.sobhy.gameya.presentation.common.MoneyText
import dev.sobhy.gameya.presentation.common.SectionHeader
import dev.sobhy.gameya.presentation.common.StatusChip
import dev.sobhy.gameya.presentation.common.StatusTone
import dev.sobhy.gameya.presentation.common.formatMoney
import dev.sobhy.gameya.ui.theme.AppSpacing

data class PaymentItemLabels(
    val memberContributionSubtitle: String,
    val contributionPrefix: String,
    val recordPayment: String,
    val paymentRecorded: String,
    val paid: String,
    val pending: String,
    val late: String,
)

@Composable
fun PaymentItem(
    memberName: String,
    amount: Double,
    status: PaymentStatus,
    labels: PaymentItemLabels,
    onMarkPaid: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val statusText = when (status) {
        PaymentStatus.PAID -> labels.paid
        PaymentStatus.UNPAID -> labels.pending
        PaymentStatus.LATE -> labels.late
    }
    val statusTone = when (status) {
        PaymentStatus.PAID -> StatusTone.SUCCESS
        PaymentStatus.UNPAID -> StatusTone.NEUTRAL
        PaymentStatus.LATE -> StatusTone.WARNING
    }

    val pressInteraction = remember { MutableInteractionSource() }
    val pressed by pressInteraction.collectIsPressedAsState()
    val buttonScale by animateFloatAsState(
        targetValue = if (pressed) 0.96f else 1f,
        animationSpec = spring(Spring.DampingRatioMediumBouncy, Spring.StiffnessMedium),
        label = "cta_scale"
    )

    val paidHighlight by animateFloatAsState(
        targetValue = if (status == PaymentStatus.PAID) 1f else 0f,
        animationSpec = tween(420, easing = FastOutSlowInEasing),
        label = "paid_highlight"
    )
    val borderTint = lerp(
        start = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f),
        stop = MaterialTheme.colorScheme.tertiary,
        fraction = paidHighlight
    )

    val accessibilitySummary = buildString {
        append(memberName)
        append(". ")
        append(labels.memberContributionSubtitle)
        append(". ")
        append(formatMoney(amount))
        append(". ")
        append(statusText)
    }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .animateContentSize()
            .padding(vertical = AppSpacing.xxs)
            .semantics { contentDescription = accessibilitySummary },
        elevation = CardDefaults.cardElevation(defaultElevation = if (paidHighlight > 0.5f) 3.dp else 1.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface,
        ),
        border = BorderStroke(
            width = 1.dp,
            color = borderTint
        )
    ) {
        Column(
            modifier = Modifier.padding(AppSpacing.sm)
        ) {
            SectionHeader(
                title = memberName,
                subtitle = labels.memberContributionSubtitle
            )
            Spacer(Modifier.height(AppSpacing.xs))
            MoneyText(amount = amount, emphasized = true, prefix = labels.contributionPrefix)
            Spacer(Modifier.height(AppSpacing.xs))

            AnimatedContent(
                targetState = status,
                transitionSpec = {
                    (
                        fadeIn(tween(240, easing = FastOutSlowInEasing)) +
                            scaleIn(initialScale = 0.92f, animationSpec = tween(240, easing = FastOutSlowInEasing))
                        ).togetherWith(
                        fadeOut(tween(200)) +
                            scaleOut(targetScale = 0.94f, animationSpec = tween(200))
                    ).using(SizeTransform(clip = false) { _, _ -> tween(240) })
                },
                label = "payment_status_slot"
            ) { targetStatus ->
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = if (targetStatus != PaymentStatus.PAID) {
                        Arrangement.SpaceBetween
                    } else {
                        Arrangement.Start
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    val chipText = when (targetStatus) {
                        PaymentStatus.PAID -> labels.paid
                        PaymentStatus.UNPAID -> labels.pending
                        PaymentStatus.LATE -> labels.late
                    }
                    val tone = when (targetStatus) {
                        PaymentStatus.PAID -> StatusTone.SUCCESS
                        PaymentStatus.UNPAID -> StatusTone.NEUTRAL
                        PaymentStatus.LATE -> StatusTone.WARNING
                    }
                    StatusChip(label = chipText, tone = tone)

                    if (targetStatus != PaymentStatus.PAID) {
                        Button(
                            onClick = onMarkPaid,
                            interactionSource = pressInteraction,
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.primary
                            ),
                            modifier = Modifier
                                .graphicsLayer {
                                    scaleX = buttonScale
                                    scaleY = buttonScale
                                }
                                .heightIn(min = 48.dp)
                                .semantics { contentDescription = labels.recordPayment }
                        ) {
                            Text(labels.recordPayment)
                        }
                    }
                }
            }

            AnimatedVisibility(visible = status == PaymentStatus.PAID) {
                Text(
                    text = labels.paymentRecorded,
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.tertiary,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.padding(top = AppSpacing.xs)
                )
            }
        }
    }
}
