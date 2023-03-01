package com.mumbicodes.projectie.presentation.all_milestones

import android.app.Application
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.toMutableStateList
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mumbicodes.projectie.R
import com.mumbicodes.projectie.domain.model.Milestone
import com.mumbicodes.projectie.domain.model.Project
import com.mumbicodes.projectie.domain.model.ProjectName
import com.mumbicodes.projectie.domain.model.Task
import com.mumbicodes.projectie.domain.relations.MilestoneWithTasks
import com.mumbicodes.projectie.domain.use_case.milestones.MilestonesUseCases
import com.mumbicodes.projectie.domain.use_case.projects.ProjectsUseCases
import com.mumbicodes.projectie.domain.use_case.tasks.TasksUseCases
import com.mumbicodes.projectie.domain.util.AllMilestonesOrder
import com.mumbicodes.projectie.presentation.add_edit_milestone.TaskState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AllMilestonesViewModel @Inject constructor(
    private val milestonesUseCases: MilestonesUseCases,
    private val projectsUseCases: ProjectsUseCases,
    private val tasksUseCase: TasksUseCases,
    private val appContext: Application,
) : ViewModel() {
    private val _state = mutableStateOf(AllMilestonesStates())
    val state = _state

    private val _screenStates = mutableStateOf(ScreenStates())
    val screenStates = _screenStates

    private val _searchParam = mutableStateOf("")
    val searchParam = _searchParam

    private var getMilestonesJob: Job? = null
    private var getProjectsJob: Job? = null
    private var getAllMilestonesJob: Job? = null

    private val _uiEvents = MutableSharedFlow<AllMilestonesUIEvents>()
    val uiEvents = _uiEvents

    private val _projectNames: MutableState<List<ProjectName>> = mutableStateOf(emptyList())

    private var _stateTasks = mutableListOf<TaskState>().toMutableStateList()
    val stateTasks: List<TaskState> = _stateTasks

    init {
        getAllMilestones(
            milestonesOrder = screenStates.value.data.milestonesOrder,
            milestoneStatus = screenStates.value.data.selectedMilestoneStatus,
        )
    }

    private fun getAllMilestones(milestonesOrder: AllMilestonesOrder, milestoneStatus: String) {
        viewModelScope.launch {
            _screenStates.value = screenStates.value.copy(
                isLoading = true
            )

            getMilestonesJob?.cancel()

            getMilestonesJob = milestonesUseCases.getAllMilestonesUseCase(milestonesOrder)
                .onEach { milestonesWithTasks ->
                    _screenStates.value = screenStates.value.copy(
                        data = screenStates.value.data.copy(
                            milestones = milestonesWithTasks,
                            milestonesOrder = milestonesOrder,
                        ),
                        isLoading = false,
                    )
                    milestonesWithTasks.filterMilestones(milestoneStatus, searchParam.value)
                    getProjectNameAndId()
                }
                .launchIn(viewModelScope)
        }
    }

    private fun getProjectNameAndId() {
        getProjectsJob?.cancel()
        getProjectsJob = projectsUseCases.getProjectNameAndIdUseCase()
            .onEach { projectNames ->
                _projectNames.value = projectNames

                mapProjectNameWithMilestoneId()
            }
            .launchIn(viewModelScope)
    }

    private fun mapProjectNameWithMilestoneId() {

        val mappedMilestonesWithProjectName = mutableMapOf<Int, String>()

        _projectNames.value.forEach { projectName ->
            screenStates.value.data.filteredMilestones.forEach { milestoneWithTasks ->
                if (milestoneWithTasks.milestone.projectId == projectName.projectId) {
                    mappedMilestonesWithProjectName[milestoneWithTasks.milestone.milestoneId] =
                        projectName.projectName
                }
            }
        }
        _screenStates.value = screenStates.value.copy(
            data = screenStates.value.data.copy(
                milestonesProjectName = mappedMilestonesWithProjectName
            ),
            isLoading = false,
        )
    }

    fun onEvent(milestonesEvents: AllMilestonesEvents) {
        when (milestonesEvents) {
            is AllMilestonesEvents.DeleteMilestone -> {
                viewModelScope.launch {
                    milestonesUseCases.deleteMilestoneUseCase(milestonesEvents.milestone)

                    uiEvents.emit(AllMilestonesUIEvents.DeleteMilestone)
                }
            }
            is AllMilestonesEvents.OrderMilestones -> {
                if (screenStates.value.data.milestonesOrder::class == milestonesEvents.milestonesOrder::class) {
                    return
                }

                getAllMilestones(
                    milestonesOrder = milestonesEvents.milestonesOrder,
                    milestoneStatus = screenStates.value.data.selectedMilestoneStatus
                )
            }
            is AllMilestonesEvents.ResetMilestonesOrder -> {
                getAllMilestones(
                    milestonesOrder = milestonesEvents.milestonesOrder,
                    milestoneStatus = screenStates.value.data.selectedMilestoneStatus
                )
            }
            is AllMilestonesEvents.SearchMilestone -> {
                _searchParam.value = milestonesEvents.searchParam

                screenStates.value.data.milestones.filterMilestones(
                    milestoneStatus = screenStates.value.data.selectedMilestoneStatus,
                    searchParam = searchParam.value
                )
            }
            is AllMilestonesEvents.SelectMilestoneStatus -> {
                if (screenStates.value.data.selectedMilestoneStatus == milestonesEvents.milestoneStatus) {
                    return
                }

                screenStates.value.data.milestones.filterMilestones(
                    milestoneStatus = milestonesEvents.milestoneStatus,
                    searchParam = searchParam.value
                )
            }

            is AllMilestonesEvents.PassMilestone -> {
                getMilestoneById(milestonesEvents.milestoneId)
            }
            is AllMilestonesEvents.ToggleTaskState -> {
                _stateTasks.find {
                    it.taskId == milestonesEvents.taskId
                }?.let { foundTaskState ->
                    foundTaskState.statusState = !foundTaskState.statusState
                }

                val tasks = tasksUseCase.transformTasksUseCase.transformTaskStatesToTasks(
                    stateTasks
                )
                checkAndUpdateMilestoneStatus(tasks)
                // Update db
                viewModelScope.launch {
                    tasksUseCase.addTasksUseCase(
                        tasks
                    )
                    checkAndUpdateProjectStatus()
                }
            }
        }
    }

    private fun List<MilestoneWithTasks>.filterMilestones(
        milestoneStatus: String,
        searchParam: String,
    ) {
        _screenStates.value = screenStates.value.copy(
            data = screenStates.value.data.copy(
                filteredMilestones = if (milestoneStatus == appContext.getString(R.string.all)) {
                    this.filter {
                        it.milestone.milestoneTitle.contains(searchParam)
                    }
                } else {
                    this.filter {
                        it.milestone.status == milestoneStatus
                    }.filter {
                        it.milestone.milestoneTitle.contains(searchParam)
                    }
                },
                selectedMilestoneStatus = milestoneStatus,
            ),
            isLoading = false,

        )
    }

    private fun getMilestoneById(milestoneId: Int) {
        getAllMilestonesJob?.cancel()
        getAllMilestonesJob = milestonesUseCases.getMilestoneByIdWithTasksUseCase(milestoneId)
            .onEach { milestoneWithTask ->
                _screenStates.value = screenStates.value.copy(
                    data = screenStates.value.data.copy(
                        mileStone = milestoneWithTask ?: MilestoneWithTasks(
                            milestone = Milestone(
                                projectId = 0,
                                milestoneId = 0,
                                milestoneTitle = "",
                                milestoneSrtDate = 0,
                                milestoneEndDate = 0,
                                status = "",
                            ),
                            tasks = listOf()
                        )
                    ),
                    isLoading = false,
                )
                // adding tasks to state
                _stateTasks.apply {
                    clear()
                    addAll(
                        tasksUseCase.transformTasksUseCase.transformTasksToTaskStates(
                            milestoneWithTask!!.tasks
                        )
                    )
                }
            }
            .launchIn(viewModelScope)
    }

    private fun checkAndUpdateMilestoneStatus(tasks: List<Task>) {

        viewModelScope.launch {
            val currentMilestoneStatus =
                milestonesUseCases.checkMilestoneStatusUseCase.invoke(tasks)
            milestonesUseCases.addMilestoneUseCase(
                screenStates.value.data.mileStone.milestone.copy(
                    status = currentMilestoneStatus
                )
            )
        }
    }

    private fun checkAndUpdateProjectStatus() {
        viewModelScope.launch {

            val projectId = screenStates.value.data.mileStone.milestone.projectId
            val projectStatus =
                projectsUseCases.checkProjectStatusUseCase.invoke(projectId)

            val project: Project = projectsUseCases.getProjectByIdUseCase(projectId)

            projectsUseCases.updateProjectsUseCase.invoke(
                project.copy(projectStatus = projectStatus)
            )
        }
    }
}
