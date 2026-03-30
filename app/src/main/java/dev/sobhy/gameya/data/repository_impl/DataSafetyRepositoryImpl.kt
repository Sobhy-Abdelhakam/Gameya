package dev.sobhy.gameya.data.repository_impl

import android.content.Context
import android.content.SharedPreferences
import androidx.room.withTransaction
import com.google.gson.Gson
import dev.sobhy.gameya.data.backup.BACKUP_SCHEMA_VERSION
import dev.sobhy.gameya.data.backup.BackupEnvelope
import dev.sobhy.gameya.data.backup.BackupPayload
import dev.sobhy.gameya.data.backup.CycleBackupDto
import dev.sobhy.gameya.data.backup.GroupBackupDto
import dev.sobhy.gameya.data.backup.MemberBackupDto
import dev.sobhy.gameya.data.backup.PaymentBackupDto
import dev.sobhy.gameya.data.backup.ShareBackupDto
import dev.sobhy.gameya.data.backup.sha256HexUtf8
import dev.sobhy.gameya.data.local.AppDatabase
import dev.sobhy.gameya.data.local.entity.CycleEntity
import dev.sobhy.gameya.data.local.entity.GroupEntity
import dev.sobhy.gameya.data.local.entity.MemberEntity
import dev.sobhy.gameya.data.local.entity.PaymentEntity
import dev.sobhy.gameya.data.local.entity.ShareEntity
import dev.sobhy.gameya.domain.model.BackupInfo
import dev.sobhy.gameya.domain.model.CyclePayoutHistoryItem
import dev.sobhy.gameya.domain.model.PaymentAuditItem
import dev.sobhy.gameya.domain.repository.DataSafetyRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStreamWriter
import java.nio.charset.StandardCharsets
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DataSafetyRepositoryImpl @Inject constructor(
    private val db: AppDatabase,
    @ApplicationContext private val context: Context,
    private val gson: Gson
) : DataSafetyRepository {

    /** Compact JSON for stable SHA-256 over payload (pretty-printed file can differ). */
    private val compactGson: Gson = Gson()

    private val prefs: SharedPreferences =
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    private val _lastBackup = MutableStateFlow<BackupInfo?>(null)

    init {
        _lastBackup.value = readLastBackupFromPrefs()
    }

    override fun observeLastBackup(): Flow<BackupInfo?> = _lastBackup.asStateFlow()

    override suspend fun performBackup(isAutomatic: Boolean): Result<BackupInfo> = runCatching {
        val groups = db.groupDao().getAllGroupsSnapshot().map { it.toDto() }
        val members = db.memberDao().getAllMembers().map { it.toDto() }
        val shares = db.shareDao().getAllShares().map { it.toDto() }
        val cycles = db.cycleDao().getAllCycles().map { it.toDto() }
        val payments = db.paymentDao().getAllPayments().map { it.toDto() }

        val payload = BackupPayload(groups, members, shares, cycles, payments)
        val payloadJson = compactGson.toJson(payload)
        val hash = sha256HexUtf8(payloadJson)
        val exportedAt = System.currentTimeMillis()
        val envelope = BackupEnvelope(
            exportedAtMillis = exportedAt,
            payloadSha256 = hash,
            payload = payload
        )
        val fullJson = gson.toJson(envelope)

        val dir = backupDirectory()
        dir.mkdirs()
        val fileName = if (isAutomatic) {
            "gameya_auto_backup.json"
        } else {
            "gameya_manual_backup_$exportedAt.json"
        }
        val file = File(dir, fileName)
        file.writeText(fullJson, StandardCharsets.UTF_8)

        val info = BackupInfo(
            timestampMillis = exportedAt,
            filePath = file.absolutePath,
            bytesWritten = file.length(),
            isAutomatic = isAutomatic
        )
        persistLastBackup(info)
        info
    }

    override suspend fun restoreFromJsonString(json: String): Result<Unit> = runCatching {
        val envelope = gson.fromJson(json, BackupEnvelope::class.java)
            ?: throw IllegalArgumentException("Invalid backup file")
        if (envelope.schemaVersion != BACKUP_SCHEMA_VERSION) {
            throw IllegalArgumentException("Unsupported backup schema: ${envelope.schemaVersion}")
        }
        val payloadJson = compactGson.toJson(envelope.payload)
        val expected = sha256HexUtf8(payloadJson)
        if (!expected.equals(envelope.payloadSha256, ignoreCase = true)) {
            throw IllegalArgumentException("Backup integrity check failed (checksum mismatch)")
        }
        val p = envelope.payload
        db.withTransaction {
            db.clearAllTables()
            p.groups.forEach { g ->
                db.groupDao().insertGroup(g.toEntity())
            }
            db.memberDao().insertMembers(p.members.map { it.toEntity() })
            db.shareDao().insertShares(p.shares.map { it.toEntity() })
            p.cycles.forEach { db.cycleDao().insertCycle(it.toEntity()) }
            db.paymentDao().insertPayments(p.payments.map { it.toEntity() })
        }
    }

    override suspend fun exportGroupsCsv(targetFile: File): Result<Unit> = runCatching {
        val groups = db.groupDao().getAllGroupsSnapshot()
        targetFile.parentFile?.mkdirs()
        OutputStreamWriter(FileOutputStream(targetFile), StandardCharsets.UTF_8).use { writer ->
            writer.write("\uFEFF")
            writer.appendLine("group_id,name,contribution_per_share,cycle_type,start_date_ms,created_at_ms")
            groups.forEach { g ->
                writer.appendLine(
                    listOf(
                        g.id.toString(),
                        escapeCsv(g.name),
                        g.contributionPerShare.toString(),
                        escapeCsv(g.cycleType),
                        g.startDate.toString(),
                        g.createdAt.toString()
                    ).joinToString(",")
                )
            }
        }
    }

    override suspend fun exportPaymentsCsv(targetFile: File): Result<Unit> = runCatching {
        val rows = db.paymentDao().getPaymentAuditHistory()
        targetFile.parentFile?.mkdirs()
        OutputStreamWriter(FileOutputStream(targetFile), StandardCharsets.UTF_8).use { writer ->
            writer.write("\uFEFF")
            writer.appendLine("payment_id,group_name,cycle_index,member_name,amount,status,paid_at_ms")
            rows.forEach { r ->
                writer.appendLine(
                    listOf(
                        r.paymentId.toString(),
                        escapeCsv(r.groupName),
                        r.cycleIndex.toString(),
                        escapeCsv(r.memberName),
                        r.amount.toString(),
                        escapeCsv(r.status),
                        r.paidAt?.toString() ?: ""
                    ).joinToString(",")
                )
            }
        }
    }

    override suspend fun getPaymentAuditHistory(): List<PaymentAuditItem> {
        return db.paymentDao().getPaymentAuditHistory().map {
            PaymentAuditItem(
                paymentId = it.paymentId,
                groupName = it.groupName,
                cycleIndex = it.cycleIndex,
                memberName = it.memberName,
                amount = it.amount,
                status = it.status,
                paidAtMillis = it.paidAt
            )
        }
    }

    override suspend fun getCyclePayoutHistory(): List<CyclePayoutHistoryItem> {
        return db.cycleDao().getCyclePayoutHistory().map {
            CyclePayoutHistoryItem(
                cycleId = it.cycleId,
                groupId = it.groupId,
                groupName = it.groupName,
                cycleIndex = it.cycleIndex,
                cycleDateMillis = it.cycleDate,
                payoutMemberName = it.payoutMemberName,
                isClosed = it.isClosed
            )
        }
    }

    private fun backupDirectory(): File {
        val external = context.getExternalFilesDir(android.os.Environment.DIRECTORY_DOCUMENTS)
        return if (external != null) {
            File(external, "Gameya/backups")
        } else {
            File(context.filesDir, "backups")
        }
    }

    private fun persistLastBackup(info: BackupInfo) {
        prefs.edit()
            .putLong(KEY_LAST_TS, info.timestampMillis)
            .putString(KEY_LAST_PATH, info.filePath)
            .putLong(KEY_LAST_BYTES, info.bytesWritten)
            .putBoolean(KEY_LAST_AUTO, info.isAutomatic)
            .apply()
        _lastBackup.value = info
    }

    private fun readLastBackupFromPrefs(): BackupInfo? {
        val ts = prefs.getLong(KEY_LAST_TS, 0L)
        if (ts == 0L) return null
        return BackupInfo(
            timestampMillis = ts,
            filePath = prefs.getString(KEY_LAST_PATH, "").orEmpty(),
            bytesWritten = prefs.getLong(KEY_LAST_BYTES, 0),
            isAutomatic = prefs.getBoolean(KEY_LAST_AUTO, false)
        )
    }

    private fun escapeCsv(value: String): String {
        val needsQuote = value.contains(',') || value.contains('"') || value.contains('\n') || value.contains('\r')
        val escaped = value.replace("\"", "\"\"")
        return if (needsQuote) "\"$escaped\"" else escaped
    }

    private companion object {
        const val PREFS_NAME = "data_safety"
        const val KEY_LAST_TS = "last_backup_ts"
        const val KEY_LAST_PATH = "last_backup_path"
        const val KEY_LAST_BYTES = "last_backup_bytes"
        const val KEY_LAST_AUTO = "last_backup_auto"
    }
}

