package dev.sobhy.gameya.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dev.sobhy.gameya.data.repository_impl.GroupRepositoryImpl
import dev.sobhy.gameya.domain.repository.GroupRepository

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {
    @Binds
    abstract fun bindGroupRepository(impl: GroupRepositoryImpl): GroupRepository
}