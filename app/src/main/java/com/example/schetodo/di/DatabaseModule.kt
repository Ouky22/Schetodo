package com.example.schetodo.di

import android.content.Context
import com.example.schetodo.data.SchetodoDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
object DatabaseModule {
    @Singleton
    @Provides
    fun provideSchetodoDatabase(@ApplicationContext context: Context): SchetodoDatabase {
        return SchetodoDatabase.getInstance(context.applicationContext)
    }
}