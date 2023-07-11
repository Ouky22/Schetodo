package com.example.schetodo.feature.schedule_template.use_case

import com.example.schetodo.data.schedule_block.ScheduleBlockRepository
import com.example.schetodo.data.todo_block.TodoBlockRepository
import kotlinx.coroutines.flow.first
import java.time.LocalDate
import java.time.LocalDateTime
import javax.inject.Inject

class ApplyScheduleTemplateUseCase @Inject constructor(
    private val scheduleBlockRepository: ScheduleBlockRepository,
    private val todoBlockRepository: TodoBlockRepository
) {
    suspend operator fun invoke(
        scheduleTemplateId: Int,
        applyDate: LocalDate,
        applyScheduleConflictStrategy: ApplyScheduleConflictStrategy
    ) {
        scheduleBlockRepository.getScheduleBlocksOfScheduleTemplate(scheduleTemplateId)
            .first()
            .filter { !it.todoBlock.markedForDeletion }
            .filter { scheduleBlock ->
                val overlappingTodoBlocks = todoBlockRepository.getTodoBlocksThatOverlapWith(
                    scheduleBlock.todoBlock, applyDate
                )

                val overlapsWithOtherTodoBlocks = overlappingTodoBlocks.isNotEmpty()
                if (overlapsWithOtherTodoBlocks) {
                    when (applyScheduleConflictStrategy) {
                        ApplyScheduleConflictStrategy.REPLACE -> {
                            overlappingTodoBlocks.forEach { todoBlockRepository.deleteTodoBlock(it) }
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
    }
}

enum class ApplyScheduleConflictStrategy {
    REPLACE,
    SKIP
}