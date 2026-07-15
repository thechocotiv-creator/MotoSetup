package com.motosetup.app.feature.sessioni

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.motosetup.app.data.entitlement.canAddRun
import com.motosetup.app.data.repository.EntitlementStore
import com.motosetup.app.data.repository.SessionRepository
import com.motosetup.app.data.repository.TrackRepository
import com.motosetup.app.model.Meteo
import com.motosetup.app.model.Run
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class DettaglioRunUiState(
    val trackName: String = "",
    val weather: Meteo = Meteo.Sole,
    val runs: List<Run> = emptyList(),
    val selectedRunId: String = "",
    val canAddRun: Boolean = true,
) {
    val selectedRun: Run? get() = runs.find { it.id == selectedRunId }
    val selectedRunPosition: Int get() = (runs.indexOfFirst { it.id == selectedRunId } + 1).coerceAtLeast(1)
}

/** Vedi design_handoff_motosetup_app/README.md #4. */
@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class DettaglioRunViewModel @Inject constructor(
    private val sessionRepository: SessionRepository,
    private val trackRepository: TrackRepository,
    private val entitlementStore: EntitlementStore,
) : ViewModel() {

    private val sessionId = MutableStateFlow<String?>(null)
    private val selectedRunId = MutableStateFlow("")

    private val sessionFlow = sessionId.filterNotNull().flatMapLatest { sessionRepository.observeSession(it) }
    private val runsFlow = sessionId.filterNotNull().flatMapLatest { sessionRepository.observeRuns(it) }

    val uiState: StateFlow<DettaglioRunUiState> = combine(
        sessionFlow,
        runsFlow,
        trackRepository.observeTracks(),
        entitlementStore.isPremium,
        selectedRunId,
    ) { session, runs, tracks, isPremium, selectedId ->
        val resolvedSelectedId = if (runs.any { it.id == selectedId }) selectedId else runs.firstOrNull()?.id.orEmpty()
        DettaglioRunUiState(
            trackName = tracks.find { it.id == session?.trackId }?.name.orEmpty(),
            weather = session?.weather ?: Meteo.Sole,
            runs = runs,
            selectedRunId = resolvedSelectedId,
            canAddRun = canAddRun(runs.size, isPremium),
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), DettaglioRunUiState())

    fun load(sessionId: String, initialRunId: String) {
        this.sessionId.value = sessionId
        selectedRunId.value = initialRunId
    }

    fun selectPreviousRun() {
        val state = uiState.value
        val index = state.runs.indexOfFirst { it.id == state.selectedRunId }
        if (index > 0) selectedRunId.value = state.runs[index - 1].id
    }

    fun selectNextRun() {
        val state = uiState.value
        val index = state.runs.indexOfFirst { it.id == state.selectedRunId }
        if (index in 0 until state.runs.lastIndex) selectedRunId.value = state.runs[index + 1].id
    }

    fun cycleWeather() {
        val id = sessionId.value ?: return
        viewModelScope.launch { sessionRepository.updateSessionWeather(id, uiState.value.weather.next()) }
    }

    fun addRun() {
        val id = sessionId.value ?: return
        val nextIndex = uiState.value.runs.size + 1
        viewModelScope.launch {
            sessionRepository.addRun(id, Run(index = nextIndex)).onSuccess { newRunId -> selectedRunId.value = newRunId }
        }
    }

    fun updateField(path: String, value: Any) {
        val sid = sessionId.value ?: return
        val runId = uiState.value.selectedRunId
        if (runId.isBlank()) return
        viewModelScope.launch { sessionRepository.updateRunField(sid, runId, path, value) }
    }
}
