package com.mumbicodes.domain.relations

import androidx.room.Embedded
import androidx.room.Relation
import com.mumbicodes.domain.model.Project

data class ProjectWithMilestones(
    @Embedded val project: Project,
    @Relation(
        parentColumn = "projectId",
        entityColumn = "projectId"
    )
    val milestones: List<MilestoneWithTasks>
)
