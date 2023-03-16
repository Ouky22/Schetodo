package com.example.schetodo.di

import com.example.schetodo.data.notification.NotificationRepository
import com.example.schetodo.data.notification.NotificationRepositoryImpl
import com.example.schetodo.data.schedule_block.ScheduleBlockRepository
import com.example.schetodo.data.schedule_block.ScheduleBlockRepositoryImpl
import com.example.schetodo.data.todo_category.TodoCategoryRepository
import com.example.schetodo.data.todo_category.TodoCategoryRepositoryImpl
import com.example.schetodo.data.todo.TodoRepository
import com.example.schetodo.data.todo.TodoRepositoryImpl
import com.example.schetodo.data.todo_block.TodoBlockRepository
import com.example.schetodo.data.todo_block.TodoBlockRepositoryImpl
import com.example.schetodo.data.user_preferences.UserPreferencesRepository
import com.example.schetodo.data.user_preferences.UserPreferencesRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
interface RepositoryModule {

    @Singleton
    @Binds
    fun bindTodoRepository(todoRepository: TodoRepositoryImpl): TodoRepository

    @Singleton
    @Binds
    fun bindTodoCategoryRepository(todoCategoryRepositoryImpl: TodoCategoryRepositoryImpl): TodoCategoryRepository

    @Singleton
    @Binds
    fun bindTodoBlockRepository(todoBlockRepositoryImpl: TodoBlockRepositoryImpl): TodoBlockRepository

    @Singleton
    @Binds
    fun bindScheduleBlockRepository(scheduleBlockRepositoryImpl: ScheduleBlockRepositoryImpl): ScheduleBlockRepository

    @Singleton
    @Binds
    fun bindNotificationRepository(notificationRepository: NotificationRepositoryImpl): NotificationRepository

    @Singleton
    @Binds
    fun bindUserPreferencesRepository(userPreferencesRepositoryImpl: UserPreferencesRepositoryImpl): UserPreferencesRepository
}