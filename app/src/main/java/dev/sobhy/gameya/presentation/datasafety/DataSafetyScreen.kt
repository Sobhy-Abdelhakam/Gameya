package dev.sobhy.gameya.presentation.datasafety

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.compose.runtime.collectAsState
import dev.sobhy.gameya.R
import dev.sobhy.gameya.domain.model.BackupInfo
import dev.sobhy.gameya.presentation.common.formatMoney
import dev.sobhy.gameya.ui.theme.AppSpacing
import androidx.navigation.NavController
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.concurrent.TimeUnit

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DataSafetyScreen(
    navController: NavController,
    viewModel: DataSafetyViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    val context = LocalContext.current
    var showRestoreDialog by remember { mutableStateOf(false) }

    val openBackupFile = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument()
    ) { uri ->
        uri?.let { viewModel.restoreFromUri(it) }
    }

    LaunchedEffect(state.message) {
        val msg = state.message ?: return@LaunchedEffect
        snackbarHostState.showSnackbar(msg)
        viewModel.clearMessage()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.data_safety_title)) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = null
                        )
                    }
                }
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { padding ->
        Box(
            Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            if (state.isBusy) {
                CircularProgressIndicator(
                    Modifier
                        .align(Alignment.Center)
                        .padding(AppSpacing.md)
                )
            }

            LazyColumn(
                contentPadding = PaddingValues(AppSpacing.sm),
                verticalArrangement = Arrangement.spacedBy(AppSpacing.sm),
                modifier = Modifier.fillMaxSize()
            ) {
                item {
                    Text(
                        stringResource(R.string.data_safety_section_backup),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                    BackupStatusCard(state.lastBackup)
                    Button(
                        onClick = { viewModel.backupNow() },
                        enabled = !state.isBusy,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = AppSpacing.xs)
                    ) {
                        Text(stringResource(R.string.data_safety_backup_now))
                    }
                    OutlinedButton(
                        onClick = { showRestoreDialog = true },
                        enabled = !state.isBusy,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(stringResource(R.string.data_safety_restore))
                    }
                }

                item { HorizontalDivider() }

                item {
                    Text(
                        stringResource(R.string.data_safety_section_export),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                    Button(
                        onClick = { viewModel.exportGroupsCsv() },
                        enabled = !state.isBusy,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(stringResource(R.string.data_safety_export_groups_csv))
                    }
                    OutlinedButton(
                        onClick = { viewModel.exportPaymentsCsv() },
                        enabled = !state.isBusy,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(stringResource(R.string.data_safety_export_payments_csv))
                    }
                }

                item { HorizontalDivider() }

                item {
                    Text(
                        stringResource(R.string.data_safety_section_history),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(
                        stringResource(R.string.data_safety_payments_audit_title),
                        style = MaterialTheme.typography.labelLarge,
                        modifier = Modifier.padding(top = AppSpacing.xs)
                    )
                }
                items(state.paymentAudit) { row ->
                    Column(Modifier.padding(vertical = AppSpacing.xxs)) {
                        Text(
                            "${row.groupName} · cycle ${row.cycleIndex} · ${row.memberName}",
                            style = MaterialTheme.typography.bodyMedium
                        )
                        Text(
                            "${row.status} · ${formatMoney(row.amount)}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            if (row.paidAtMillis != null) {
                                stringResource(
                                    R.string.data_safety_paid_at,
                                    formatDateTime(row.paidAtMillis)
                                )
                            } else {
                                stringResource(R.string.data_safety_not_paid_yet)
                            },
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }

                item {
                    Text(
                        stringResource(R.string.data_safety_payout_history_title),
                        style = MaterialTheme.typography.labelLarge,
                        modifier = Modifier.padding(top = AppSpacing.sm)
                    )
                }
                items(state.payoutHistory) { row ->
                    Text(
                        stringResource(
                            R.string.data_safety_payout_line,
                            row.cycleIndex + 1,
                            row.groupName,
                            formatDateTime(row.cycleDateMillis),
                            row.payoutMemberName
                        ),
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.padding(vertical = AppSpacing.xxs)
                    )
                }
            }
        }
    }

    if (showRestoreDialog) {
        AlertDialog(
            onDismissRequest = { showRestoreDialog = false },
            title = { Text(stringResource(R.string.data_safety_restore_title)) },
            text = { Text(stringResource(R.string.data_safety_restore_body)) },
            confirmButton = {
                TextButton(
                    onClick = {
                        showRestoreDialog = false
                        openBackupFile.launch(arrayOf("application/json", "*/*"))
                    }
                ) {
                    Text(stringResource(R.string.data_safety_restore_confirm))
                }
            },
            dismissButton = {
                TextButton(onClick = { showRestoreDialog = false }) {
                    Text(stringResource(R.string.data_safety_restore_dismiss))
                }
            }
        )
    }
}

@Composable
private fun BackupStatusCard(info: BackupInfo?) {
    val context = LocalContext.current
    val text = if (info == null) {
        stringResource(R.string.data_safety_last_backup_never)
    } else {
        stringResource(
            R.string.data_safety_last_backup_relative,
            formatRelativeBackupTime(context, info.timestampMillis)
        )
    }
    Text(
        text,
        style = MaterialTheme.typography.bodyMedium,
        color = if (info == null) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurface
    )
}

private fun formatRelativeBackupTime(context: android.content.Context, millis: Long): String {
    val diff = System.currentTimeMillis() - millis
    val minutes = TimeUnit.MILLISECONDS.toMinutes(diff).toInt().coerceAtLeast(0)
    return when {
        minutes < 1 -> context.getString(R.string.data_safety_time_just_now)
        minutes < 60 -> context.getString(R.string.data_safety_time_minutes_ago, minutes)
        minutes < 24 * 60 -> {
            val h = minutes / 60
            context.getString(R.string.data_safety_time_hours_ago, h)
        }
        else -> {
            val d = minutes / (24 * 60)
            context.getString(R.string.data_safety_time_days_ago, d)
        }
    }
}

private fun formatDateTime(millis: Long): String {
    val fmt = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())
    return fmt.format(Date(millis))
}
