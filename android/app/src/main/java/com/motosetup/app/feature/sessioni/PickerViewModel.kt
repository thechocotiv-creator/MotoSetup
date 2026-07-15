package com.motosetup.app.feature.sessioni

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.motosetup.app.data.repository.SessionRepository
import com.motosetup.app.model.Run
import com.motosetup.app.navigation.PickerKind
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

/** Usato solo da AppBottomSheetHost per la sheet Picker — disaccoppiato da DettaglioRunViewModel, vedi piano Fase 4. */
@HiltViewModel
class PickerViewModel @Inject constructor(
    private val sessionRepository: SessionRepository,
) : ViewModel() {

    private val _run = MutableStateFlow<Run?>(null)
    val run: StateFlow<Run?> = _run.asStateFlow()

    fun load(sessionId: String, runId: String) {
        _run.value = null
        viewModelScope.launch {
            _run.value = sessionRepository.observeRun(sessionId, runId).first()
        }
    }

    fun confirm(sessionId: String, runId: String, kind: PickerKind, indices: List<Int>) {
        val (path, value) = pickerFieldUpdate(kind, indices)
        viewModelScope.launch { sessionRepository.updateRunField(sessionId, runId, path, value) }
    }
}
