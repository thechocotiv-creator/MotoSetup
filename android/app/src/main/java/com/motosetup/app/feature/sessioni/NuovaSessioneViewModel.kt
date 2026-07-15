package com.motosetup.app.feature.sessioni

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.motosetup.app.data.entitlement.canAddBike
import com.motosetup.app.data.repository.BikeRepository
import com.motosetup.app.data.repository.EntitlementStore
import com.motosetup.app.data.repository.SessionRepository
import com.motosetup.app.data.repository.TrackRepository
import com.motosetup.app.model.Bike
import com.motosetup.app.model.Meteo
import com.motosetup.app.model.Session
import com.motosetup.app.model.Track
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class NuovaSessioneUiState(
    val bikes: List<Bike> = emptyList(),
    val tracks: List<Track> = emptyList(),
    val selectedBikeIndex: Int = 0,
    val selectedTrackIndex: Int = 0,
    val weather: Meteo = Meteo.Sole,
    val canAddBike: Boolean = true,
) {
    val isAddBikeSelected: Boolean get() = selectedBikeIndex >= bikes.size
    val isAddTrackSelected: Boolean get() = selectedTrackIndex >= tracks.size
    val selectedBikeLabel: String get() = if (isAddBikeSelected) "+ Aggiungi nuova moto" else bikes[selectedBikeIndex].name
    val selectedTrackLabel: String get() = if (isAddTrackSelected) "+ Aggiungi nuova pista" else tracks[selectedTrackIndex].name
    val canStartSession: Boolean get() = !isAddBikeSelected && !isAddTrackSelected && bikes.isNotEmpty() && tracks.isNotEmpty()
}

/** Vedi design_handoff_motosetup_app/README.md #3a. */
@HiltViewModel
class NuovaSessioneViewModel @Inject constructor(
    private val bikeRepository: BikeRepository,
    private val trackRepository: TrackRepository,
    private val sessionRepository: SessionRepository,
    private val entitlementStore: EntitlementStore,
) : ViewModel() {

    private val selectedBikeIndex = MutableStateFlow(0)
    private val selectedTrackIndex = MutableStateFlow(0)
    private val weather = MutableStateFlow(Meteo.Sole)

    private val catalog = combine(bikeRepository.observeBikes(), trackRepository.observeTracks(), entitlementStore.isPremium) { bikes, tracks, isPremium ->
        Triple(bikes, tracks, isPremium)
    }

    val uiState: StateFlow<NuovaSessioneUiState> = combine(
        catalog,
        selectedBikeIndex,
        selectedTrackIndex,
        weather,
    ) { (bikes, tracks, isPremium), bikeIndex, trackIndex, currentWeather ->
        NuovaSessioneUiState(
            bikes = bikes,
            tracks = tracks,
            selectedBikeIndex = bikeIndex.coerceIn(0, bikes.size),
            selectedTrackIndex = trackIndex.coerceIn(0, tracks.size),
            weather = currentWeather,
            canAddBike = canAddBike(bikes.size, isPremium),
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), NuovaSessioneUiState())

    fun selectNextBike() {
        val total = uiState.value.bikes.size + 1
        selectedBikeIndex.update { (it + 1) % total }
    }

    fun selectPreviousBike() {
        val total = uiState.value.bikes.size + 1
        selectedBikeIndex.update { (it - 1 + total) % total }
    }

    fun selectNextTrack() {
        val total = uiState.value.tracks.size + 1
        selectedTrackIndex.update { (it + 1) % total }
    }

    fun selectPreviousTrack() {
        val total = uiState.value.tracks.size + 1
        selectedTrackIndex.update { (it - 1 + total) % total }
    }

    fun cycleWeather() {
        weather.update { it.next() }
    }

    fun addBike(name: String) {
        if (name.isBlank()) return
        viewModelScope.launch { bikeRepository.addBike(Bike(name = name.trim())) }
    }

    fun addTrack(name: String) {
        if (name.isBlank()) return
        viewModelScope.launch { trackRepository.addCustomTrack(name.trim()) }
    }

    fun createSession(onCreated: (sessionId: String, runId: String) -> Unit) {
        val state = uiState.value
        if (!state.canStartSession) return
        val bike = state.bikes[state.selectedBikeIndex]
        val track = state.tracks[state.selectedTrackIndex]
        viewModelScope.launch {
            sessionRepository.createSession(Session(trackId = track.id, bikeId = bike.id, weather = state.weather))
                .onSuccess { sessionId ->
                    val runId = sessionRepository.observeRuns(sessionId).first().firstOrNull()?.id.orEmpty()
                    onCreated(sessionId, runId)
                }
        }
    }
}
