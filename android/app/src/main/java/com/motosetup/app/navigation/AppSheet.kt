package com.motosetup.app.navigation

/** Campo del [com.motosetup.app.model.Run] modificabile via wheel picker — vedi #4a. */
enum class PickerKind { Ora, Temperatura, BestLap, Giri }

/**
 * Contenuti mostrati in AppBottomSheetHost. Non fanno parte del back stack
 * (sono overlay, non NavEntry) — vedi CLAUDE.md Android, "Navigazione".
 */
sealed interface AppSheet {
    data class ModificaMoto(val bikeId: String) : AppSheet
    data object ModificaProfilo : AppSheet
    data object ModificaPassword : AppSheet
    data object Abbonamento : AppSheet
    data class Picker(val kind: PickerKind, val sessionId: String, val runId: String) : AppSheet
}
