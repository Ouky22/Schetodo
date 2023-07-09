package com.example.schetodo.di

import com.example.schetodo.feature.schedule.notification.TodoBlockNotificationScheduler
import com.example.schetodo.feature.schedule.notification.TodoBlockNotificationSchedulerImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
interface NotificationSchedulerModule {
    @Singleton
    @Binds
    fun bindTodoBlockNotificationScheduler(todoBlockNotificationScheduler: TodoBlockNotificationSchedulerImpl): TodoBlockNotificationScheduler
}