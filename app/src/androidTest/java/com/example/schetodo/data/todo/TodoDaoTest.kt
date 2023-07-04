package com.example.schetodo.data.todo

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import app.cash.turbine.test
import com.example.schetodo.data.SchetodoDatabase
import com.example.schetodo.data.todo_category.TodoCategoryDao
import com.example.schetodo.data.todo_category.TodoCategory
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Before
import org.junit.Test
import java.io.IOException

@ExperimentalCoroutinesApi
class TodoDaoTest {
    private lateinit var todoDao: TodoDao
    private lateinit var todoCategoryDao: TodoCategoryDao
    private lateinit var db: SchetodoDatabase

    @Before
    fun createDb() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(context, SchetodoDatabase::class.java).build()
        todoDao = db.todoDao
        todoCategoryDao = db.todoCategoryDao
    }

    @After
    @Throws(IOException::class)
    fun closeDb() {
        db.close()
    }

    @Test
    fun when_getting_todos_then_todos_marked_for_deletion_are_not_returned() = runTest {
        val category = TodoCategory(1, "c1", 0, null, "")
        val todo1 = Todo(1, "t1", TodoPriority.HIGH, TodoFlag.DONE, category.categoryId)
        val todo2 = Todo(2, "t2", TodoPriority.LOW, TodoFlag.DONE, category.categoryId)
        val todo3 = Todo(3, "t3", TodoPriority.LOW, TodoFlag.DONE, category.categoryId)
        todoCategoryDao.insertTodoCategory(category)
        todoDao.insertTodo(todo1)
        todoDao.insertTodo(todo2)
        todoDao.insertTodo(todo3)

        todoDao.markTodoForDeletion(todo1.todoId)

        assertThat(todoDao.getAllTodosOfTodoCategory(1).first())
            .containsExactly(todo2, todo3)
        assertThat(todoDao.getAllTodosWithFlag(TodoFlag.DONE).first())
            .containsExactly(todo2, todo3)
    }

    @Test
    fun test_mark_all_todos_of_todo_category_for_deletion() = runTest {
        val category = TodoCategory(1, "c1", 0, null, "")
        val todo1 = Todo(1, "test 1", TodoPriority.LOW, TodoFlag.IN_PROGRESS, category.categoryId)
        val todo2 = Todo(
            2,
            "test 2",
            TodoPriority.HIGH,
            TodoFlag.IN_PROGRESS,
            category.categoryId,
            markedForDeletion = true
        )
        todoCategoryDao.insertTodoCategory(category)
        todoDao.insertTodo(todo1)
        todoDao.insertTodo(todo2)

        todoDao.markAllTodosOfCategoryForDeletion(category.categoryId)

        todoDao.getAllTodosOfTodoCategory(category.categoryId).first().forEach {
            assertThat(it.markedForDeletion).isTrue()
        }
    }

    @Test
    fun test_unmark_all_todos_of_todo_category_for_deletion() = runTest {
        val category = TodoCategory(1, "c1", 0, null, "")
        val todo1 = Todo(1, "test 1", TodoPriority.LOW, TodoFlag.IN_PROGRESS, category.categoryId)
        val todo2 = Todo(
            2,
            "test 2",
            TodoPriority.HIGH,
            TodoFlag.IN_PROGRESS,
            category.categoryId,
            markedForDeletion = true
        )
        todoCategoryDao.insertTodoCategory(category)
        todoDao.insertTodo(todo1)
        todoDao.insertTodo(todo2)

        todoDao.unmarkAllTodosOfCategoryForDeletion(category.categoryId)

        todoDao.getAllTodosOfTodoCategory(category.categoryId).first().forEach {
            assertThat(it.markedForDeletion).isFalse()
        }
    }

    @Test
    fun test_mark_todo_for_deletion() = runTest {
        val category = TodoCategory(1, "c1", 0, null, "")
        val todo1 = Todo(1, "test 1", TodoPriority.LOW, TodoFlag.IN_PROGRESS, category.categoryId)
        val todo2 = Todo(2, "test 2", TodoPriority.HIGH, TodoFlag.IN_PROGRESS, category.categoryId)
        todoCategoryDao.insertTodoCategory(category)
        todoDao.insertTodo(todo1)
        todoDao.insertTodo(todo2)

        todoDao.markTodoForDeletion(todo1.todoId)

        assertThat(todoDao.getTodoById(todo1.todoId).first()?.markedForDeletion).isTrue()
        assertThat(todoDao.getTodoById(todo2.todoId).first()?.markedForDeletion).isFalse()
    }

    @Test
    fun test_unmark_todo_for_deletion() = runTest {
        val category = TodoCategory(1, "c1", 0, null, "")
        val todo1 = Todo(
            1,
            "test 1",
            TodoPriority.LOW,
            TodoFlag.IN_PROGRESS,
            category.categoryId,
            markedForDeletion = true
        )
        val todo2 = Todo(
            2,
            "test 2",
            TodoPriority.HIGH,
            TodoFlag.IN_PROGRESS,
            category.categoryId,
            markedForDeletion = true
        )
        todoCategoryDao.insertTodoCategory(category)
        todoDao.insertTodo(todo1)
        todoDao.insertTodo(todo2)

        todoDao.unmarkTodoForDeletion(todo1.todoId)

        assertThat(todoDao.getTodoById(todo1.todoId).first()?.markedForDeletion).isFalse()
        assertThat(todoDao.getTodoById(todo2.todoId).first()?.markedForDeletion).isTrue()
    }

    @Test
    fun test_deleting_all_todos_marked_for_deletion() = runTest {
        val category = TodoCategory(1, "c1", 0, null, "")
        val todo1 = Todo(1, "test 1", TodoPriority.LOW, TodoFlag.IN_PROGRESS, category.categoryId)
        val todo2 = Todo(2, "test 2", TodoPriority.HIGH, TodoFlag.IN_PROGRESS, category.categoryId)
        todoCategoryDao.insertTodoCategory(category)
        todoDao.insertTodo(todo1)
        todoDao.insertTodo(todo2)

        todoDao.markTodoForDeletion(todo1.todoId)
        todoDao.unmarkTodoForDeletion(todo2.todoId)
        todoDao.deleteAllTodosMarkedForDeletion()

        assertThat(todoDao.getAllTodos().first()).containsExactly(todo2)
    }

    @Test
    fun test_getting_todos_in_progress() = runTest {
        val category = TodoCategory(1, "c1", 0, null, "")
        val todo1 = Todo(1, "test 1", TodoPriority.LOW, TodoFlag.IN_PROGRESS, category.categoryId)
        val todo2 = Todo(2, "test 2", TodoPriority.HIGH, TodoFlag.IN_PROGRESS, category.categoryId)
        val todo3 = Todo(3, "test 3", TodoPriority.MEDIUM, TodoFlag.UNDONE, category.categoryId)
        val todo4 = Todo(4, "test 4", TodoPriority.VERY_HIGH, TodoFlag.DONE, category.categoryId)
        todoCategoryDao.insertTodoCategory(category)
        todoDao.insertTodo(todo1)
        todoDao.insertTodo(todo2)
        todoDao.insertTodo(todo3)
        todoDao.insertTodo(todo4)

        val todosInProgress = todoDao.getAllTodosWithFlag(TodoFlag.IN_PROGRESS).first()
        assertThat(todosInProgress.size).isEqualTo(2)
        assertThat(todosInProgress.contains(todo1)).isTrue()
        assertThat(todosInProgress.contains(todo2)).isTrue()
    }

    @Test
    fun when_deleting_todo_by_id_and_id_not_exists_do_nothing() = runTest {
        todoDao.deleteTodoById(1)
    }

    @Test
    fun when_deleting_todo_by_id_and_id_exists_delete_it() = runTest {
        val category = TodoCategory(1, "c1", 0, null, "")
        val todo = Todo(1, "test", TodoPriority.MEDIUM, TodoFlag.UNDONE, category.categoryId)
        todoCategoryDao.insertTodoCategory(category)
        todoDao.insertTodo(todo)

        todoDao.deleteTodoById(todo.todoId)

        assertThat(todoDao.getTodoById(todo.todoId).first()).isNull()
    }

    @Test
    fun when_getting_todo_by_id_and_id_not_exists_then_return_null() = runTest {
        assertThat(todoDao.getTodoById(1).first()).isNull()
    }

    @Test
    fun when_getting_todo_by_id_and_id_exists_then_return_the_todo() = runTest {
        val category = TodoCategory(1, "c1", 0, null, "")
        val todo = Todo(1, "test", TodoPriority.MEDIUM, TodoFlag.UNDONE, category.categoryId)
        todoCategoryDao.insertTodoCategory(category)
        todoDao.insertTodo(todo)

        assertThat(todoDao.getTodoById(todo.todoId).first()).isEqualTo(todo)
    }

    @Test
    fun test_get_all_todos_of_todo_category() = runTest {
        val category1 = TodoCategory(1, "c1", 0, null, "")
        val category2 = TodoCategory(2, "c2", 0, 1, "")
        todoCategoryDao.insertTodoCategory(category1)
        todoCategoryDao.insertTodoCategory(category2)

        val todo1 = Todo(1, "t1", TodoPriority.MEDIUM, TodoFlag.UNDONE, 1)
        val todo2 = Todo(2, "t2", TodoPriority.HIGH, TodoFlag.IN_PROGRESS, 1)
        val todo3 = Todo(3, "t3", TodoPriority.LOW, TodoFlag.DONE, 2)
        todoDao.insertTodo(todo1)
        todoDao.insertTodo(todo2)
        todoDao.insertTodo(todo3)

        todoDao.getAllTodosOfTodoCategory(category1.categoryId).test {
            val todos = awaitItem()
            assertThat(todos.size).isEqualTo(2)
            // todo2 has highest priority and therefore should be the first in the list
            assertThat(todos.first()).isEqualTo(todo2)
            assertThat(todos).contains(todo1)
        }
        todoDao.getAllTodosOfTodoCategory(category2.categoryId).test {
            val todos = awaitItem()
            assertThat(todos.size).isEqualTo(1)
            assertThat(todos).contains(todo3)
        }
    }

    @Test
    fun when_deleting_category_then_all_containing_todos_are_deleted() = runTest {
        val category1 = TodoCategory(1, "c1", 0, null, "")
        val category2 = TodoCategory(2, "c2", 0, 1, "")
        todoCategoryDao.insertTodoCategory(category1)
        todoCategoryDao.insertTodoCategory(category2)

        val todo1 = Todo(1, "t1", TodoPriority.MEDIUM, TodoFlag.UNDONE, 1)
        val todo2 = Todo(2, "t2", TodoPriority.HIGH, TodoFlag.IN_PROGRESS, 1)
        val todo3 = Todo(3, "t3", TodoPriority.LOW, TodoFlag.DONE, 2)
        todoDao.insertTodo(todo1)
        todoDao.insertTodo(todo2)
        todoDao.insertTodo(todo3)

        todoCategoryDao.deleteTodoCategory(category2)
        todoDao.getAllTodosOfTodoCategory(category2.categoryId).test {
            val todos = awaitItem()
            assertThat(todos.isEmpty())
        }

        todoCategoryDao.deleteTodoCategory(category1)
        todoDao.getAllTodos().test {
            val todos = awaitItem()
            assertThat(todos.isEmpty())
        }
    }
}