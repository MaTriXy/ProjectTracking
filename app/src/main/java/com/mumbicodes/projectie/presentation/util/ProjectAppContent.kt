package com.mumbicodes.projectie.presentation.util

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import com.mumbicodes.projectie.presentation.designsystem.components.BottomBar
import com.mumbicodes.projectie.presentation.designsystem.components.NavigationRailComposable
import com.mumbicodes.projectie.presentation.util.navigation.ProjectNavHost
import com.mumbicodes.projectie.presentation.util.navigation.Screens

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProjectAppContent(
    modifier: Modifier = Modifier,
    navigationType: NavigationType,
    contentType: ContentType,
    navController: NavHostController,
    navigateToDestination: (String) -> Unit,
    onDrawerClicked: () -> Unit,
    windowWidthSizeClass: WindowWidthSizeClass,
) {

    val bottomBarState = rememberSaveable { (mutableStateOf(false)) }

    Row(modifier = Modifier.fillMaxSize()) {
        AnimatedVisibility(visible = navigationType == NavigationType.NAVIGATION_RAIL) {
            NavigationRailComposable(
                navController = navController,
                onItemClick = {
                    navigateToDestination(it.route)
                },
                onAddClick = {
                    navigateToDestination("${Screens.AddAndEditScreens.route}/${-1}")
                }
            )
        }
      /*  Column(modifier = modifier.fillMaxSize()) {
            ProjectNavHost(
                modifier = Modifier.weight(1f),
                navController = navController,
                isBottomBarVisible = {
                    bottomBarState.value = it
                },
                contentType = contentType,
                windowWidthSizeClass = windowWidthSizeClass
            )
            AnimatedVisibility(visible = navigationType == NavigationType.BOTTOM_NAVIGATION) {
                if (bottomBarState.value)
                    BottomBar(
                        navController = navController,
                        onItemClick = {
                            val route = if (it is Screens.AddAndEditScreens) {
                                "${Screens.AddAndEditScreens.route}/${-1}"
                            } else {
                                it.route
                            }
                            navigateToDestination(route)
                        }
                    )
            }
        }*/

        Scaffold(
            bottomBar = {
                AnimatedVisibility(visible = navigationType == NavigationType.BOTTOM_NAVIGATION) {
                    if (bottomBarState.value)
                        BottomBar(
                            navController = navController,
                            onItemClick = {
                                val route = if (it is Screens.AddAndEditScreens) {
                                    "${Screens.AddAndEditScreens.route}/${-1}"
                                } else {
                                    it.route
                                }
                                navigateToDestination(route)
                            }
                        )
                }
            },
            modifier = Modifier.background(color = MaterialTheme.colorScheme.background),
        ) { paddingValues ->

            ProjectNavHost(
                modifier = Modifier
                    .padding(paddingValues)
                    .weight(1f),
                navController = navController,
                isBottomBarVisible = {
                    bottomBarState.value = it
                },
                contentType = contentType,
                windowWidthSizeClass = windowWidthSizeClass
            )
        }
    }
}
