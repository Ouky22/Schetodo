package com.example.schetodo.feature.schedule.add_edit_schedule_block

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.schetodo.R
import com.example.schetodo.data.notification.Notification
import com.example.schetodo.data.schedule_block.ScheduleBlock
import com.example.schetodo.data.schedule_block.ScheduleBlockRepository
import com.example.schetodo.data.todo.Todo
import com.example.schetodo.data.todo.TodoRepository
import com.example.schetodo.data.todo_block.TodoBlock
import com.example.schetodo.data.todo_block.TodoBlockRepository
import com.example.schetodo.data.todo_category.TodoCategory
import com.example.schetodo.data.todo_category.TodoCategoryRepository
import com.example.schetodo.feature.schedule.add_edit_schedule_block.AddEditScheduleBlockEvent.*
import com.example.schetodo.feature.use_case.GeneralUseCases
import com.example.schetodo.ui.navigation.schedule.AddScheduleBlock
import com.example.schetodo.ui.navigation.schedule.EditScheduleBlock
import com.example.schetodo.ui.util.UiText
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import javax.inject.Inject

@HiltViewModel
class AddEditScheduleBlockViewModel @Inject constructor(
    private val scheduleBlockRepository: ScheduleBlockRepository,
    private val todoRepository: TodoRepository,
    private val todoCategoryRepository: TodoCategoryRepository,
    private val todoBlockRepository: TodoBlockRepository,
    private val generalUseCases: GeneralUseCases,
    savedStateHandle: SavedStateHandle
) : ViewModel() {
    var state by mutableStateOf(AddEditScheduleBlockScreenState())
        private set

    private val _errorMessages = MutableSharedFlow<UiText>()
    val errorMessages: SharedFlow<UiText>
        get() = _errorMessages.asSharedFlow()

    private val _closeAddEditScheduleBlockScreen = MutableStateFlow(false)
    val closeAddEditScheduleBlockScreen: StateFlow<Boolean>
        get() = _closeAddEditScheduleBlockScreen.asStateFlow()

    val todoBlockId: Int
    private var todoBlockTemplateId: Int? = null

    private var scheduleBlockDate: LocalDate? = null
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
            is MarkScheduleBlockForDeletion -> onMarkScheduleBlockForDeletion()
            is ChangeShowNotificationAtBeginning -> onChangeShowNotificationAtBeginning(event.showNotification)
            is ChangeShowNotificationAtEnd -> onChangeShowNotificationAtEnd(event.showNotification)
        }
    }

    private fun onChangeShowNotificationAtBeginning(showNotification: Boolean) {
        state = state.copy(
            showNotificationAtBeginning = showNotification
        )
    }

    private fun onChangeShowNotificationAtEnd(showNotification: Boolean) {
        state = state.copy(
            showNotificationAtEnd = showNotification
        )
    }

    private fun onMarkScheduleBlockForDeletion() {
        if (!state.inEditingMode)
            return

        viewModelScope.launch {
            todoBlockRepository.markTodoBlockForDeletion(todoBlockId)
            _closeAddEditScheduleBlockScreen.value = true
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
                    todoCategories = state.todoCategories,
                    notifications = getNotifications(todoBlock)
                )
            )

            _closeAddEditScheduleBlockScreen.value = true
        }
    }

    private fun getNotifications(todoBlock: TodoBlock): List<Notification> {
        val notifications = mutableListOf<Notification>()
        if (state.showNotificationAtBeginning)
            notifications += Notification(
                dateTime = LocalDateTime.of(todoBlock.date ?: LocalDate.now(), todoBlock.startTime)
            )
        if (state.showNotificationAtEnd)
            notifications += Notification(
                dateTime = LocalDateTime.of(todoBlock.date ?: LocalDate.now(), todoBlock.endTime)
            )
        return notifications
    }

    private fun addSelectedTodos(todoIds: List<Int>) {
        viewModelScope.launch {
            val selectedTodos = todoIds.mapNotNull {
                todoRepository.getTodoById(it).first()
            }
            val allSelectedTodos = (state.todos + selectedTodos).toSet().toList()
            setTodosState(allSelectedTodos)
            addTodoCategoriesOfTodos(allSelectedTodos)
        }
    }

    private fun addTodoCategoriesOfTodos(todos: List<Todo>) {
        addSelectedTodoCategories(todos.map { it.categoryId })
    }

    private fun removeSelectedTodo(todo: Todo) {
        setTodosState((state.todos - todo))
    }

    private fun addSelectedTodoCategories(todoCategoryIds: List<Int>) {
        viewModelScope.launch {
            val selectedCategories = todoCategoryIds.mapNotNull { categoryId ->
                todoCategoryRepository.getTodoCategory(categoryId).first()
            }
            val allSelectedCategories = (state.todoCategories + selectedCategories).toSet().toList()
            state = state.copy(todoCategories = allSelectedCategories.sortedBy { it.name })
        }
    }

    private fun removeSelectedTodoCategory(todoCategory: TodoCategory) {
        state = state.copy(
            todoCategories = (state.todoCategories - todoCategory).sortedBy { it.name }
        )
    }

    private fun updateTodoBlockNotes(notes: String) {
        state = state.copy(
            notes = notes
        )
    }

    private fun updateStartTime(startTime: LocalTime) {
        this.startTime = startTime
        state = state.copy(
            startTime = generalUseCases.formatTime(startTime)
        )
    }

    private fun updateEndTime(endTime: LocalTime) {
        this.endTime = endTime
        state = state.copy(
            endTime = generalUseCases.formatTime(endTime)
        )
    }

    private fun updateCurrentDate(date: LocalDate) {
        scheduleBlockDate = date
        state = state.copy(
            date = generalUseCases.formatDate(date)
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

            scheduleBlock.todoBlock.date?.let { updateCurrentDate(it) }
            updateStartTime(scheduleBlock.todoBlock.startTime.toSecondOfDay())
            updateEndTime(scheduleBlock.todoBlock.endTime.toSecondOfDay())

            state = state.copy(
                todoCategories = scheduleBlock.todoCategories.sortedBy { it.name },
                notes = scheduleBlock.todoBlock.notes ?: "",
                inEditingMode = true
            )
            setTodosState(scheduleBlock.todos)
            setNotificationsState(scheduleBlock)
            todoBlockTemplateId = scheduleBlock.todoBlock.templateId
        }
    }

    private fun setNotificationsState(scheduleBlock: ScheduleBlock) {
        val scheduleBlockFromTemplate = scheduleBlock.todoBlock.date == null
        if (scheduleBlockFromTemplate) {
            val showNotificationAtBeginning = scheduleBlock.notifications.any {
                it.dateTime.toLocalTime() == scheduleBlock.todoBlock.startTime
            }
            val showNotificationAtEnd = scheduleBlock.notifications.any {
                it.dateTime.toLocalTime() == scheduleBlock.todoBlock.endTime
            }

            state = state.copy(
                showNotificationAtBeginning = showNotificationAtBeginning,
                showNotificationAtEnd = showNotificationAtEnd
            )
        } else {
            val showNotificationAtBeginning = scheduleBlock.notifications.any {
                it.dateTime == LocalDateTime.of(
                    scheduleBlock.todoBlock.date, scheduleBlock.todoBlock.startTime
                )
            }
            val showNotificationAtEnd = scheduleBlock.notifications.any {
                it.dateTime == LocalDateTime.of(
                    scheduleBlock.todoBlock.date, scheduleBlock.todoBlock.endTime
                )
            }

            state = state.copy(
                showNotificationAtBeginning = showNotificationAtBeginning,
                showNotificationAtEnd = showNotificationAtEnd
            )
        }
    }

    private fun loadDataForAddingScheduleBlock(savedStateHandle: SavedStateHandle) {
        val dateStamp = savedStateHandle.get<Long>(AddScheduleBlock.dateStampArg)
        dateStamp?.let { updateCurrentDate(dateStamp) }

        val startTimeStamp = savedStateHandle.get<Int>(AddScheduleBlock.startTimeStampArg)
        val startTimeReceived = startTimeStamp != null && startTimeStamp >= 0
        if (startTimeReceived) updateStartTime(startTimeStamp!!)
        else updateStartTime(0)

        val endTimeStamp = savedStateHandle.get<Int>(AddScheduleBlock.endTimeStampArg)
        val endTimeReceived = endTimeStamp != null && endTimeStamp >= 0
        if (endTimeReceived) updateEndTime(endTimeStamp!!)
        else updateEndTime(0)

        viewModelScope.launch {
            onChangeShowNotificationAtBeginning(
                scheduleBlockRepository.showScheduleBlockNotificationAtBeginning.first()
            )
            onChangeShowNotificationAtEnd(
                scheduleBlockRepository.showScheduleBlockNotificationAtEnd.first()
            )
        }
    }

    private suspend fun todoBlockOverlapsWithOtherTodoBlock(todoBlock: TodoBlock): Boolean {
        val isTemplateTodoBlock = todoBlock.templateId != null
        return if (isTemplateTodoBlock)
            todoBlockRepository.templateTodoBlockOverlapsWithTodoBlockFromSameTemplate(todoBlock)
        else
            todoBlockRepository.todoBlockOverlapsWithOtherTodoBlock(todoBlock)
    }

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

    private fun setTodosState(todos: List<Todo>) {
        state = state.copy(
            todos = todos.sortedWith(
                compareByDescending(Todo::priority).thenBy(Todo::description)
            )
        )
    }
}