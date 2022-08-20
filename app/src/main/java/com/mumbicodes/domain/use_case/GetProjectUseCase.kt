package com.mumbicodes.domain.use_case

import com.mumbicodes.domain.model.Project
import com.mumbicodes.domain.repository.ProjectsRepository
import com.mumbicodes.domain.util.OrderType
import com.mumbicodes.domain.util.ProjectsOrder
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class GetProjectUseCase(
    private val repository: ProjectsRepository,
) {
    /**
     * Added logic to get projects and sort the projects
     *
     * By default, the order is the date added
     * */
    operator fun invoke(
        projectStatus: String,
        projectOrder: ProjectsOrder = ProjectsOrder.DateAdded(OrderType.Descending),
    ): Flow<List<Project>> {
        return repository.getAllProjectsBasedOnStatus(projectStatus)
            .map { projects ->
                when (projectOrder.orderType) {
                    is OrderType.Ascending -> {
                        when (projectOrder) {
                            is ProjectsOrder.Name -> projects.sortedBy { it.projectName.lowercase() }
                            is ProjectsOrder.Deadline -> projects.sortedBy { it.projectDeadline }
                            is ProjectsOrder.DateAdded -> projects.sortedBy { it.timeStamp }
                        }
                    }
                    is OrderType.Descending -> {
                        when (projectOrder) {
                            is ProjectsOrder.Name -> projects.sortedByDescending { it.projectName.lowercase() }
                            is ProjectsOrder.Deadline -> projects.sortedByDescending { it.projectDeadline }
                            is ProjectsOrder.DateAdded -> projects.sortedByDescending { it.timeStamp }
                        }
                    }
                }
            }
    }
}
