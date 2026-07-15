package com.motosetup.app.feature.profilo

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.motosetup.app.data.repository.AuthRepository
import com.motosetup.app.feature.auth.validatePasswordChangeForm
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class PasswordChangeUiState(
    val current: String = "",
    val next: String = "",
    val confirm: String = "",
    val errorMessage: String? = null,
    val isSaving: Boolean = false,
)

/** Usato dalla sheet "Modifica password" — vive fuori dal back stack. */
@HiltViewModel
class PasswordChangeViewModel @Inject constructor(
    private val authRepository: AuthRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow(PasswordChangeUiState())
    val uiState: StateFlow<PasswordChangeUiState> = _uiState.asStateFlow()

    fun updateCurrent(value: String) {
        _uiState.value = _uiState.value.copy(current = value)
    }

    fun updateNext(value: String) {
        _uiState.value = _uiState.value.copy(next = value)
    }

    fun updateConfirm(value: String) {
        _uiState.value = _uiState.value.copy(confirm = value)
    }

    fun save(onSaved: () -> Unit) {
        val state = _uiState.value
        val errors = validatePasswordChangeForm(state.current, state.next, state.confirm)
        if (errors.isNotEmpty()) {
            _uiState.value = state.copy(errorMessage = errors.first())
            return
        }
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isSaving = true, errorMessage = null)
            authRepository.changePassword(state.current, state.next)
                .onSuccess { onSaved() }
                .onFailure { _uiState.value = _uiState.value.copy(errorMessage = it.message) }
            _uiState.value = _uiState.value.copy(isSaving = false)
        }
    }
}
