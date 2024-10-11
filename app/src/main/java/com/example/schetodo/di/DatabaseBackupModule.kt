package com.example.schetodo.di

import android.content.Context
import com.example.schetodo.data.DatabaseDao
import com.example.schetodo.feature.dbbackup.DatabaseBackupExporter
import com.example.schetodo.feature.dbbackup.DatabaseBackupImporter
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
object DatabaseBackupModule {
    @Singleton
    @Provides
    fun provideDatabaseBackupExporter(
        @ApplicationContext context: Context,
        databaseDao: DatabaseDao,
    ): DatabaseBackupExporter =
        DatabaseBackupExporter(
            context = context,
            databaseDao = databaseDao,
        )

    @Singleton
    @Provides
    fun provideDatabaseBackupImporter(
        @ApplicationContext context: Context,
    ): DatabaseBackupImporter =
        DatabaseBackupImporter(
            context = context,
        )
}
