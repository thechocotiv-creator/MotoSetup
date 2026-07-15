package com.motosetup.app.navigation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.ui.NavDisplay
import com.motosetup.app.feature.checklist.ChecklistPistaScreen
import com.motosetup.app.feature.consigliai.ConsigliAIScreen
import com.motosetup.app.feature.home.HomeScreen
import com.motosetup.app.feature.manutenzione.ManutenzioneMotoScreen
import com.motosetup.app.feature.profilo.ProfiloScreen
import com.motosetup.app.feature.sessioni.DettaglioRunScreen
import com.motosetup.app.feature.sessioni.NuovaSessioneScreen
import com.motosetup.app.feature.sessioni.TutteLeSessioniScreen
import com.motosetup.app.feature.setup.SetupScreen
import com.motosetup.app.ui.theme.AppColor
import com.motosetup.app.ui.theme.AppSpacing
import com.motosetup.app.ui.theme.LocalHazeState
import com.motosetup.app.ui.theme.appGlassBackdrop
import dev.chrisbanes.haze.HazeState

/**
 * Shell dei 4 tab: NavDisplay renderizza il back stack del tab selezionato,
 * GlassTabBar sovrapposta, overlay di sheet/dialog/paywall sopra tutto.
 * Ogni tab mantiene il proprio back stack (SnapshotStateList<AppRoute>,
 * remember hoistato sopra il when su selectedTab) così lo stato di
 * navigazione sopravvive allo switch tab. Vedi CLAUDE.md Android, "Navigazione".
 */
@Composable
fun RootScaffold() {
    var selectedTab by remember { mutableStateOf(AppTab.Home) }
    val hazeState = remember { HazeState() }

    val homeBackStack = remember { mutableStateListOf<AppRoute>(AppRoute.HomeRoot) }
    val setupBackStack = remember { mutableStateListOf<AppRoute>(AppRoute.SetupRoot) }
    val consigliAIBackStack = remember { mutableStateListOf<AppRoute>(AppRoute.ConsigliAIRoot) }
    val profiloBackStack = remember { mutableStateListOf<AppRoute>(AppRoute.ProfiloRoot) }

    val currentBackStack = when (selectedTab) {
        AppTab.Home -> homeBackStack
        AppTab.Setup -> setupBackStack
        AppTab.ConsigliAI -> consigliAIBackStack
        AppTab.Profilo -> profiloBackStack
    }

    var activeSheet by remember { mutableStateOf<AppSheet?>(null) }
    var activeDialog by remember { mutableStateOf<AppDialog?>(null) }
    var paywallReason by remember { mutableStateOf<PaywallReason?>(null) }

    val actions = remember(currentBackStack) {
        AppNavActions(
            navigate = { route -> currentBackStack.add(route) },
            navigateBack = {
                if (currentBackStack.size > 1) currentBackStack.removeAt(currentBackStack.lastIndex)
            },
            openSheet = { activeSheet = it },
            closeSheet = { activeSheet = null },
            openDialog = { activeDialog = it },
            closeDialog = { activeDialog = null },
            showPaywall = { paywallReason = it },
        )
    }

    CompositionLocalProvider(LocalHazeState provides hazeState, LocalAppNavActions provides actions) {
        BoxWithConstraints(
            modifier = Modifier
                .fillMaxSize()
                .background(AppColor.background),
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .appGlassBackdrop()
                    .windowInsetsPadding(WindowInsets.statusBars)
                    .padding(bottom = AppSpacing.tabBarClearance),
            ) {
                NavDisplay(
                    backStack = currentBackStack,
                    onBack = { actions.navigateBack() },
                    entryProvider = entryProvider {
                        entry<AppRoute.HomeRoot> { HomeScreen() }
                        entry<AppRoute.SetupRoot> { SetupScreen() }
                        entry<AppRoute.ConsigliAIRoot> { ConsigliAIScreen() }
                        entry<AppRoute.ProfiloRoot> { ProfiloScreen() }
                        entry<AppRoute.ChecklistPista> { ChecklistPistaScreen() }
                        entry<AppRoute.DettaglioRun> { key -> DettaglioRunScreen(key.sessionId, key.runId) }
                        entry<AppRoute.ManutenzioneMoto> { key -> ManutenzioneMotoScreen(key.bikeId) }
                        entry<AppRoute.NuovaSessione> { NuovaSessioneScreen() }
                        entry<AppRoute.TutteLeSessioni> { TutteLeSessioniScreen() }
                    },
                )
            }

            GlassTabBar(
                selected = selectedTab,
                onSelect = { selectedTab = it },
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .windowInsetsPadding(WindowInsets.navigationBars)
                    .padding(bottom = AppSpacing.sm),
            )

            AppBottomSheetHost(sheet = activeSheet, onDismiss = { activeSheet = null })
            AppAlertDialogHost(dialog = activeDialog, onDismiss = { activeDialog = null })
            PaywallSheet(
                reason = paywallReason,
                onDismiss = { paywallReason = null },
                onUnlockPremium = {
                    paywallReason = null
                    activeSheet = AppSheet.Abbonamento
                },
            )
        }
    }
}
