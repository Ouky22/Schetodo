package com.example.schetodo.feature.schedule_template.list

import android.content.res.Configuration
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import com.example.schetodo.feature.schedule_template.list.ScheduleTemplatesEvent.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.schetodo.R
import com.example.schetodo.data.schedule_template.ScheduleTemplate
import com.example.schetodo.feature.schedule_template.edit_schedule_template.ID_OF_SCHEDULE_TEMPLATE_MARKED_FOR_DELETION
import com.example.schetodo.ui.SchetodoAppState
import com.example.schetodo.ui.components.SubDestinationTopAppBar
import com.example.schetodo.ui.theme.SchetodoTheme
import com.example.schetodo.ui.util.popFromCurrentBackStackEntry
import com.example.schetodo.ui.util.showSnackbarWithActionHandler
import kotlinx.coroutines.launch

@Composable
fun ScheduleTemplatesScreen(
    modifier: Modifier = Modifier,
    viewModel: ScheduleTemplatesViewModel,
    schetodoAppState: SchetodoAppState,
    onEditScheduleTemplate: (templateId: Int) -> Unit
) {
    val scheduleTemplates = viewModel.scheduleTemplates.collectAsState()
    val context = LocalContext.current
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(true) {
        launch {
            schetodoAppState.navController.popFromCurrentBackStackEntry<Int>(
                key = ID_OF_SCHEDULE_TEMPLATE_MARKED_FOR_DELETION,
                onPop = { templateId ->
                    snackbarHostState.showSnackbarWithActionHandler(
                        message = context.getString(R.string.deleted_schedule_template),
                        actionLabel = context.getString(R.string.undo),
                        onActionPerformed = {
                            viewModel.onEvent(UndoDeletionOfScheduleTemplate(templateId))
                        }
                    )
                }
            )
        }
    }

    ScheduleTemplatesScreen(
        modifier = modifier,
        scheduleTemplates = scheduleTemplates.value,
        onClickOnScheduleTemplate = onEditScheduleTemplate,
        onBackButtonClick = { schetodoAppState.navController.popBackStack() },
        snackbarHostState = snackbarHostState
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScheduleTemplatesScreen(
    modifier: Modifier = Modifier,
    scheduleTemplates: List<ScheduleTemplate>,
    onClickOnScheduleTemplate: (templateId: Int) -> Unit,
    onBackButtonClick: () -> Unit,
    snackbarHostState: SnackbarHostState
) {
    Scaffold(
        topBar = {
            SubDestinationTopAppBar(
                title = stringResource(R.string.schedule_templates),
                showBackButton = true,
                onBackButtonClick = onBackButtonClick
            )
        },
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) }
    ) { contentPadding ->
        LazyColumn(
            modifier = modifier.padding(contentPadding),
            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            items(items = scheduleTemplates, key = { it.templateId }) { template ->
                ScheduleTemplateListItem(
                    modifier = Modifier.fillMaxWidth(),
                    scheduleTemplateName = template.name,
                    onClick = { onClickOnScheduleTemplate(template.templateId) }
                )
            }
        }
    }
}

@Composable
fun ScheduleTemplateListItem(
    modifier: Modifier = Modifier,
    scheduleTemplateName: String,
    onClick: () -> Unit
) {
    Card(
        modifier = modifier.clickable { onClick() },
    ) {
        Row(
            modifier = Modifier
                .padding(horizontal = 8.dp, vertical = 32.dp)
                .wrapContentWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Start
        ) {
            Text(
                modifier = Modifier.padding(horizontal = 8.dp),
                text = scheduleTemplateName,
                style = MaterialTheme.typography.headlineSmall,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Preview
@Composable
fun ScheduleTemplatesScreenPreview() {
    SchetodoTheme {
        ScheduleTemplatesScreen(
            modifier = Modifier.fillMaxSize(),
            scheduleTemplates = listOf(
                ScheduleTemplate(1, "This is the first schedule template"),
                ScheduleTemplate(2, "This is second schedule template"),
                ScheduleTemplate(3, "The third schedule template"),
                ScheduleTemplate(4, "Fourth schedule template"),
                ScheduleTemplate(5, "Fifth template")
            ),
            onClickOnScheduleTemplate = {},
            onBackButtonClick = {},
            snackbarHostState = SnackbarHostState()
        )
    }
}

@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun ScheduleTemplatesScreenDarkPreview() {
    SchetodoTheme {
        ScheduleTemplatesScreen(
            modifier = Modifier.fillMaxSize(),
            scheduleTemplates = listOf(
                ScheduleTemplate(1, "This is the first schedule template"),
                ScheduleTemplate(2, "This is second schedule template"),
                ScheduleTemplate(3, "The third schedule template"),
                ScheduleTemplate(4, "Fourth schedule template"),
                ScheduleTemplate(5, "Fifth template")
            ),
            onClickOnScheduleTemplate = {},
            onBackButtonClick = {},
            snackbarHostState = SnackbarHostState()
        )
    }
}


@Preview
@Composable
fun ScheduleTemplateListItemPreview() {
    SchetodoTheme {
        ScheduleTemplateListItem(
            scheduleTemplateName = "This is a very long name for a schedule template",
            onClick = {}
        )
    }
}