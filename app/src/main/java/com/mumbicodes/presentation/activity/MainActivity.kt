package com.mumbicodes.presentation.activity

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.material3.Scaffold
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.material3.windowsizeclass.calculateWindowSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.rememberNavController
import androidx.window.layout.FoldingFeature
import androidx.window.layout.WindowInfoTracker
import com.mumbicodes.presentation.components.BottomBar
import com.mumbicodes.presentation.theme.*
import com.mumbicodes.presentation.util.DevicePosture
import com.mumbicodes.presentation.util.isBookPosture
import com.mumbicodes.presentation.util.isSeparating
import com.mumbicodes.presentation.util.navigation.ProjectNavHost
import com.mumbicodes.presentation.util.navigation.Screens
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    @OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Get device fold posture
        val devicePostureFlow = WindowInfoTracker.getOrCreate(this).windowLayoutInfo(this)
            .flowWithLifecycle(this.lifecycle)
            .map { layoutInfo ->
                val foldingFeature =
                    layoutInfo.displayFeatures
                        .filterIsInstance<FoldingFeature>()
                        .firstOrNull()
                when {
                    isBookPosture(foldingFeature) ->
                        DevicePosture.BookPosture(foldingFeature.bounds)

                    isSeparating(foldingFeature) ->
                        DevicePosture.Separating(foldingFeature.bounds, foldingFeature.orientation)

                    else -> DevicePosture.NormalPosture
                }
            }
            .stateIn(
                scope = lifecycleScope,
                started = SharingStarted.Eagerly,
                initialValue = DevicePosture.NormalPosture
            )

        setContent {
            ProjectTrackingTheme {
                val windowSize = calculateWindowSizeClass(activity = this)
                // observe device posture
                val devicePosture = devicePostureFlow.collectAsState().value

                Box(modifier = Modifier.fillMaxSize()) {
                    MainScreen()
                }
                ProjectsApp(
                    windowSize = windowSize.widthSizeClass,
                    foldingPosture = devicePosture,
                )
            }
        }
    }
}

@Composable
fun ProjectsApp(
    windowSize: WindowWidthSizeClass,
    foldingPosture: DevicePosture,
) {
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen() {
    val navController = rememberNavController()

    val bottomBarState = rememberSaveable { (mutableStateOf(true)) }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        bottomBar = {
            if (bottomBarState.value)
                BottomBar(
                    navController = navController,
                    onItemClick = {
                        val route = if (it is Screens.AddAndEditScreens) {
                            "${Screens.AddAndEditScreens.route}/${-1}"
                        } else {
                            it.route
                        }
                        navController.navigate(route) {
                            popUpTo(navController.graph.findStartDestination().id) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                )
        },
    ) { innerPadding ->

        ProjectNavHost(
            modifier = Modifier.padding(innerPadding),
            navController = navController,
            isBottomBarVisible = {
                bottomBarState.value = it
            }
        )
    }
}

// TODO research on how to reduce the icon and text spacing and the whole margin
