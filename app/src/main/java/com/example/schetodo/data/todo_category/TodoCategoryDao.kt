package com.example.schetodo.data.todo_category

import androidx.room.*
import com.example.schetodo.data.TODO_CATEGORY_TABLE_NAME
import kotlinx.coroutines.flow.Flow

@Dao
interface TodoCategoryDao {

    @Insert
    suspend fun insertTodoCategory(todoCategory: TodoCategory): Long

    @Upsert
    suspend fun insertOrUpdateTodoCategory(todoCategory: TodoCategory)

    @Query("SELECT * FROM $TODO_CATEGORY_TABLE_NAME WHERE categoryId = :todoCategoryId")
    fun getTodoCategoryById(todoCategoryId: Int): Flow<TodoCategory?>

    @Query("SELECT * FROM $TODO_CATEGORY_TABLE_NAME WHERE parentTodoCategoryId IS NULL AND markedForDeletion = 0 ORDER BY name ASC")
    fun getTopLevelTodoCategories(): Flow<List<TodoCategory>>

    @Query("SELECT * FROM $TODO_CATEGORY_TABLE_NAME WHERE parentTodoCategoryId = :todoCategoryId AND (markedForDeletion = 0 OR :withMarkedForDeletion = 1) ORDER BY name ASC")
    fun getDirectChildTodoCategoriesOf(
        todoCategoryId: Int,
        withMarkedForDeletion: Boolean = false
    ): Flow<List<TodoCategory>>

    @Delete
    suspend fun deleteTodoCategory(todoCategory: TodoCategory)

    @Query("DELETE FROM $TODO_CATEGORY_TABLE_NAME WHERE categoryId = :todoCategoryId")
    suspend fun deleteTodoCategoryById(todoCategoryId: Int)

    @Query("UPDATE $TODO_CATEGORY_TABLE_NAME SET markedForDeletion = 1 WHERE categoryId = :todoCategoryId")
    suspend fun markTodoCategoryForDeletion(todoCategoryId: Int)

    @Query("UPDATE $TODO_CATEGORY_TABLE_NAME SET markedForDeletion = 0 WHERE categoryId = :todoCategoryId")
    suspend fun unmarkTodoCategoryForDeletion(todoCategoryId: Int)

    @Query("DELETE from $TODO_CATEGORY_TABLE_NAME WHERE markedForDeletion = 1")
    suspend fun deleteAllTodoCategoriesMarkedForDeletion()
}