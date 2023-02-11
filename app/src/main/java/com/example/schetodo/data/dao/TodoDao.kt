package com.example.schetodo.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Upsert
import com.example.schetodo.data.entity.Todo
import kotlinx.coroutines.flow.Flow

@Dao
interface TodoDao {
    @Insert
    suspend fun insertTodo(todo: Todo): Long

    @Upsert
    suspend fun insertOrUpdateTodo(todo: Todo): Long

    @Query("SELECT * FROM Todo WHERE todoId = :todoId")
    fun getTodoById(todoId: Int): Flow<Todo?>

    @Query("SELECT * FROM Todo")
    fun getAllTodos(): Flow<List<Todo>>

    @Query("SELECT * FROM Todo WHERE categoryId = :todoCategoryId ORDER BY priority ASC")
    fun getAllTodosOfTodoCategory(todoCategoryId: Int): Flow<List<Todo>>
}