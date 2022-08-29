package com.mumbicodes.presentation.add_edit_project

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.mumbicodes.R
import com.mumbicodes.presentation.components.LabelledInputField
import com.mumbicodes.presentation.components.LabelledInputFieldWithIcon
import com.mumbicodes.presentation.components.PrimaryButton
import com.mumbicodes.presentation.theme.GreyDark
import com.mumbicodes.presentation.theme.ProjectTrackingTheme
import com.mumbicodes.presentation.theme.Space20dp
import com.mumbicodes.presentation.theme.Space48dp
import com.squaredem.composecalendar.ComposeCalendar
import kotlinx.coroutines.flow.collectLatest
import java.time.LocalDate

@Composable
fun AddAndEditScreen(
    projectsViewModel: AddEditProjectViewModel = hiltViewModel(),
    navController: NavController,
) {
    val nameTextState = projectsViewModel.projectNameState.value
    val descTextState = projectsViewModel.projectDescState.value
    val deadlineTextState = projectsViewModel.projectDeadlineState.value
    val passedProjectId = projectsViewModel.passedProjectId
    val isCalendarVisible = projectsViewModel.isCalendarVisible.value

    LaunchedEffect(key1 = true) {
        projectsViewModel.uiEvents.collectLatest { uIEvents ->
            when (uIEvents) {
                is UIEvents.ShowSnackBar -> {
                    // TODO
                }
                is UIEvents.AddEditProject -> {
                    // TODO - change to Project details
                    navController.navigateUp()
                }
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(Space20dp)
            .background(color = MaterialTheme.colorScheme.background)
    ) {

        val actionToPerform = when {
            passedProjectId != -1 -> stringResource(id = R.string.editTitle)
            else -> stringResource(id = R.string.addTitle)
        }
        ScreenHeader(
            modifier = Modifier,
            titleTextAction = actionToPerform,
            iconOnClick = {
                navController.popBackStack()
            }
        )

        FieldForms(
            modifier = Modifier.fillMaxWidth(),
            onNameChanged = { name ->
                projectsViewModel.onEvent(AddEditProjectEvents.NameChanged(name))
            },
            onDescChanged = { desc ->
                projectsViewModel.onEvent(AddEditProjectEvents.DescriptionChanged(desc))
            },
            onDeadlineClicked = {
                projectsViewModel.onEvent(AddEditProjectEvents.ToggleCalendarVisibility)
            },
            onDeadlineChanged = { deadLine ->
                projectsViewModel.onEvent(AddEditProjectEvents.DeadlineChanged(deadLine))
            },
            nameTextValue = nameTextState,
            descTextValue = descTextState,
            deadlineTextValue = deadlineTextState,
            isCalendarVisible = isCalendarVisible,
            onSaveProject = {
                projectsViewModel.onEvent(AddEditProjectEvents.AddEditProject)
            }
        )
    }
}

@Composable
fun ScreenHeader(
    modifier: Modifier,
    titleTextAction: String,
    iconOnClick: () -> Unit,
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            modifier = Modifier
                .size(24.dp, 24.dp)
                .clickable {
                    iconOnClick
                },
            painter = painterResource(id = R.drawable.ic_arrow_back),
            tint = MaterialTheme.colorScheme.outline,
            contentDescription = "Back button",
        )
        Text(
            text = stringResource(id = R.string.addEditProjectTitle, titleTextAction),
            style = MaterialTheme.typography.headlineLarge.copy(color = GreyDark),
            textAlign = TextAlign.Center,
            modifier = Modifier
                .weight(1f)
        )
    }
}

@Composable
fun FieldForms(
    modifier: Modifier = Modifier,
    onNameChanged: (String) -> Unit,
    onDescChanged: (String) -> Unit,
    onDeadlineClicked: () -> Unit,
    onDeadlineChanged: (LocalDate) -> Unit,
    nameTextValue: String,
    descTextValue: String,
    deadlineTextValue: String,
    isCalendarVisible: Boolean,
    onSaveProject: () -> Unit,
) {

    Column(modifier = modifier) {
        Spacer(modifier = Modifier.height(Space20dp))
        LabelledInputField(
            modifier = Modifier
                .fillMaxWidth(),
            onValueChange = onNameChanged,
            fieldLabel = stringResource(id = R.string.projectName),
            placeholder = "What is your project name?",
            textValue = nameTextValue,
            singleLine = false,
        )
        Spacer(modifier = Modifier.height(Space20dp))
        LabelledInputField(
            modifier = Modifier
                .fillMaxWidth(),
            onValueChange = onDescChanged,
            fieldLabel = stringResource(id = R.string.projectName),
            placeholder = "How would you describe it?",
            textValue = descTextValue,
            singleLine = false,
        )
        Spacer(modifier = Modifier.height(Space20dp))

        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .clickable {
                    onDeadlineClicked()
                },
            color = Color.Transparent
        ) {
            LabelledInputFieldWithIcon(
                modifier = Modifier
                    .fillMaxWidth(),
                onValueChange = {},
                fieldLabel = stringResource(id = R.string.projectName),
                placeholder = "DD/MM/YYYY",
                textValue = deadlineTextValue,
                singleLine = true,
                vectorIconId = R.drawable.ic_calendar,
            )
        }
        if (isCalendarVisible) {
            ComposeCalendar(
                onDone = { userDateSelection ->
                    onDeadlineChanged(userDateSelection)
                },
                onDismiss =
                onDeadlineClicked
            )
        }

        Spacer(modifier = Modifier.height(Space48dp))
        PrimaryButton(
            text = stringResource(id = R.string.saveProject),
            onClick = onSaveProject,
            isEnabled = nameTextValue.isNotBlank() &&
                descTextValue.isNotBlank() && deadlineTextValue.isNotBlank()
        )
    }
}

@Composable
@Preview
fun ScreenContentPreview() {
    ProjectTrackingTheme {
    }
}
