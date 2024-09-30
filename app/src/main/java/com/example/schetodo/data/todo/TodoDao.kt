package com.example.schetodo.data.todo

import androidx.room.*
import com.example.schetodo.data.TODO_TABLE_NAME
import kotlinx.coroutines.flow.Flow

@Dao
interface TodoDao {
    @Insert
    suspend fun insertTodo(todo: Todo): Long

    @Upsert
    suspend fun insertOrUpdateTodo(todo: Todo): Long

    @Update
    suspend fun updateTodo(todo: Todo)

    @Update
    suspend fun updateTodos(todos: List<Todo>)

    @Query("DELETE FROM $TODO_TABLE_NAME WHERE todoId = :todoId")
    suspend fun deleteTodoById(todoId: Int)

    @Query("UPDATE $TODO_TABLE_NAME SET markedForDeletion = 1 WHERE todoId = :todoId")
    suspend fun markTodoForDeletion(todoId: Int)

    @Query("UPDATE $TODO_TABLE_NAME SET markedForDeletion = 0 WHERE todoId = :todoId")
    suspend fun unmarkTodoForDeletion(todoId: Int)

    @Query("UPDATE $TODO_TABLE_NAME SET markedForDeletion = 1 WHERE categoryId = :todoCategoryId")
    suspend fun markAllTodosOfCategoryForDeletion(todoCategoryId: Int)

    @Query("UPDATE $TODO_TABLE_NAME SET markedForDeletion = 0 WHERE categoryId = :todoCategoryId")
    suspend fun unmarkAllTodosOfCategoryForDeletion(todoCategoryId: Int)

    @Query("DELETE FROM $TODO_TABLE_NAME WHERE markedForDeletion = 1")
    suspend fun deleteAllTodosMarkedForDeletion()

    @Query("SELECT * FROM $TODO_TABLE_NAME WHERE todoId = :todoId")
    fun getTodoById(todoId: Int): Flow<Todo?>

    @Query("SELECT * FROM $TODO_TABLE_NAME")
    fun getAllTodos(): Flow<List<Todo>>

    @Query("SELECT * FROM $TODO_TABLE_NAME WHERE categoryId = :todoCategoryId AND markedForDeletion = 0 ORDER BY priority DESC")
    fun getAllTodosOfTodoCategory(todoCategoryId: Int): Flow<List<Todo>>

    @Query("SELECT * FROM $TODO_TABLE_NAME WHERE flag = :todoFlag AND markedForDeletion = 0 ORDER by priority DESC")
    fun getAllTodosWithFlag(todoFlag: TodoFlag): Flow<List<Todo>>
}