package com.example.schetodo.feature.schedule_template.use_case

import com.example.schetodo.data.schedule_block.ScheduleBlockRepository
import com.example.schetodo.data.todo_block.TodoBlockRepository
import com.example.schetodo.di.CoroutineScopeModule.ApplicationCoroutineScope
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.LocalDateTime
import javax.inject.Inject

class ApplyScheduleTemplateUseCase @Inject constructor(
    private val scheduleBlockRepository: ScheduleBlockRepository,
    private val todoBlockRepository: TodoBlockRepository,
    @ApplicationCoroutineScope private val applicationCoroutineScope: CoroutineScope
) {
    suspend operator fun invoke(
        scheduleTemplateId: Int,
        applyDate: LocalDate,
        applyScheduleConflictStrategy: ApplyScheduleConflictStrategy
    ) {
        applicationCoroutineScope.launch {
            scheduleBlockRepository.getScheduleBlocksOfScheduleTemplate(scheduleTemplateId)
                .first()
                .filter { scheduleBlock ->
                    val overlappingTodoBlocks = todoBlockRepository.getTodoBlocksThatOverlapWith(
                        scheduleBlock.todoBlock, applyDate
                    )

                    val overlapsWithOtherTodoBlocks = overlappingTodoBlocks.isNotEmpty()
                    if (overlapsWithOtherTodoBlocks) {
                        when (applyScheduleConflictStrategy) {
                            ApplyScheduleConflictStrategy.REPLACE -> {
                                overlappingTodoBlocks.forEach {
                                    todoBlockRepository.deleteTodoBlock(it)
                                }
                                true
                            }
                            ApplyScheduleConflictStrategy.SKIP -> false
                        }
                    } else
                        true
                }
                .map { scheduleBlock ->
                    val todoBlock = scheduleBlock.todoBlock.copy(
                        todoBlockId = 0,
                        templateId = null,
                        date = applyDate
                    )

                    val notifications = scheduleBlock.notifications.map {

                        val notificationDate = it.dateTime.toLocalTime()
                        it.copy(
                            notificationId = 0,
                            dateTime = LocalDateTime.of(applyDate, notificationDate)
                        )
                    }
                    scheduleBlock.copy(
                        todoBlock = todoBlock,
                        notifications = notifications,
                    )
                }
                .forEach { scheduleBlock ->
                    scheduleBlockRepository.insertOrUpdateScheduleBlock(scheduleBlock)
                }
        }.join()
    }
}

enum class ApplyScheduleConflictStrategy {
    REPLACE,
    SKIP
}