package com.motosetup.app.feature.consigliai

import java.util.concurrent.TimeUnit

/** Vedi design_handoff_motosetup_app/README.md #5 (sezione "Cronologia"). */
fun relativeAdviceTimeLabel(dateMillis: Long, now: Long = System.currentTimeMillis()): String {
    val days = TimeUnit.MILLISECONDS.toDays(now - dateMillis).coerceAtLeast(0)
    return when {
        days == 0L -> "oggi"
        days == 1L -> "ieri"
        days < 7L -> "$days giorni fa"
        days < 14L -> "1 settimana fa"
        days < 30L -> "${days / 7} settimane fa"
        days < 60L -> "1 mese fa"
        else -> "${days / 30} mesi fa"
    }
}
