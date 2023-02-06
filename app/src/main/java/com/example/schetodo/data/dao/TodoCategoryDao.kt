package com.example.schetodo.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.example.schetodo.data.entity.TodoCategory
import kotlinx.coroutines.flow.Flow

@Dao
interface TodoCategoryDao {

    @Insert
    suspend fun insertTodoCategory(todoCategory: TodoCategory): Long

    @Query("SELECT * FROM TodoCategory WHERE categoryId = :todoCategoryId")
    fun getTodoCategoryById(todoCategoryId: Int): Flow<TodoCategory?>

    @Query("SELECT * FROM TodoCategory WHERE parentTodoCategoryId IS NULL ORDER BY name ASC")
    fun getTopLevelTodoCategories(): Flow<List<TodoCategory>>

    @Query("SELECT * FROM TodoCategory WHERE parentTodoCategoryId = :todoCategoryId ORDER BY name ASC")
    fun getDirectChildTodoCategoriesOf(todoCategoryId: Int): Flow<List<TodoCategory>>

    @Delete
    suspend fun deleteTodoCategory(todoCategory: TodoCategory)
}