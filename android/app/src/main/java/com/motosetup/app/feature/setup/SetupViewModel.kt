package com.motosetup.app.feature.setup

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

data class SetupUiState(
    val today: SessionSummary? = null,
    val previous: List<SessionSummary> = emptyList(),
)

@HiltViewModel
class SetupViewModel @Inject constructor(
    sessionSummaries: SessionSummaries,
) : ViewModel() {

    val uiState: StateFlow<SetupUiState> = sessionSummaries.flow.map { summaries ->
        val today = summaries.firstOrNull { relativeDayLabel(it.date) == "OGGI" }
        SetupUiState(today = today, previous = summaries.filterNot { it.sessionId == today?.sessionId })
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), SetupUiState())
}
