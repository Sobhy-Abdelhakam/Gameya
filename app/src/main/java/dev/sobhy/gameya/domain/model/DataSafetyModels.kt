package dev.sobhy.gameya.domain.model

data class BackupInfo(
    val timestampMillis: Long,
    val filePath: String,
    val bytesWritten: Long,
    val isAutomatic: Boolean
)

data class PaymentAuditItem(
    val paymentId: Long,
    val groupName: String,
    val cycleIndex: Int,
    val memberName: String,
    val amount: Double,
    val status: String,
    val paidAtMillis: Long?
)

data class CyclePayoutHistoryItem(
    val cycleId: Long,
    val groupId: Long,
    val groupName: String,
    val cycleIndex: Int,
    val cycleDateMillis: Long,
    val payoutMemberName: String,
    val isClosed: Boolean
)
