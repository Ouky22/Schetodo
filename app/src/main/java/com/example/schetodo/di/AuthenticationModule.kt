package com.example.schetodo.di

import android.content.Context
import com.example.schetodo.feature.authentication.AuthenticationService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
object AuthenticationModule {
    @Singleton
    @Provides
    fun provideAuthenticationService(
        @ApplicationContext context: Context
    ): AuthenticationService = AuthenticationService(
        context,
    )
}
