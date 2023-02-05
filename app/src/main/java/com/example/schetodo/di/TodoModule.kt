package com.example.schetodo.di

import com.example.schetodo.data.repository.TodoCategoryRepository
import com.example.schetodo.data.repository.TodoCategoryRepositoryImpl
import com.example.schetodo.data.repository.TodoRepository
import com.example.schetodo.data.repository.TodoRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
interface TodoModule {

    @Singleton
    @Binds
    fun bindTodoRepository(todoRepository: TodoRepositoryImpl): TodoRepository

    @Singleton
    @Binds
    fun bindTodoCategoryRepository(
        todoCategoryRepositoryImpl: TodoCategoryRepositoryImpl
    ): TodoCategoryRepository
}