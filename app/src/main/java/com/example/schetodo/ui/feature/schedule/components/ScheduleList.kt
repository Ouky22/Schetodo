package com.example.schetodo.ui.feature.schedule.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.schetodo.data.todo_category.TodoCategory
import com.example.schetodo.ui.util.UiText
import java.time.LocalTime

interface ScheduleListItem {
    val startTime: LocalTime
    val endTime: LocalTime
    val durationHours: UiText
    val durationMinutes: UiText
}

data class ScheduleGap(
    override val startTime: LocalTime,
    override val endTime: LocalTime,
    override val durationHours: UiText = UiText.DynamicString(""),
    override val durationMinutes: UiText = UiText.DynamicString("")
) : ScheduleListItem

data class UiScheduleBlock(
    val todoBlockId: Int = 0,
    override val startTime: LocalTime = LocalTime.of(0, 0),
    override val endTime: LocalTime = LocalTime.of(0, 0),
    val startTimeText: String = "",
    val endTimeText: String = "",
    override val durationHours: UiText = UiText.DynamicString(""),
    override val durationMinutes: UiText = UiText.DynamicString(""),
    val categories: List<TodoCategory> = emptyList(),
    val todoDescriptions: List<String> = emptyList(),
    val notes: String = "",
    val isCurrentScheduleBlock: Boolean = false
) : ScheduleListItem


@Composable
fun ScheduleList(
    modifier: Modifier = Modifier,
    scheduleListItems: List<ScheduleListItem>,
    onScheduleBlockItemClick: (todoBlockId: Int) -> Unit,
    onScheduleGapClick: (startTime: LocalTime, endTime: LocalTime) -> Unit
) {
    LazyColumn(
        modifier = modifier,
        contentPadding = PaddingValues(top = 16.dp, bottom = 100.dp, start = 12.dp, end = 12.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        items(
            items = scheduleListItems, key = { it.startTime.toSecondOfDay() }
        ) { scheduleListItem ->
            when (scheduleListItem) {
                is UiScheduleBlock ->
                    ScheduleBlockItem(
                        todoCategories = scheduleListItem.categories,
                        todoDescriptions = scheduleListItem.todoDescriptions,
                        todoBlocKNotes = scheduleListItem.notes,
                        startTimeString = scheduleListItem.startTimeText,
                        endTimeString = scheduleListItem.endTimeText,
                        durationString = "${scheduleListItem.durationHours.asString()} ${scheduleListItem.durationMinutes.asString()}",
                        modifier = Modifier.clickable { onScheduleBlockItemClick(scheduleListItem.todoBlockId) },
                        elevate = scheduleListItem.isCurrentScheduleBlock
                    )
                is ScheduleGap ->
                    OutlinedButton(
                        onClick = {
                            onScheduleGapClick(
                                scheduleListItem.startTime,
                                scheduleListItem.endTime
                            )
                        }, modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(text = "${scheduleListItem.durationHours.asString()} ${scheduleListItem.durationMinutes.asString()}")
                    }
            }
        }
    }
}