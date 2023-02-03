package com.mumbicodes.projectie.presentation.all_milestones.components

import android.content.res.Configuration
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.mumbicodes.projectie.domain.model.Milestone
import com.mumbicodes.projectie.domain.model.Task
import com.mumbicodes.projectie.domain.relations.MilestoneWithTasks
import com.mumbicodes.projectie.presentation.components.TagItem
import com.mumbicodes.projectie.presentation.components.provideShadowColor
import com.mumbicodes.projectie.presentation.projectDetails.components.TaskItem
import com.mumbicodes.projectie.presentation.theme.ProjectTrackingTheme
import com.mumbicodes.projectie.presentation.theme.Space16dp
import com.mumbicodes.projectie.presentation.theme.Space8dp
import com.mumbicodes.projectie.presentation.util.getNumberOfDays
import com.mumbicodes.projectie.presentation.util.toDateAsString

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AllMilestonesItem(
    modifier: Modifier = Modifier,
    milestoneWithTasks: MilestoneWithTasks,
    projectName: String,
    onClickMilestone: (MilestoneWithTasks) -> Unit = {},
) {
    Surface(
        shape = MaterialTheme.shapes.small,
        onClick = {
            onClickMilestone(milestoneWithTasks)
        },
        modifier = modifier
            .fillMaxWidth()
            .shadow(
                elevation = 60.dp,
                shape = MaterialTheme.shapes.small,
                ambientColor = provideShadowColor(),
                spotColor = provideShadowColor()
            )
            .background(color = MaterialTheme.colorScheme.surface),
    ) {

        Column(modifier = Modifier.padding(Space16dp)) {
            Text(
                modifier = Modifier.fillMaxWidth(),
                text = milestoneWithTasks.milestone.milestoneTitle,
                style = MaterialTheme.typography.headlineMedium.copy(MaterialTheme.colorScheme.onSurface)
            )

            Text(
                modifier = Modifier.fillMaxWidth(),
                text = projectName,
                style = MaterialTheme.typography.labelSmall.copy(MaterialTheme.colorScheme.inverseOnSurface),
            )

            Spacer(modifier = Modifier.height(Space8dp))

            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(2.dp)
            ) {
                milestoneWithTasks.tasks.forEach { task ->
                    TaskItem(modifier = Modifier, task = task, descIsVisible = false)
                }
            }
            Spacer(modifier = Modifier.height(Space8dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = buildAnnotatedString {
                        withStyle(
                            style = MaterialTheme.typography.labelSmall.toSpanStyle()
                                .copy(MaterialTheme.colorScheme.primary)
                        ) {
                            append(milestoneWithTasks.milestone.milestoneSrtDate.toDateAsString("dd MMM yyyy"))
                        }
                        withStyle(
                            style = MaterialTheme.typography.labelSmall.toSpanStyle()
                                .copy(MaterialTheme.colorScheme.primary)
                        ) {
                            append(" to ")
                        }
                        withStyle(
                            style = MaterialTheme.typography.labelSmall.toSpanStyle()
                                .copy(MaterialTheme.colorScheme.primary)
                        ) {
                            append(milestoneWithTasks.milestone.milestoneEndDate.toDateAsString("dd MMM yyyy"))
                        }
                    }
                )

                TagItem(numberOfDaysRemaining = milestoneWithTasks.milestone.milestoneEndDate.getNumberOfDays())
            }
        }
    }
}

@Preview
@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES, showBackground = true)
@Composable
fun AllMilestonesItemPreview() {
    ProjectTrackingTheme {
        AllMilestonesItem(
            modifier = Modifier,
            projectName = "Project name",
            milestoneWithTasks = MilestoneWithTasks(
                Milestone(
                    projectId = 1,
                    milestoneId = 2,
                    milestoneTitle = "This is a milestone title",
                    milestoneSrtDate = 19236,
                    milestoneEndDate = 19247,
                    status = "Not started",
                ),
                tasks = listOf(
                    Task(
                        taskId = 1,
                        milestoneId = 2,
                        taskTitle = "Display Projects",
                        taskDesc = "Display Projects",
                        status = true
                    ),
                    Task(
                        taskId = 2,
                        milestoneId = 2,
                        taskTitle = "Bottom navigation",
                        taskDesc = "Bottom navigation",
                        status = false
                    ),
                    Task(
                        taskId = 3,
                        milestoneId = 2,
                        taskTitle = "Display Projects",
                        taskDesc = "Display Projects",
                        status = true
                    ),
                )
            )
        )
    }
}
