package com.motosetup.app.navigation

/**
 * Contenuti mostrati in AppBottomSheetHost. Non fanno parte del back stack
 * (sono overlay, non NavEntry) — vedi CLAUDE.md Android, "Navigazione".
 * Wheel/number picker resta fuori: costruito in Fase 4 (rischio #5).
 */
sealed interface AppSheet {
    data class ModificaMoto(val bikeId: String) : AppSheet
    data object ModificaProfilo : AppSheet
    data object ModificaPassword : AppSheet
    data object Abbonamento : AppSheet
}
