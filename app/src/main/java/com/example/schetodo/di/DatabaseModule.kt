package com.example.schetodo.di

import android.content.Context
import com.example.schetodo.data.SchetodoDatabase
import com.example.schetodo.data.dao.TodoCategoryDao
import com.example.schetodo.data.dao.TodoDao
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

    @Singleton
    @Provides
    fun provideTodoDao(schetodoDatabase: SchetodoDatabase): TodoDao = schetodoDatabase.todoDao

    @Singleton
    @Provides
    fun provideTodoCategoryDao(schetodoDatabase: SchetodoDatabase): TodoCategoryDao =
        schetodoDatabase.todoCategoryDao
}