private fun GroupEntity.toDto() = GroupBackupDto(
    id = id,
    name = name,
    contributionPerShare = contributionPerShare,
    cycleType = cycleType,
    startDate = startDate,
    createdAt = createdAt
)

private fun GroupBackupDto.toEntity() = GroupEntity(
    id = id,
    name = name,
    contributionPerShare = contributionPerShare,
    cycleType = cycleType,
    startDate = startDate,
    createdAt = createdAt
)

private fun MemberEntity.toDto() = MemberBackupDto(
    id = id,
    groupId = groupId,
    name = name,
    phone = phone,
    shares = shares
)

private fun MemberBackupDto.toEntity() = MemberEntity(
    id = id,
    groupId = groupId,
    name = name,
    phone = phone,
    shares = shares
)

private fun ShareEntity.toDto() = ShareBackupDto(
    id = id,
    groupId = groupId,
    memberId = memberId,
    fraction = fraction,
    orderIndex = orderIndex
)

private fun ShareBackupDto.toEntity() = ShareEntity(
    id = id,
    groupId = groupId,
    memberId = memberId,
    fraction = fraction,
    orderIndex = orderIndex
)

private fun CycleEntity.toDto() = CycleBackupDto(
    id = id,
    groupId = groupId,
    cycleIndex = cycleIndex,
    date = date,
    payoutShareId = payoutShareId,
    isClosed = isClosed
)

private fun CycleBackupDto.toEntity() = CycleEntity(
    id = id,
    groupId = groupId,
    cycleIndex = cycleIndex,
    date = date,
    payoutShareId = payoutShareId,
    isClosed = isClosed
)

private fun PaymentEntity.toDto() = PaymentBackupDto(
    id = id,
    cycleId = cycleId,
    memberId = memberId,
    amount = amount,
    status = status,
    paidAt = paidAt
)

private fun PaymentBackupDto.toEntity() = PaymentEntity(
    id = id,
    cycleId = cycleId,
    memberId = memberId,
    amount = amount,
    status = status,
    paidAt = paidAt
)
