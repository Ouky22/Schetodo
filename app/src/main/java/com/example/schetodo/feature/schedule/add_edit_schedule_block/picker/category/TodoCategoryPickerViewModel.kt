package com.example.schetodo.feature.schedule.add_edit_schedule_block.picker.category

import com.example.schetodo.data.todo.TodoRepository
import com.example.schetodo.data.todo_category.TodoCategory
import com.example.schetodo.data.todo_category.TodoCategoryRepository
import com.example.schetodo.feature.schedule.add_edit_schedule_block.picker.PickerViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class TodoCategoryPickerViewModel @Inject constructor(
    todoRepository: TodoRepository,
    todoCategoryRepository: TodoCategoryRepository
) : PickerViewModel<TodoCategory>(todoRepository, todoCategoryRepository)