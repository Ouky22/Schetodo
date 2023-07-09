package com.example.schetodo.feature.schedule.add_edit_schedule_block.picker

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.schetodo.R
import com.example.schetodo.ui.components.PositiveNegativeButtonRow
import com.example.schetodo.ui.components.SchetodoTopAppBar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PickerScreen(
    modifier: Modifier = Modifier,
    topAppBarTitle: String,
    showTopBarBackButton: Boolean,
    onTopBarBackButtonClick: () -> Unit,
    selectedItemCount: Int,
    onAdd: () -> Unit,
    onCancel: () -> Unit,
    pickerList: @Composable () -> Unit
) {
    Scaffold(
        topBar = {
            SchetodoTopAppBar(
                title = topAppBarTitle,
                showBackButton = showTopBarBackButton,
                onBackButtonClick = onTopBarBackButtonClick
            )
        }
    ) { contentPadding ->
        Column(
            modifier = modifier.padding(contentPadding)
        ) {
            Column(modifier = Modifier.weight(1f)) {
                pickerList()
            }

            PositiveNegativeButtonRow(
                positiveButtonText =
                if (selectedItemCount == 0) stringResource(id = R.string.add)
                else stringResource(id = R.string.add_with_number, selectedItemCount),
                positiveButtonEnabled = selectedItemCount > 0,
                negativeButtonText = stringResource(id = R.string.cancel),
                onPositiveClick = onAdd,
                onNegativeClick = onCancel,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp, bottom = 24.dp)
                    .padding(horizontal = 16.dp)
            )
        }
    }
}