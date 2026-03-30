package dev.sobhy.gameya.data.backup

import com.google.gson.annotations.SerializedName

internal const val BACKUP_SCHEMA_VERSION = 1

internal data class BackupEnvelope(
    @SerializedName("schemaVersion")
    val schemaVersion: Int = BACKUP_SCHEMA_VERSION,
    @SerializedName("exportedAtMillis")
    val exportedAtMillis: Long,
    @SerializedName("payloadSha256")
    val payloadSha256: String,
    @SerializedName("payload")
    val payload: BackupPayload
)

internal data class BackupPayload(
    @SerializedName("groups")
    val groups: List<GroupBackupDto>,
    @SerializedName("members")
    val members: List<MemberBackupDto>,
    @SerializedName("shares")
    val shares: List<ShareBackupDto>,
    @SerializedName("cycles")
    val cycles: List<CycleBackupDto>,
    @SerializedName("payments")
    val payments: List<PaymentBackupDto>
)

internal data class GroupBackupDto(
    @SerializedName("id") val id: Long,
    @SerializedName("name") val name: String,
    @SerializedName("contributionPerShare") val contributionPerShare: Double,
    @SerializedName("cycleType") val cycleType: String,
    @SerializedName("startDate") val startDate: Long,
    @SerializedName("createdAt") val createdAt: Long
)

internal data class MemberBackupDto(
    @SerializedName("id") val id: Long,
    @SerializedName("groupId") val groupId: Long,
    @SerializedName("name") val name: String,
    @SerializedName("phone") val phone: String,
    @SerializedName("shares") val shares: Double
)

internal data class ShareBackupDto(
    @SerializedName("id") val id: Long,
    @SerializedName("groupId") val groupId: Long,
    @SerializedName("memberId") val memberId: Long,
    @SerializedName("fraction") val fraction: Double,
    @SerializedName("orderIndex") val orderIndex: Int
)

internal data class CycleBackupDto(
    @SerializedName("id") val id: Long,
    @SerializedName("groupId") val groupId: Long,
    @SerializedName("cycleIndex") val cycleIndex: Int,
    @SerializedName("date") val date: Long,
    @SerializedName("payoutShareId") val payoutShareId: Long,
    @SerializedName("isClosed") val isClosed: Boolean
)

internal data class PaymentBackupDto(
    @SerializedName("id") val id: Long,
    @SerializedName("cycleId") val cycleId: Long,
    @SerializedName("memberId") val memberId: Long,
    @SerializedName("amount") val amount: Double,
    @SerializedName("status") val status: String,
    @SerializedName("paidAt") val paidAt: Long?
)
