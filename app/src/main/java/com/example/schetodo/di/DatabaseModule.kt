package com.example.schetodo.di

import android.content.Context
import com.example.schetodo.data.SchetodoDatabase
import com.example.schetodo.data.notification.NotificationDao
import com.example.schetodo.data.relationship.TodoBlockCategoryRelationshipDao
import com.example.schetodo.data.relationship.TodoBlockTodoRelationshipDao
import com.example.schetodo.data.schedule_block.ScheduleBlockDao
import com.example.schetodo.data.todo_category.TodoCategoryDao
import com.example.schetodo.data.todo.TodoDao
import com.example.schetodo.data.todo_block.TodoBlockDao
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

    @Singleton
    @Provides
    fun provideTodoBlockDao(schetodoDatabase: SchetodoDatabase): TodoBlockDao =
        schetodoDatabase.todoBlockDao

    @Singleton
    @Provides
    fun provideTodoBlockCategoryRelationshipDao(schetodoDatabase: SchetodoDatabase): TodoBlockCategoryRelationshipDao =
        schetodoDatabase.todoBlockCategoryRelationshipDao

    @Singleton
    @Provides
    fun provideTodoBlockTodoRelationshipDao(schetodoDatabase: SchetodoDatabase): TodoBlockTodoRelationshipDao =
        schetodoDatabase.todoBlockTodoRelationshipDao

    @Singleton
    @Provides
    fun provideScheduleBlockDao(schetodoDatabase: SchetodoDatabase): ScheduleBlockDao =
        schetodoDatabase.scheduleBlockDao

    @Singleton
    @Provides
    fun provideNotificationDao(schetodoDatabase: SchetodoDatabase): NotificationDao =
        schetodoDatabase.notificationDao
}