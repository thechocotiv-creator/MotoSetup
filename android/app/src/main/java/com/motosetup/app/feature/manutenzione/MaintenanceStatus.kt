package com.motosetup.app.feature.manutenzione

import com.motosetup.app.model.MaintenanceItem
import com.motosetup.app.ui.theme.AppStatus

/** Vedi CLAUDE.md alla radice, "Funzionamento Sessioni e Manutenzione". */
fun maintenanceStatus(daysSinceService: Int, intervalDays: Int): AppStatus {
    if (intervalDays <= 0) return AppStatus.Expired
    val ratio = daysSinceService.toDouble() / intervalDays
    return when {
        ratio >= 1.0 -> AppStatus.Expired
        ratio >= 0.8 -> AppStatus.Upcoming
        else -> AppStatus.Ok
    }
}

fun AppStatus.label(): String = when (this) {
    AppStatus.Expired -> "Scaduta"
    AppStatus.Upcoming -> "In scadenza"
    AppStatus.Ok -> "OK"
}

/** Scaduta → In scadenza → OK, indipendente dall'ordinal di AppStatus. */
fun List<MaintenanceItem>.sortedByUrgency(): List<MaintenanceItem> {
    fun priority(item: MaintenanceItem): Int = when (maintenanceStatus(item.daysSinceService, item.intervalDays)) {
        AppStatus.Expired -> 0
        AppStatus.Upcoming -> 1
        AppStatus.Ok -> 2
    }
    return sortedBy(::priority)
}
