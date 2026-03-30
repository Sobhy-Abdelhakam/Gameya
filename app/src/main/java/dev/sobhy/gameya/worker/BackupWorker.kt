package dev.sobhy.gameya.worker

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import dagger.hilt.android.EntryPointAccessors
import dev.sobhy.gameya.domain.repository.DataSafetyRepository

class BackupWorker(
    context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        val repo = repository()
        return try {
            repo.performBackup(isAutomatic = true).getOrThrow()
            Result.success()
        } catch (_: Exception) {
            if (runAttemptCount < 3) Result.retry() else Result.failure()
        }
    }

    private fun repository(): DataSafetyRepository {
        val entryPoint = EntryPointAccessors.fromApplication(
            applicationContext,
            DataSafetyWorkerEntryPoint::class.java
        )
        return entryPoint.dataSafetyRepository()
    }
}
