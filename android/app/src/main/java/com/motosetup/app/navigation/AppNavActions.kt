package com.motosetup.app.navigation

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf

/**
 * Azioni di navigazione/overlay esposte a qualunque schermata annidata, senza
 * prop-drilling attraverso ogni livello — stesso pattern di LocalHazeState in
 * Glass.kt. Fornito da RootScaffold, che è l'unico a conoscere il back stack
 * del tab corrente e lo stato di sheet/dialog/paywall.
 */
@Immutable
data class AppNavActions(
    val navigate: (AppRoute) -> Unit,
    val navigateBack: () -> Unit,
    val openSheet: (AppSheet) -> Unit,
    val closeSheet: () -> Unit,
    val openDialog: (AppDialog) -> Unit,
    val closeDialog: () -> Unit,
    val showPaywall: (PaywallReason) -> Unit,
)

val LocalAppNavActions = staticCompositionLocalOf<AppNavActions> {
    error("LocalAppNavActions non fornito: deve essere impostato da RootScaffold")
}
