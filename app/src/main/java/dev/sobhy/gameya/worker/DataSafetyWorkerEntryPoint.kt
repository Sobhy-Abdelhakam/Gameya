package dev.sobhy.gameya.worker

import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dev.sobhy.gameya.domain.repository.DataSafetyRepository

@EntryPoint
@InstallIn(SingletonComponent::class)
interface DataSafetyWorkerEntryPoint {
    fun dataSafetyRepository(): DataSafetyRepository
}
