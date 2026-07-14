package com.motosetup.app.ui.theme

import androidx.compose.ui.graphics.Color

/** Palette fissa (dark-mode only) — vedi CLAUDE.md alla radice, sezione "Design tokens". */
object AppColor {
    val background = Color(0xFF1C1C1C)
    val panel = Color(0xFF1F1F1F)
    val textPrimary = Color(0xFFF7F7F7)
    val textSecondary = Color(0xFFA3A3A3)

    val accentBlue = Color(0xFF7AB8FF)
    val red = Color(0xFFE0432F)
    val gold = Color(0xFFD9A441)
    val green = Color(0xFF52CF83)
    val purple = Color(0xFFD17EE8)

    fun status(status: AppStatus): Color = when (status) {
        AppStatus.Ok -> textSecondary
        AppStatus.Upcoming -> gold
        AppStatus.Expired -> red
    }
}

/** Scala a 3 stati riusata sia per la manutenzione sia per il best-lap relativo di una sessione. */
enum class AppStatus { Ok, Upcoming, Expired }

/** I 5 colori selezionabili per le card moto nel garage. */
enum class BikeCardColor(val swatch: Color) {
    Blu(AppColor.accentBlue),
    Rosso(AppColor.red),
    Oro(AppColor.gold),
    Verde(AppColor.green),
    Viola(AppColor.purple),
}
