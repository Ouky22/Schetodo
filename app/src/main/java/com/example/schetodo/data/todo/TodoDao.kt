package com.example.schetodo.data.todo

import androidx.room.*
import com.example.schetodo.data.todo.Todo
import com.example.schetodo.data.todo.TodoFlag
import kotlinx.coroutines.flow.Flow

@Dao
interface TodoDao {
    @Insert
    suspend fun insertTodo(todo: Todo): Long

    @Upsert
    suspend fun insertOrUpdateTodo(todo: Todo): Long

    @Update
    suspend fun updateTodo(todo: Todo)

    @Query("DELETE FROM Todo WHERE todoId = :todoId")
    suspend fun deleteTodoById(todoId: Int)

    @Query("UPDATE Todo SET markedForDeletion = 1 WHERE todoId = :todoId")
    suspend fun markTodoForDeletion(todoId: Int)

    @Query("UPDATE Todo SET markedForDeletion = 0 WHERE todoId = :todoId")
    suspend fun unmarkTodoForDeletion(todoId: Int)

    @Query("UPDATE Todo SET markedForDeletion = 1 WHERE categoryId = :todoCategoryId")
    suspend fun markAllTodosOfCategoryForDeletion(todoCategoryId: Int)

    @Query("UPDATE Todo SET markedForDeletion = 0 WHERE categoryId = :todoCategoryId")
    suspend fun unmarkAllTodosOfCategoryForDeletion(todoCategoryId: Int)

    @Query("DELETE FROM Todo WHERE markedForDeletion = 1")
    suspend fun deleteAllTodosMarkedForDeletion()

    @Query("SELECT * FROM Todo WHERE todoId = :todoId")
    fun getTodoById(todoId: Int): Flow<Todo?>

    @Query("SELECT * FROM Todo")
    fun getAllTodos(): Flow<List<Todo>>

    @Query("SELECT * FROM Todo WHERE categoryId = :todoCategoryId AND markedForDeletion = 0 ORDER BY priority DESC")
    fun getAllTodosOfTodoCategory(todoCategoryId: Int): Flow<List<Todo>>

    @Query("SELECT * FROM Todo WHERE flag = :todoFlag AND markedForDeletion = 0 ORDER by priority DESC")
    fun getAllTodosWithFlag(todoFlag: TodoFlag): Flow<List<Todo>>
}