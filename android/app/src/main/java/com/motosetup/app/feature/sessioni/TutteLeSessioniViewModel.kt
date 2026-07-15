package com.motosetup.app.feature.sessioni

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.motosetup.app.feature.setup.SessionSummaries
import com.motosetup.app.feature.setup.SessionSummary
import com.motosetup.app.feature.setup.filterSessions
import com.motosetup.app.feature.setup.relativeDayLabel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update

data class TutteLeSessioniUiState(
    val sessions: List<SessionSummary> = emptyList(),
    val bikeOptions: List<String> = emptyList(),
    val trackOptions: List<String> = emptyList(),
    val dayOptions: List<String> = emptyList(),
    val selectedBike: String? = null,
    val selectedTrack: String? = null,
    val selectedDay: String? = null,
) {
    val filtered: List<SessionSummary> get() = filterSessions(sessions, selectedBike, selectedTrack, selectedDay)
}

/** Vedi design_handoff_motosetup_app/README.md #3b. */
@HiltViewModel
class TutteLeSessioniViewModel @Inject constructor(
    sessionSummaries: SessionSummaries,
) : ViewModel() {

    private val selectedBike = MutableStateFlow<String?>(null)
    private val selectedTrack = MutableStateFlow<String?>(null)
    private val selectedDay = MutableStateFlow<String?>(null)

    val uiState: StateFlow<TutteLeSessioniUiState> = combine(
        sessionSummaries.flow,
        selectedBike,
        selectedTrack,
        selectedDay,
    ) { sessions, bike, track, day ->
        TutteLeSessioniUiState(
            sessions = sessions,
            bikeOptions = sessions.map { it.bikeName }.distinct(),
            trackOptions = sessions.map { it.trackName }.distinct(),
            dayOptions = sessions.map { relativeDayLabel(it.date) }.distinct(),
            selectedBike = bike,
            selectedTrack = track,
            selectedDay = day,
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), TutteLeSessioniUiState())

    fun cycleBikeFilter() = cycle(selectedBike) { uiState.value.bikeOptions }
    fun cycleTrackFilter() = cycle(selectedTrack) { uiState.value.trackOptions }
    fun cycleDayFilter() = cycle(selectedDay) { uiState.value.dayOptions }

    /** null ("Tutte") + ogni opzione disponibile, in ciclo. */
    private fun cycle(target: MutableStateFlow<String?>, options: () -> List<String>) {
        val values = listOf(null) + options()
        val currentIndex = values.indexOf(target.value).coerceAtLeast(0)
        target.update { values[(currentIndex + 1) % values.size] }
    }
}
