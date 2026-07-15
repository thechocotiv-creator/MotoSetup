package com.motosetup.app.feature.sessioni

import com.motosetup.app.model.Run
import com.motosetup.app.navigation.PickerKind

/** Colonne/formattazione del wheel picker per campo — logica pura, vedi #4a. */
private object PickerColumns {
    val hours = (0..23).map { it.toString().padStart(2, '0') }
    val minutes = (0..59).map { it.toString().padStart(2, '0') }
    val seconds = (0..59).map { it.toString().padStart(2, '0') }
    val millis = (0..999).map { it.toString().padStart(3, '0') }
    val temperatures = (-10..50).map { it.toString() }
    val lapMinutes = (0..9).map { it.toString() }
    val counts = (0..99).map { it.toString() }
}

private val bestLapRegex = Regex("""(\d+):(\d{2})\.(\d{3})""")

fun pickerTitle(kind: PickerKind): String = when (kind) {
    PickerKind.Ora -> "Ora"
    PickerKind.Temperatura -> "Temperatura"
    PickerKind.BestLap -> "Best lap"
    PickerKind.Giri -> "Giri"
}

fun pickerColumns(kind: PickerKind): List<List<String>> = when (kind) {
    PickerKind.Ora -> listOf(PickerColumns.hours, PickerColumns.minutes)
    PickerKind.Temperatura -> listOf(PickerColumns.temperatures)
    PickerKind.BestLap -> listOf(PickerColumns.lapMinutes, PickerColumns.seconds, PickerColumns.millis)
    PickerKind.Giri -> listOf(PickerColumns.counts)
}

fun pickerSeparators(kind: PickerKind): List<String?> = when (kind) {
    PickerKind.Ora -> listOf(":", null)
    PickerKind.Temperatura -> listOf(null)
    PickerKind.BestLap -> listOf(":", ".", null)
    PickerKind.Giri -> listOf(null)
}

fun pickerInitialIndices(kind: PickerKind, run: Run): List<Int> = when (kind) {
    PickerKind.Ora -> {
        val parts = run.time.split(":")
        listOf(
            PickerColumns.hours.indexOf(parts.getOrNull(0)).coerceAtLeast(0),
            PickerColumns.minutes.indexOf(parts.getOrNull(1)).coerceAtLeast(0),
        )
    }
    PickerKind.Temperatura -> listOf(PickerColumns.temperatures.indexOf(run.temperature.toString()).coerceAtLeast(0))
    PickerKind.BestLap -> {
        val match = bestLapRegex.find(run.bestLap)
        if (match != null) {
            val (m, s, ms) = match.destructured
            listOf(PickerColumns.lapMinutes.indexOf(m).coerceAtLeast(0), s.toInt(), ms.toInt())
        } else {
            listOf(0, 0, 0)
        }
    }
    PickerKind.Giri -> listOf(run.laps.coerceIn(0, 99))
}

/** Path Firestore (dot-path su [Run]) + valore da scrivere per gli indici selezionati nel wheel picker. */
fun pickerFieldUpdate(kind: PickerKind, indices: List<Int>): Pair<String, Any> = when (kind) {
    PickerKind.Ora -> "time" to "${PickerColumns.hours[indices[0]]}:${PickerColumns.minutes[indices[1]]}"
    PickerKind.Temperatura -> "temperature" to PickerColumns.temperatures[indices[0]].toInt()
    PickerKind.BestLap ->
        "bestLap" to "${PickerColumns.lapMinutes[indices[0]]}:${PickerColumns.seconds[indices[1]]}.${PickerColumns.millis[indices[2]]}"
    PickerKind.Giri -> "laps" to PickerColumns.counts[indices[0]].toInt()
}
