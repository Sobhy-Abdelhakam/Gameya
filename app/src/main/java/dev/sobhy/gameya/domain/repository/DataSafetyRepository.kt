package dev.sobhy.gameya.domain.repository

import dev.sobhy.gameya.domain.model.BackupInfo
import dev.sobhy.gameya.domain.model.CyclePayoutHistoryItem
import dev.sobhy.gameya.domain.model.PaymentAuditItem
import kotlinx.coroutines.flow.Flow
import java.io.File

interface DataSafetyRepository {

    suspend fun performBackup(isAutomatic: Boolean): Result<BackupInfo>

    suspend fun restoreFromJsonString(json: String): Result<Unit>

    fun observeLastBackup(): Flow<BackupInfo?>

    suspend fun exportGroupsCsv(targetFile: File): Result<Unit>

    suspend fun exportPaymentsCsv(targetFile: File): Result<Unit>

    suspend fun getPaymentAuditHistory(): List<PaymentAuditItem>

    suspend fun getCyclePayoutHistory(): List<CyclePayoutHistoryItem>
}
