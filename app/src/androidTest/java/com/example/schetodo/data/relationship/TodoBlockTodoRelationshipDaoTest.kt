package com.example.schetodo.data.relationship

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.example.schetodo.data.SchetodoDatabase
import com.example.schetodo.data.todo.Todo
import com.example.schetodo.data.todo.TodoDao
import com.example.schetodo.data.todo.TodoFlag
import com.example.schetodo.data.todo.TodoPriority
import com.example.schetodo.data.todo_block.TodoBlock
import com.example.schetodo.data.todo_block.TodoBlockDao
import com.example.schetodo.data.todo_category.TodoCategory
import com.example.schetodo.data.todo_category.TodoCategoryDao
import com.google.common.truth.Truth
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Before
import org.junit.Test
import java.io.IOException
import java.time.LocalTime

@OptIn(ExperimentalCoroutinesApi::class)
class TodoBlockTodoRelationshipDaoTest {
    private lateinit var todoBlockDao: TodoBlockDao
    private lateinit var todoDao: TodoDao
    private lateinit var todoCategoryDao: TodoCategoryDao
    private lateinit var todoBlockTodoRelationshipDao: TodoBlockTodoRelationshipDao
    private lateinit var db: SchetodoDatabase

    @Before
    fun createDb() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(context, SchetodoDatabase::class.java).build()
        todoDao = db.todoDao
        todoCategoryDao = db.todoCategoryDao
        todoBlockDao = db.todoBlockDao
        todoBlockTodoRelationshipDao = db.todoBlockTodoRelationshipDao
    }

    @After
    @Throws(IOException::class)
    fun closeDb() {
        db.close()
    }

    @Test
    fun test_disconnecting_all_todo_categories_of_todo_block() = runTest {
        val todoCategory = TodoCategory(1, "c1", 0, null, "icon")
        val todo1 = Todo(1, "", TodoPriority.MEDIUM, TodoFlag.IN_PROGRESS, todoCategory.categoryId)
        val todo2 = Todo(2, "", TodoPriority.LOW, TodoFlag.DONE, todoCategory.categoryId)
        val time = LocalTime.now()
        val todoBlock1 = TodoBlock(1, "n", null, time, time.plusHours(2), null)
        val todoBlock2 = TodoBlock(2, "n", null, time, time.plusHours(2), null)

        todoCategoryDao.insertTodoCategory(todoCategory)
        todoDao.insertTodo(todo1)
        todoDao.insertTodo(todo2)
        todoBlockDao.insertTodoBlock(todoBlock1)
        todoBlockDao.insertTodoBlock(todoBlock2)
        listOf(todo1, todo2).forEach {
            todoBlockTodoRelationshipDao.connectTodoBlockAndTodo(
                todoBlock1.todoBlockId, it.todoId
            )
        }
        todoBlockTodoRelationshipDao.connectTodoBlockAndTodo(
            todoBlock2.todoBlockId, todo1.todoId
        )

        todoBlockTodoRelationshipDao.disconnectAllTodosFromTodoBlock(todoBlock1.todoBlockId)

        val relationships = todoBlockTodoRelationshipDao.getAllTodoBlockTodoRelationships().first()
        Truth.assertThat(relationships).containsExactly(
            TodoBlockTodoRelationship(todoBlock2.todoBlockId, todo1.todoId)
        )
    }

    @Test
    fun test_connecting_todo_block_and_todo_categories() = runTest {
        val todoCategory = TodoCategory(1, "c1", 0, null, "icon")
        val todo1 = Todo(1, "", TodoPriority.MEDIUM, TodoFlag.IN_PROGRESS, todoCategory.categoryId)
        val todo2 = Todo(2, "", TodoPriority.LOW, TodoFlag.DONE, todoCategory.categoryId)
        val time = LocalTime.now()
        val todoBlock = TodoBlock(1, "n", null, time, time.plusHours(2), null)

        todoCategoryDao.insertTodoCategory(todoCategory)
        todoDao.insertTodo(todo1)
        todoDao.insertTodo(todo2)
        todoBlockDao.insertTodoBlock(todoBlock)
        listOf(todo1, todo2).forEach {
            todoBlockTodoRelationshipDao.connectTodoBlockAndTodo(
                todoBlock.todoBlockId, it.todoId
            )
        }

        val relationships = todoBlockTodoRelationshipDao.getAllTodoBlockTodoRelationships().first()
        Truth.assertThat(relationships).containsExactly(
            TodoBlockTodoRelationship(todoBlock.todoBlockId, todo1.todoId),
            TodoBlockTodoRelationship(todoBlock.todoBlockId, todo2.todoId)
        )
    }
}