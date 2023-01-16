package com.mumbicodes.presentation.splash

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mumbicodes.domain.use_case.onBoarding.OnBoardingUseCases
import com.mumbicodes.presentation.util.navigation.Screens
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class OnBoardingViewModel @Inject constructor(
    private val onBoardingUseCases: OnBoardingUseCases
) : ViewModel() {

    private val _startDestination: MutableState<String> = mutableStateOf(Screens.OnBoardingScreen.route)
    val startDestination = _startDestination

    init {
        viewModelScope.launch {
            onBoardingUseCases.readOnBoardingStateUseCase().collect { isOnBoarded ->
                if (isOnBoarded) {
                    _startDestination.value = Screens.AllProjectsScreens.route
                } else {
                    _startDestination.value = Screens.OnBoardingScreen.route
                }
            }
        }
    }
    fun saveOnBoardingState(isOnboarded: Boolean) {
        viewModelScope.launch {
            onBoardingUseCases.saveOnBoardingStateUseCase(isOnBoarded = isOnboarded)
        }
    }
}
