package com.motosetup.app.feature.profilo

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.motosetup.app.data.repository.AuthRepository
import com.motosetup.app.feature.auth.isValidEmail
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

data class ProfileEditUiState(
    val nickname: String = "",
    val email: String = "",
    val errorMessage: String? = null,
)

/** Usato dalla sheet "Modifica profilo" — vive fuori dal back stack, non condivide ProfiloViewModel. */
@HiltViewModel
class ProfileEditViewModel @Inject constructor(
    private val authRepository: AuthRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow(ProfileEditUiState())
    val uiState: StateFlow<ProfileEditUiState> = _uiState.asStateFlow()

    fun load() {
        viewModelScope.launch {
            authRepository.observeCurrentUser().first()?.let { user ->
                _uiState.value = ProfileEditUiState(nickname = user.nickname, email = user.email)
            }
        }
    }

    fun updateNickname(value: String) {
        _uiState.value = _uiState.value.copy(nickname = value)
    }

    fun updateEmail(value: String) {
        _uiState.value = _uiState.value.copy(email = value)
    }

    fun save(onSaved: () -> Unit) {
        val state = _uiState.value
        if (state.nickname.isBlank() || !isValidEmail(state.email)) {
            _uiState.value = state.copy(errorMessage = "Inserisci un nickname e un'email validi.")
            return
        }
        viewModelScope.launch {
            authRepository.updateProfile(state.nickname.trim(), state.email.trim())
                .onSuccess { onSaved() }
                .onFailure { _uiState.value = _uiState.value.copy(errorMessage = it.message) }
        }
    }
}
