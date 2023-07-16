package com.example.schetodo.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import javax.inject.Qualifier
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
object CoroutineScopeModule {

    @Retention(AnnotationRetention.RUNTIME)
    @Qualifier
    annotation class ApplicationCoroutineScope

    @Singleton
    @ApplicationCoroutineScope
    @Provides
    fun provideApplicationCoroutineScope(): CoroutineScope = CoroutineScope(SupervisorJob())
}