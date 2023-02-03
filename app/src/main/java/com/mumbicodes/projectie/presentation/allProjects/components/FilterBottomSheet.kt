package com.mumbicodes.projectie.presentation.allProjects.components

import android.content.res.Configuration
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.mumbicodes.projectie.R
import com.mumbicodes.projectie.domain.util.OrderType
import com.mumbicodes.projectie.domain.util.ProjectsOrder
import com.mumbicodes.projectie.presentation.components.PrimaryButton
import com.mumbicodes.projectie.presentation.components.SecondaryButton
import com.mumbicodes.projectie.presentation.theme.*

@Composable
fun FilterBottomSheetContent(
    modifier: Modifier = Modifier,
    projectsOrder: ProjectsOrder = ProjectsOrder.DateAdded(OrderType.Descending),
    selectedProjectsOrder: ProjectsOrder,
    onOrderChange: (ProjectsOrder) -> Unit,
    onFiltersApplied: () -> Unit,
    onFiltersReset: () -> Unit,
) {
    Column(
        modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.background)
            .padding(Space20dp),
        horizontalAlignment = Alignment.Start
    ) {

        Text(
            text = stringResource(id = R.string.filter),
            style = MaterialTheme.typography.headlineMedium.copy(color = MaterialTheme.colorScheme.onSurface),
            modifier = Modifier.fillMaxWidth(),
        )
        Spacer(Modifier.height(Space24dp))

        Text(
            text = stringResource(id = R.string.filterBy),
            style = MaterialTheme.typography.titleMedium.copy(color = MaterialTheme.colorScheme.inverseSurface),
            modifier = Modifier.fillMaxWidth(),
        )
        Spacer(Modifier.height(Space12dp))

        // Used user's selection state since the selection has not been published
        DefaultRadioButton(
            text = stringResource(id = R.string.dateCreated),
            isSelected = selectedProjectsOrder is ProjectsOrder.DateAdded,
            onSelect = { onOrderChange(ProjectsOrder.DateAdded(selectedProjectsOrder.orderType)) },
            modifier = Modifier.fillMaxWidth(),
        )

        DefaultRadioButton(
            text = stringResource(id = R.string.projectName),
            isSelected = selectedProjectsOrder is ProjectsOrder.Name,
            onSelect = { onOrderChange(ProjectsOrder.Name(selectedProjectsOrder.orderType)) },
            modifier = Modifier.fillMaxWidth(),
        )

        DefaultRadioButton(
            text = stringResource(id = R.string.projectDeadline),
            isSelected = selectedProjectsOrder is ProjectsOrder.Deadline,
            onSelect = { onOrderChange(ProjectsOrder.Deadline(selectedProjectsOrder.orderType)) },
            modifier = Modifier.fillMaxWidth(),
        )
        Spacer(Modifier.height(Space24dp))

        Text(
            text = stringResource(id = R.string.orderBy),
            style = MaterialTheme.typography.titleMedium.copy(color = MaterialTheme.colorScheme.inverseSurface),
            modifier = Modifier.fillMaxWidth(),
        )
        Spacer(Modifier.height(Space12dp))

        DefaultRadioButton(
            text = stringResource(id = R.string.descending),
            isSelected = selectedProjectsOrder.orderType is OrderType.Descending,
            onSelect = { onOrderChange(selectedProjectsOrder.copy(OrderType.Descending)) },
            modifier = Modifier.fillMaxWidth(),
        )

        DefaultRadioButton(
            text = stringResource(id = R.string.ascending),
            isSelected = selectedProjectsOrder.orderType is OrderType.Ascending,
            onSelect = { onOrderChange(selectedProjectsOrder.copy(OrderType.Ascending)) },
            modifier = Modifier.fillMaxWidth(),
        )

        Spacer(Modifier.height(Space24dp))

        // need a way to compare whether the selected and project order have the same value
        PrimaryButton(
            modifier = Modifier.fillMaxWidth(),
            text = stringResource(id = R.string.applyFilters),
            onClick = onFiltersApplied,
            isEnabled = projectsOrder !is ProjectsOrder.DateAdded ||
                projectsOrder.orderType !is OrderType.Descending ||
                selectedProjectsOrder !is ProjectsOrder.DateAdded ||
                selectedProjectsOrder.orderType !is OrderType.Descending
        )

        Spacer(Modifier.height(Space8dp))

        SecondaryButton(
            modifier = Modifier.fillMaxWidth(),
            text = stringResource(id = R.string.resetFilters),
            onClick = onFiltersReset,
            isEnabled = projectsOrder !is ProjectsOrder.DateAdded || projectsOrder.orderType !is OrderType.Descending
        )
    }
}

@Preview
@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES, showBackground = true)
@Composable
fun FilterBottomSheetContent() {
    ProjectTrackingTheme {
        FilterBottomSheetContent(
            onOrderChange = {},
            onFiltersApplied = {},
            onFiltersReset = {},
            projectsOrder = ProjectsOrder.Name(OrderType.Ascending),
            selectedProjectsOrder = ProjectsOrder.Name(OrderType.Ascending),
        )
    }
}
