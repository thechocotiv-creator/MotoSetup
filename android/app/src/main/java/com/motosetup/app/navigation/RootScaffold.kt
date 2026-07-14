package com.motosetup.app.navigation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.motosetup.app.feature.consigliai.ConsigliAIScreen
import com.motosetup.app.feature.home.HomeScreen
import com.motosetup.app.feature.profilo.ProfiloScreen
import com.motosetup.app.feature.setup.SetupScreen
import com.motosetup.app.ui.theme.AppColor
import com.motosetup.app.ui.theme.AppSpacing
import com.motosetup.app.ui.theme.LocalHazeState
import com.motosetup.app.ui.theme.appGlassBackdrop
import dev.chrisbanes.haze.HazeState

/**
 * Shell dei 4 tab: contenuto sotto, GlassTabBar sovrapposta.
 * Fase 0: switch di stato semplice, nessun back stack ancora (Nav3 arriva in
 * Fase 2 quando esistono destinazioni push reali). Vedi CLAUDE.md Android.
 */
@Composable
fun RootScaffold() {
    var selectedTab by remember { mutableStateOf(AppTab.Home) }
    val hazeState = remember { HazeState() }

    CompositionLocalProvider(LocalHazeState provides hazeState) {
        BoxWithConstraints(
            modifier = Modifier
                .fillMaxSize()
                .background(AppColor.background),
        ) {
            Box(modifier = Modifier.fillMaxSize().appGlassBackdrop()) {
                when (selectedTab) {
                    AppTab.Home -> HomeScreen()
                    AppTab.Setup -> SetupScreen()
                    AppTab.ConsigliAI -> ConsigliAIScreen()
                    AppTab.Profilo -> ProfiloScreen()
                }
            }

            GlassTabBar(
                selected = selectedTab,
                onSelect = { selectedTab = it },
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = AppSpacing.sm),
            )
        }
    }
}
