package com.example.schetodo.ui.feature.schedule.add_edit_schedule_block.picker

import com.example.schetodo.data.todo.Todo
import com.example.schetodo.data.todo.TodoRepository
import com.example.schetodo.data.todo_category.TodoCategoryRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class TodoPickerViewModel @Inject constructor(
    todoRepository: TodoRepository,
    todoCategoryRepository: TodoCategoryRepository
) : PickerViewModel<Todo>(todoRepository, todoCategoryRepository)