package com.example.schetodo.ui.navigation.schedule

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Schedule
import androidx.navigation.NavType
import androidx.navigation.navArgument
import com.example.schetodo.R
import com.example.schetodo.ui.navigation.MainSchetodoDestination
import com.example.schetodo.ui.navigation.SchetodoDestination

object Schedule : MainSchetodoDestination {
    override val icon = Icons.Outlined.Schedule
    override val route = "schedule"
    override val titleResourceId = R.string.schedule
}

object AddScheduleBlock : SchetodoDestination {
    override val route = "add_schedule_block"
    const val dateStampArg = "date_stamp"
    const val startTimeStampArg = "start_time_stamp"
    const val endTimeStampArg = "end_time_stamp"

    val routeWithArgs =
        "$route/{$dateStampArg}" +
                "?$startTimeStampArg={$startTimeStampArg}&" +
                "$endTimeStampArg={$endTimeStampArg}"

    val args = listOf(
        navArgument(dateStampArg) {
            type = NavType.LongType
        },
        navArgument(startTimeStampArg) {
            type = NavType.IntType
            defaultValue = -1
        },
        navArgument(endTimeStampArg) {
            type = NavType.IntType
            defaultValue = -1
        }
    )
}

object EditScheduleBlock: SchetodoDestination {
    override val route = "edit_schedule_block"
    const val todoBlockIdArg = "todo_block_id"

    val routeWithArgs = "$route/{$todoBlockIdArg}"

    val args = listOf(
        navArgument(todoBlockIdArg) {
            type = NavType.IntType
        }
    )
}

object TodoPicker: SchetodoDestination {
    override val route = "todo_picker"
}

object TodoCategoryPicker: SchetodoDestination {
    override val route = "todo_category_picker"
}

object ScheduleTemplates: SchetodoDestination {
    override val route = "schedule_templates"
}

object EditScheduleTemplate : SchetodoDestination {
    override val route = "edit_schedule_template"
    const val scheduleTemplateIdArg = "schedule_template_id"

    val routeWithArgs = "$route/{$scheduleTemplateIdArg}"

    val args = listOf(
        navArgument(scheduleTemplateIdArg) {
            type = NavType.IntType
        }
    )
}