package com.example.schetodo.ui.feature.schedule.add_edit_schedule_block

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.schetodo.R
import com.example.schetodo.data.schedule_block.ScheduleBlock
import com.example.schetodo.data.schedule_block.ScheduleBlockRepository
import com.example.schetodo.data.todo.Todo
import com.example.schetodo.data.todo.TodoFlag
import com.example.schetodo.data.todo.TodoRepository
import com.example.schetodo.data.todo_block.TodoBlock
import com.example.schetodo.data.todo_block.TodoBlockRepository
import com.example.schetodo.data.todo_category.TodoCategory
import com.example.schetodo.data.todo_category.TodoCategoryRepository
import com.example.schetodo.ui.navigation.schedule.AddScheduleBlock
import com.example.schetodo.ui.navigation.schedule.EditScheduleBlock
import dagger.hilt.android.lifecycle.HiltViewModel
import com.example.schetodo.ui.feature.schedule.add_edit_schedule_block.AddEditScheduleBlockEvent.*
import com.example.schetodo.ui.util.UiText
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle
import java.util.*
import javax.inject.Inject

@HiltViewModel
class AddEditScheduleBlockViewModel @Inject constructor(
    private val scheduleBlockRepository: ScheduleBlockRepository,
    private val todoRepository: TodoRepository,
    private val todoCategoryRepository: TodoCategoryRepository,
    private val todoBlockRepository: TodoBlockRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {
    var state by mutableStateOf(AddEditScheduleBlockScreenState())
        private set

    private val _errorMessages = MutableSharedFlow<UiText>()
    val errorMessages: SharedFlow<UiText>
        get() = _errorMessages.asSharedFlow()

    private var todoBlockId: Int
    private var todoBlockTemplateId: Int? = null

    private lateinit var scheduleBlockDate: LocalDate
    private lateinit var startTime: LocalTime
    private lateinit var endTime: LocalTime

    init {
        todoBlockId = savedStateHandle[EditScheduleBlock.todoBlockIdArg] ?: 0

        val viewModelCreatedForEditing = todoBlockId >= 1
        if (viewModelCreatedForEditing)
            loadScheduleBlockToEdit(todoBlockId)
        else
            loadDataForAddingScheduleBlock(savedStateHandle)
    }

    fun onEvent(event: AddEditScheduleBlockEvent) {
        when (event) {
            is ChangeTodoBlockNotes -> updateTodoBlockNotes(event.notes)
            is ChangeDate -> updateCurrentDate(event.date)
            is ChangeStartTime -> updateStartTime(event.startTime)
            is ChangeEndTime -> updateEndTime(event.endTime)
            is SelectTodos -> addSelectedTodos(event.todoIds)
            is RemoveSelectedTodo -> removeSelectedTodo(event.todo)
            is SelectTodoCategories -> addSelectedTodoCategories(event.todoCategoryIds)
            is RemoveSelectedTodoCategory -> removeSelectedTodoCategory(event.category)
            is SaveScheduleBlock -> saveScheduleBlock()
        }
    }

    private fun saveScheduleBlock() {
        if (endTimeIsNotAfterStartTime()) {
            sendEndTimeNotAfterStartTimeErrorMessage()
            return
        }
        if (schetodoBlockHasNotEnoughInformation()) {
            sendScheduleBlockNotEnoughInfoErrorMessage()
            return
        }

        viewModelScope.launch {
            val todoBlock = TodoBlock(
                todoBlockId, state.notes, scheduleBlockDate, startTime, endTime, todoBlockTemplateId
            )

            if (todoBlockOverlapsWithOtherTodoBlock(todoBlock)) {
                sendTodoBlockOverlapsErrorMessage()
                return@launch
            }

            scheduleBlockRepository.insertOrUpdateScheduleBlock(
                ScheduleBlock(
                    todoBlock = todoBlock,
                    todos = state.todos,
                    todoCategories = state.todoCategories
                )
            )

            state = state.copy(successfullySaved = true)
        }
    }

    private fun addSelectedTodos(todoIds: List<Int>) {
        viewModelScope.launch {
            val selectedTodos = todoIds.mapNotNull {
                todoRepository.getTodoById(it).first()
            }
            val allSelectedTodos = (state.todos + selectedTodos).toSet().toList()
            state = state.copy(todos = allSelectedTodos)

            addTodoCategoriesOfTodos(allSelectedTodos)
        }
    }

    private fun addTodoCategoriesOfTodos(todos: List<Todo>) {
        addSelectedTodoCategories(todos.map { it.categoryId })
    }

    private fun removeSelectedTodo(todo: Todo) {
        state = state.copy(todos = state.todos - todo)
    }

    private fun addSelectedTodoCategories(todoCategoryIds: List<Int>) {
        viewModelScope.launch {
            val selectedCategories = todoCategoryIds.mapNotNull { categoryId ->
                todoCategoryRepository.getTodoCategory(categoryId).first()
            }
            val allSelectedCategories = (state.todoCategories + selectedCategories).toSet().toList()
            state = state.copy(todoCategories = allSelectedCategories)
        }
    }

    private fun removeSelectedTodoCategory(todoCategory: TodoCategory) {
        state = state.copy(todoCategories = state.todoCategories - todoCategory)
    }

    private fun updateTodoBlockNotes(notes: String) {
        state = state.copy(
            notes = notes
        )
    }

    private fun updateStartTime(startTime: LocalTime) {
        this.startTime = startTime
        state = state.copy(
            startTime = startTime.format(DateTimeFormatter.ofLocalizedTime(FormatStyle.SHORT))
        )
    }

    private fun updateEndTime(endTime: LocalTime) {
        this.endTime = endTime
        state = state.copy(
            endTime = endTime.format(DateTimeFormatter.ofLocalizedTime(FormatStyle.SHORT))
        )
    }

    private fun updateCurrentDate(date: LocalDate) {
        scheduleBlockDate = date
        state = state.copy(
            date = scheduleBlockDate.format(
                DateTimeFormatter.ofPattern("EEE dd LLL, yyyy", Locale.getDefault())
            )
        )
    }

    private fun updateStartTime(startTimeStamp: Int) =
        updateStartTime(LocalTime.ofSecondOfDay(startTimeStamp.toLong()).withSecond(0).withNano(0))

    private fun updateEndTime(endTimeStamp: Int) =
        updateEndTime(LocalTime.ofSecondOfDay(endTimeStamp.toLong()).withSecond(0).withNano(0))

    private fun updateCurrentDate(dateStamp: Long) =
        updateCurrentDate(LocalDate.ofEpochDay(dateStamp))

    private fun loadScheduleBlockToEdit(todoBlockId: Int) {
        viewModelScope.launch {
            val scheduleBlock =
                scheduleBlockRepository.getScheduleBlockByTodoBlockId(todoBlockId).first()
                    ?: throw Exception("There is no ScheduleBlock with TodoBlock id $todoBlockId")

            updateCurrentDate(
                scheduleBlock.todoBlock.date ?: throw Exception("TodoBlock needs a date")
            )
            updateStartTime(scheduleBlock.todoBlock.startTime.toSecondOfDay())
            updateEndTime(scheduleBlock.todoBlock.endTime.toSecondOfDay())
            state = state.copy(
                todoCategories = scheduleBlock.todoCategories,
                todos = scheduleBlock.todos,
                notes = scheduleBlock.todoBlock.notes ?: "",
                inEditingMode = true
            )
            todoBlockTemplateId = scheduleBlock.todoBlock.templateId
        }
    }

    private fun loadDataForAddingScheduleBlock(savedStateHandle: SavedStateHandle) {
        val dateStamp = savedStateHandle.get<Long>(AddScheduleBlock.dateStampArg)
            ?: throw Exception("Date stamp argument cannot be null when adding ScheduleBlock")

        updateCurrentDate(dateStamp)

        val startTimeStamp = savedStateHandle.get<Int>(AddScheduleBlock.startTimeStampArg)
        val startTimeReceived = startTimeStamp != null && startTimeStamp >= 0
        if (startTimeReceived) updateStartTime(startTimeStamp!!)
        else updateStartTime(0)

        val endTimeStamp = savedStateHandle.get<Int>(AddScheduleBlock.endTimeStampArg)
        val endTimeReceived = endTimeStamp != null && endTimeStamp >= 0
        if (endTimeReceived) updateEndTime(endTimeStamp!!)
        else updateEndTime(0)
    }

    private suspend fun todoBlockOverlapsWithOtherTodoBlock(todoBlock: TodoBlock) =
        if (state.inEditingMode) // do not check if an existing TodoBlock overlaps with itself
            todoBlockRepository.todoBlockOverlapsWithOtherTodoBlock(
                todoBlock = todoBlock, exceptOfTodoBlockId = todoBlock.todoBlockId
            )
        else
            todoBlockRepository.todoBlockOverlapsWithOtherTodoBlock(todoBlock)

    private fun endTimeIsNotAfterStartTime() = !endTime.isAfter(startTime)

    private fun schetodoBlockHasNotEnoughInformation(): Boolean {
        val notesNotSet = state.notes.isBlank()
        val todosNotSet = state.todos.isEmpty()
        val categoriesNotSet = state.todoCategories.isEmpty()
        return notesNotSet and todosNotSet and categoriesNotSet
    }

    private fun sendScheduleBlockNotEnoughInfoErrorMessage() {
        viewModelScope.launch {
            _errorMessages.emit(UiText.StringResource(R.string.not_enough_info_for_schetodo_block_error_msg))
        }
    }

    private fun sendTodoBlockOverlapsErrorMessage() {
        viewModelScope.launch {
            _errorMessages.emit(UiText.StringResource(R.string.schedule_block_overlap_error_msg))
        }
    }

    private fun sendEndTimeNotAfterStartTimeErrorMessage() {
        viewModelScope.launch {
            _errorMessages.emit(UiText.StringResource(R.string.start_time_bigger_end_time_error_msg))
        }
    }
}