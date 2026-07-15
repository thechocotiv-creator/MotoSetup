package com.motosetup.app.feature.setup

import com.motosetup.app.model.Meteo
import java.util.concurrent.TimeUnit

data class SessionSummary(
    val sessionId: String,
    val trackName: String,
    val bikeName: String,
    val weather: Meteo,
    val date: Long,
    val runCount: Int,
    val bestLap: String,
    val latestRunId: String,
)

/** Vedi design_handoff_motosetup_app/README.md #3b (etichette "OGGI", "3 GIORNI FA", "1 SETTIMANA FA" in screenshot 12). */
fun relativeDayLabel(dateMillis: Long, now: Long = System.currentTimeMillis()): String {
    val days = TimeUnit.MILLISECONDS.toDays(now - dateMillis).coerceAtLeast(0)
    return when {
        days == 0L -> "OGGI"
        days == 1L -> "IERI"
        days < 7L -> "$days GIORNI FA"
        days < 14L -> "1 SETTIMANA FA"
        days < 30L -> "${days / 7} SETTIMANE FA"
        days < 60L -> "1 MESE FA"
        else -> "${days / 30} MESI FA"
    }
}

fun filterSessions(
    sessions: List<SessionSummary>,
    bikeName: String?,
    trackName: String?,
    dayLabel: String?,
): List<SessionSummary> = sessions.filter { summary ->
    (bikeName == null || summary.bikeName == bikeName) &&
        (trackName == null || summary.trackName == trackName) &&
        (dayLabel == null || relativeDayLabel(summary.date) == dayLabel)
}
