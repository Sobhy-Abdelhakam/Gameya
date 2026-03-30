package dev.sobhy.gameya.di

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dev.sobhy.gameya.data.repository_impl.DataSafetyRepositoryImpl
import dev.sobhy.gameya.domain.repository.DataSafetyRepository
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class DataSafetyModule {

    @Binds
    @Singleton
    abstract fun bindDataSafetyRepository(impl: DataSafetyRepositoryImpl): DataSafetyRepository
}

@Module
@InstallIn(SingletonComponent::class)
object GsonModule {
    @Provides
    @Singleton
    fun provideGson(): Gson = GsonBuilder().setPrettyPrinting().create()
}
