package dev.sobhy.gameya.presentation.payments

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import dev.sobhy.gameya.domain.enums.PaymentStatus

@Composable
fun PaymentItem(
    memberName: String,
    amount: Double,
    status: PaymentStatus,
    onMarkPaid: () -> Unit
) {

    val statusColor = when (status) {
        PaymentStatus.PAID -> Color(0xFF4CAF50)
        PaymentStatus.UNPAID -> Color.Gray
        PaymentStatus.LATE -> Color(0xFFF44336)
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {

        Column(
            modifier = Modifier.padding(16.dp)
        ) {

            // 👤 Name
            Text(
                text = memberName,
                style = MaterialTheme.typography.titleMedium
            )

            Spacer(Modifier.height(4.dp))

            // 💰 Amount
            Text(
                text = "Amount: $amount",
                style = MaterialTheme.typography.bodyMedium
            )

            Spacer(Modifier.height(8.dp))

            // 🟢 Status Chip
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {

                Box(
                    modifier = Modifier
                        .background(
                            color = statusColor.copy(alpha = 0.2f),
                            shape = RoundedCornerShape(12.dp)
                        )
                        .padding(horizontal = 12.dp, vertical = 6.dp)
                ) {
                    Text(
                        text = when (status) {
                            PaymentStatus.PAID -> "Paid ✅"
                            PaymentStatus.UNPAID -> "Pending"
                            PaymentStatus.LATE -> "Late ⚠️"
                        },
                        color = statusColor
                    )
                }

                // 💰 Button
                if (status != PaymentStatus.PAID) {
                    Button(onClick = onMarkPaid) {
                        Text("Mark Paid")
                    }
                }
            }
        }
    }
}