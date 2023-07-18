package com.example.schetodo.feature.use_case

import com.example.schetodo.R
import com.example.schetodo.data.schedule_block.ScheduleBlock
import com.example.schetodo.data.todo.Todo
import com.example.schetodo.data.todo_block.TodoBlock
import com.example.schetodo.feature.schedule.components.ScheduleGap
import com.example.schetodo.feature.schedule.components.ScheduleListItem
import com.example.schetodo.feature.schedule.components.UiScheduleBlock
import com.example.schetodo.ui.util.UiText
import java.time.Duration
import java.time.LocalDate
import java.time.LocalTime
import javax.inject.Inject

class ConvertScheduleBlocksToScheduleListItemsUseCase @Inject constructor(
    private val formatTimeUseCase: FormatTimeUseCase
) {
    operator fun invoke(scheduleBlocks: List<ScheduleBlock>): List<ScheduleListItem> {
        var previousEndTime = LocalTime.of(0, 0)
        val scheduleListItems = mutableListOf<ScheduleListItem>()

        for (scheduleBlock in scheduleBlocks.sortedBy { it.todoBlock.startTime }) {
            val gapDuration = Duration.between(previousEndTime, scheduleBlock.todoBlock.startTime)
            if (gapDuration.toMinutes() > 0)
                scheduleListItems.add(
                    ScheduleGap(
                        startTime = previousEndTime,
                        endTime = scheduleBlock.todoBlock.startTime,
                        durationHours = getDurationHoursUiText(gapDuration),
                        durationMinutes = getDurationMinutesUiText(gapDuration),
                        startTimeText = formatTimeUseCase(previousEndTime),
                        endTimeText = formatTimeUseCase(scheduleBlock.todoBlock.startTime)
                    )
                )

            val uiScheduleBlock = convertScheduleBlockToUiScheduleBlock(scheduleBlock)
            scheduleListItems.add(uiScheduleBlock)
            previousEndTime = scheduleBlock.todoBlock.endTime
        }

        if (scheduleBlocks.isNotEmpty()) {
            val scheduleMaxTime = LocalTime.of(23, 59)
            val gap = Duration.between(previousEndTime, scheduleMaxTime)
            if (gap.toMinutes() > 0)
                scheduleListItems.add(
                    ScheduleGap(
                        startTime = previousEndTime,
                        endTime = scheduleMaxTime,
                        durationHours = getDurationHoursUiText(gap),
                        durationMinutes = getDurationMinutesUiText(gap),
                        startTimeText = formatTimeUseCase(previousEndTime),
                        endTimeText = formatTimeUseCase(scheduleMaxTime)
                    )
                )
        }

        return scheduleListItems
    }

    private fun convertScheduleBlockToUiScheduleBlock(scheduleBlock: ScheduleBlock): UiScheduleBlock {
        val todoBlock = scheduleBlock.todoBlock
        val duration = Duration.between(todoBlock.startTime, todoBlock.endTime)

        return UiScheduleBlock(
            todoBlockId = todoBlock.todoBlockId,
            categories = scheduleBlock.todoCategories.sortedBy { it.name },
            todoDescriptions = scheduleBlock.todos
                .sortedWith(compareByDescending(Todo::priority).thenBy(Todo::description))
                .map { it.description },
            notes = todoBlock.notes ?: "",
            startTime = todoBlock.startTime,
            endTime = todoBlock.endTime,
            startTimeText = formatTimeUseCase(todoBlock.startTime),
            endTimeText = formatTimeUseCase(todoBlock.endTime),
            durationHours = getDurationHoursUiText(duration),
            durationMinutes = getDurationMinutesUiText(duration),
            isCurrentScheduleBlock = isCurrentScheduleBlock(todoBlock)
        )
    }

    private fun isCurrentScheduleBlock(todoBlockOfScheduleBlock: TodoBlock) =
        LocalDate.now() == todoBlockOfScheduleBlock.date &&
                LocalTime.now().isAfter(todoBlockOfScheduleBlock.startTime) &&
                LocalTime.now().isBefore(todoBlockOfScheduleBlock.endTime)

    private fun getDurationHoursUiText(duration: Duration): UiText {
        val durationHours = duration.toHours().toInt()
        return if (durationHours >= 1)
            UiText.StringResource(R.string.hour, durationHours)
        else
            UiText.DynamicString("")
    }

    private fun getDurationMinutesUiText(duration: Duration): UiText {
        val durationMinutes = (duration.toMinutes() % 60).toInt()
        return if (durationMinutes >= 1)
            UiText.StringResource(R.string.minute, durationMinutes)
        else
            UiText.DynamicString("")
    }
}