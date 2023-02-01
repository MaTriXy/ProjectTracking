package com.mumbicodes.projectie.presentation.activity

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.calculateWindowSizeClass
import androidx.compose.runtime.collectAsState
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.window.layout.FoldingFeature
import androidx.window.layout.WindowInfoTracker
import com.mumbicodes.projectie.presentation.splash.SplashScreenViewModel
import com.mumbicodes.projectie.presentation.theme.*
import com.mumbicodes.projectie.presentation.theme.ProjectTrackingTheme
import com.mumbicodes.projectie.presentation.util.DevicePosture
import com.mumbicodes.projectie.presentation.util.isBookPosture
import com.mumbicodes.projectie.presentation.util.isSeparating
import com.mumbicodes.projectie.presentation.util.navigation.ProjectAppComposable
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var splashScreenViewModel: SplashScreenViewModel

    @OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        installSplashScreen().setKeepOnScreenCondition {
            !splashScreenViewModel.isLoading.value
        }

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
                val windowSizeClass = calculateWindowSizeClass(activity = this)
                // observe device posture
                val devicePosture = devicePostureFlow.collectAsState().value

                /*Box(modifier = Modifier.fillMaxSize()) {
                    MainScreen()
                }*/
                ProjectAppComposable(
                    windowWidthSizeClass = windowSizeClass.widthSizeClass,
                    foldingPosture = devicePosture,
                )
            }
        }
    }
}

/*@OptIn(ExperimentalMaterial3Api::class)
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
}*/
