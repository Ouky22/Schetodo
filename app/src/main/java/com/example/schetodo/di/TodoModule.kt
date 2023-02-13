package com.example.schetodo.di

import com.example.schetodo.data.todo_category.TodoCategoryRepository
import com.example.schetodo.data.todo_category.TodoCategoryRepositoryImpl
import com.example.schetodo.data.todo.TodoRepository
import com.example.schetodo.data.todo.TodoRepositoryImpl
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