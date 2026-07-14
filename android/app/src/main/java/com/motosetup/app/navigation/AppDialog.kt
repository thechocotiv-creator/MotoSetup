package com.motosetup.app.navigation

/** Contenuti mostrati in AppAlertDialogHost — conferme distruttive. */
sealed interface AppDialog {
    data class EliminaMoto(val bikeId: String) : AppDialog
    data object EliminaAccount : AppDialog
}
