package com.mumbicodes.presentation.all_milestones

import android.app.Application
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mumbicodes.R
import com.mumbicodes.domain.model.ProjectName
import com.mumbicodes.domain.relations.MilestoneWithTasks
import com.mumbicodes.domain.use_case.milestones.MilestonesUseCases
import com.mumbicodes.domain.use_case.projects.ProjectsUseCases
import com.mumbicodes.domain.util.AllMilestonesOrder
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
    private val appContext: Application,
) : ViewModel() {
    private val _state = mutableStateOf(AllMilestonesStates())
    val state = _state

    private val _searchParam = mutableStateOf("")
    val searchParam = _searchParam

    private var getMilestonesJob: Job? = null
    private var getProjectsJob: Job? = null

    private val _uiEvents = MutableSharedFlow<AllMilestonesUIEvents>()
    val uiEvents = _uiEvents

    private val _projectNames: MutableState< List<ProjectName>> = mutableStateOf(emptyList())

    init {
        getAllMilestones(
            milestonesOrder = state.value.milestonesOrder,
            milestoneStatus = state.value.selectedMilestoneStatus,
        )
    }

    private fun getAllMilestones(milestonesOrder: AllMilestonesOrder, milestoneStatus: String) {
        getMilestonesJob?.cancel()

        getMilestonesJob = milestonesUseCases.getAllMilestonesUseCase(milestonesOrder)
            .onEach { milestonesWithTasks ->
                _state.value = _state.value.copy(
                    milestones = milestonesWithTasks,
                    milestonesOrder = milestonesOrder,
                )
                milestonesWithTasks.filterMilestones(milestoneStatus, searchParam.value)
                getProjectNameAndId()
            }
            .launchIn(viewModelScope)
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
            state.value.filteredMilestones.forEach { milestoneWithTasks ->
                if (milestoneWithTasks.milestone.projectId == projectName.projectId) {
                    mappedMilestonesWithProjectName[milestoneWithTasks.milestone.milestoneId] =
                        projectName.projectName
                }
            }
        }

        _state.value = _state.value.copy(
            milestonesProjectName = mappedMilestonesWithProjectName
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
                if (state.value.milestonesOrder::class == milestonesEvents.milestonesOrder::class) {
                    return
                }

                getAllMilestones(
                    milestonesOrder = milestonesEvents.milestonesOrder,
                    milestoneStatus = state.value.selectedMilestoneStatus
                )
            }
            is AllMilestonesEvents.ResetMilestonesOrder -> {
                getAllMilestones(
                    milestonesOrder = milestonesEvents.milestonesOrder,
                    milestoneStatus = state.value.selectedMilestoneStatus
                )
            }
            is AllMilestonesEvents.SearchMilestone -> {
                _searchParam.value = milestonesEvents.searchParam

                state.value.milestones.filterMilestones(
                    milestoneStatus = state.value.selectedMilestoneStatus,
                    searchParam = searchParam.value
                )
            }
            is AllMilestonesEvents.SelectMilestoneStatus -> {
                if (state.value.selectedMilestoneStatus == milestonesEvents.milestoneStatus) {
                    return
                }
                state.value.milestones.filterMilestones(
                    milestoneStatus = milestonesEvents.milestoneStatus,
                    searchParam = searchParam.value
                )
            }

            is AllMilestonesEvents.PassMilestone -> {
                _state.value = _state.value.copy(
                    mileStone = milestonesEvents.milestone
                )
            }
        }
    }

    private fun List<MilestoneWithTasks>.filterMilestones(
        milestoneStatus: String,
        searchParam: String,
    ) {
        _state.value = _state.value.copy(
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
        )
    }
}
