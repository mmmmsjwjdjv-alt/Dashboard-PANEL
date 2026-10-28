package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.components.BootSplash
import com.example.ui.components.MaxoBottomBar
import com.example.ui.components.MaxoTopBar
import com.example.ui.components.ToastHost
import com.example.ui.screens.LinkScreen
import com.example.ui.screens.OverviewScreen
import com.example.ui.screens.ShizukuScreen
import com.example.ui.screens.UsersScreen
import com.example.ui.theme.MaxoBlack
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.viewmodel.DashboardTab
import com.example.ui.viewmodel.DashboardViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                MaxoApp()
            }
        }
    }
}

@Composable
fun MaxoApp(viewModel: DashboardViewModel = viewModel()) {
    val state by viewModel.uiState.collectAsState()

    // Handle back press to return to Overview from secondary tabs
    BackHandler(enabled = state.currentTab != DashboardTab.OVERVIEW) {
        viewModel.switchTab(DashboardTab.OVERVIEW)
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaxoBlack)
    ) {
        // Glowing Cyber Particles & Digital Grid Background
        com.example.ui.components.CyberParticleBackground(
            modifier = Modifier.fillMaxSize()
        )

        Scaffold(
            containerColor = androidx.compose.ui.graphics.Color.Transparent,
            topBar = {
                MaxoTopBar(
                    clockString = state.currentTimeString,
                    isLoading = state.isTopLoading
                )
            },
            bottomBar = {
                MaxoBottomBar(
                    currentTab = state.currentTab,
                    onTabSelected = { viewModel.switchTab(it) }
                )
            }
        ) { innerPadding ->
            Crossfade(
                targetState = state.currentTab,
                animationSpec = tween(durationMillis = 200),
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                label = "tab_crossfade"
            ) { tab ->
                when (tab) {
                    DashboardTab.OVERVIEW -> OverviewScreen(
                        state = state,
                        onNavigate = { viewModel.switchTab(it) }
                    )
                    DashboardTab.SHIZUKU -> ShizukuScreen(
                        viewModel = viewModel
                    )
                    DashboardTab.LINK -> LinkScreen(
                        viewModel = viewModel
                    )
                    DashboardTab.USERS -> UsersScreen(
                        viewModel = viewModel
                    )
                }
            }
        }

        // Floating cyber toast messages
        ToastHost(toastFlow = viewModel.toastEvents)

        // Startup boot animation overlay
        BootSplash(
            isVisible = state.isBooting,
            onDismiss = { viewModel.dismissBoot() }
        )
    }
}
