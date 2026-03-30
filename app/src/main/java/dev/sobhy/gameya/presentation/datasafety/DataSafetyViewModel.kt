package dev.sobhy.gameya.presentation.datasafety

import android.content.Context
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import dev.sobhy.gameya.domain.model.BackupInfo
import dev.sobhy.gameya.domain.model.CyclePayoutHistoryItem
import dev.sobhy.gameya.domain.model.PaymentAuditItem
import dev.sobhy.gameya.R
import dev.sobhy.gameya.domain.repository.DataSafetyRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.io.File
import javax.inject.Inject

data class DataSafetyUiState(
    val lastBackup: BackupInfo? = null,
    val paymentAudit: List<PaymentAuditItem> = emptyList(),
    val payoutHistory: List<CyclePayoutHistoryItem> = emptyList(),
    val message: String? = null,
    val isBusy: Boolean = false
)

@HiltViewModel
class DataSafetyViewModel @Inject constructor(
    private val dataSafetyRepository: DataSafetyRepository,
    @ApplicationContext private val context: Context
) : ViewModel() {

    private val _state = MutableStateFlow(DataSafetyUiState())
    val state: StateFlow<DataSafetyUiState> = _state.asStateFlow()

    init {
        viewModelScope.launch {
            dataSafetyRepository.observeLastBackup().collect { info ->
                _state.update { it.copy(lastBackup = info) }
            }
        }
        loadHistory()
    }

    fun loadHistory() {
        viewModelScope.launch {
            val payments = dataSafetyRepository.getPaymentAuditHistory()
            val payouts = dataSafetyRepository.getCyclePayoutHistory()
            _state.update {
                it.copy(
                    paymentAudit = payments,
                    payoutHistory = payouts
                )
            }
        }
    }

    fun clearMessage() {
        _state.update { it.copy(message = null) }
    }

    fun backupNow() {
        viewModelScope.launch {
            _state.update { it.copy(isBusy = true, message = null) }
            dataSafetyRepository.performBackup(isAutomatic = false).fold(
                onSuccess = { info ->
                    _state.update {
                        it.copy(
                            isBusy = false,
                            lastBackup = info,
                            message = context.getString(R.string.data_safety_backup_success)
                        )
                    }
                },
                onFailure = { e ->
                    _state.update {
                        it.copy(isBusy = false, message = e.message ?: "Backup failed")
                    }
                }
            )
        }
    }

    fun restoreFromUri(uri: Uri) {
        viewModelScope.launch {
            _state.update { it.copy(isBusy = true, message = null) }
            val json = context.contentResolver.openInputStream(uri)?.use { stream ->
                stream.bufferedReader().readText()
            }
            if (json == null) {
                _state.update { it.copy(isBusy = false, message = "Could not read file") }
                return@launch
            }
            dataSafetyRepository.restoreFromJsonString(json).fold(
                onSuccess = {
                    _state.update {
                        it.copy(
                            isBusy = false,
                            message = context.getString(R.string.data_safety_restore_success)
                        )
                    }
                    loadHistory()
                },
                onFailure = { e ->
                    _state.update {
                        it.copy(isBusy = false, message = e.message ?: "Restore failed")
                    }
                }
            )
        }
    }

    fun exportGroupsCsv() {
        viewModelScope.launch {
            _state.update { it.copy(isBusy = true) }
            val dir = exportDir()
            dir.mkdirs()
            val file = File(dir, "gameya_groups_${System.currentTimeMillis()}.csv")
            dataSafetyRepository.exportGroupsCsv(file).fold(
                onSuccess = {
                    _state.update {
                        it.copy(
                            isBusy = false,
                            message = context.getString(R.string.data_safety_export_saved, file.absolutePath)
                        )
                    }
                },
                onFailure = { e ->
                    _state.update {
                        it.copy(isBusy = false, message = e.message ?: "Export failed")
                    }
                }
            )
        }
    }

    fun exportPaymentsCsv() {
        viewModelScope.launch {
            _state.update { it.copy(isBusy = true) }
            val dir = exportDir()
            dir.mkdirs()
            val file = File(dir, "gameya_payments_${System.currentTimeMillis()}.csv")
            dataSafetyRepository.exportPaymentsCsv(file).fold(
                onSuccess = {
                    _state.update {
                        it.copy(
                            isBusy = false,
                            message = context.getString(R.string.data_safety_export_saved, file.absolutePath)
                        )
                    }
                },
                onFailure = { e ->
                    _state.update {
                        it.copy(isBusy = false, message = e.message ?: "Export failed")
                    }
                }
            )
        }
    }

    private fun exportDir(): File {
        val external = context.getExternalFilesDir(android.os.Environment.DIRECTORY_DOCUMENTS)
        return if (external != null) File(external, "Gameya/exports") else File(context.filesDir, "exports")
    }
}
