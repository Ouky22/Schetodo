package com.example.schetodo.data

import java.time.LocalDate

val MAX_DATE: LocalDate = LocalDate.now().plusYears(5)
val MIN_DATE: LocalDate = LocalDate.of(1970, 1, 1)

const val NOTIFICATION_TABLE_NAME = "Notification"
const val TODO_BLOCK_CATEGORY_RELATIONSHIP_TABLE_NAME = "TodoBlockCategoryRelationship"
const val TODO_BLOCK_TODO_RELATIONSHIP_TABLE_NAME = "TodoBlockTodoRelationship"
const val SCHEDULE_TEMPLATE_TABLE_NAME = "ScheduleTemplate"
const val TODO_TABLE_NAME = "Todo"
const val TODO_BLOCK_TABLE_NAME = "TodoBlock"
const val TODO_CATEGORY_TABLE_NAME = "TodoCategory"

const val BACKUP_FILE_MIME_TYPE = "application/octet-stream"
