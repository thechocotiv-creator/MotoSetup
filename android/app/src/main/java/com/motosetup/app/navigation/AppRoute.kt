package com.motosetup.app.navigation

/**
 * Destinazioni del back stack di navigazione (una `SnapshotStateList<AppRoute>`
 * per tab, vedi CLAUDE.md Android). I quattro *Root sono le schermate dei tab
 * stessi, sempre in fondo allo stack del proprio tab; le altre sono le
 * destinazioni push elencate in CLAUDE.md ("Navigazione — mapping presentazioni").
 */
sealed interface AppRoute {
    data object HomeRoot : AppRoute
    data object SetupRoot : AppRoute
    data object ConsigliAIRoot : AppRoute
    data object ProfiloRoot : AppRoute

    data object ChecklistPista : AppRoute
    data class DettaglioRun(val sessionId: String, val runId: String) : AppRoute
    data class ManutenzioneMoto(val bikeId: String) : AppRoute
    data object NuovaSessione : AppRoute
    data object TutteLeSessioni : AppRoute
}
