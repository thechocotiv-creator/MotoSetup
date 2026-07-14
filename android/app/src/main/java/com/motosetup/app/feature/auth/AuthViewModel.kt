package com.motosetup.app.feature.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.motosetup.app.data.repository.AuthRepository
import com.motosetup.app.data.repository.AuthState
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val authRepository: AuthRepository,
) : ViewModel() {

    val authState: StateFlow<AuthState> = authRepository.authState
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), AuthState.Loading)

    private val _postAuthOnboardingActive = MutableStateFlow(false)
    val postAuthOnboardingActive: StateFlow<Boolean> = _postAuthOnboardingActive.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _uiMessage = MutableStateFlow<String?>(null)
    val uiMessage: StateFlow<String?> = _uiMessage.asStateFlow()

    fun login(email: String, password: String) {
        val errors = validateLoginForm(email, password)
        if (errors.isNotEmpty()) {
            _uiMessage.value = errors.first()
            return
        }
        viewModelScope.launch {
            _isLoading.value = true
            authRepository.signInWithEmail(email, password)
                .onFailure { _uiMessage.value = it.message }
            _isLoading.value = false
        }
    }

    fun register(nickname: String, email: String, password: String, confirmPassword: String) {
        val errors = validateRegisterForm(nickname, email, password, confirmPassword)
        if (errors.isNotEmpty()) {
            _uiMessage.value = errors.first()
            return
        }
        viewModelScope.launch {
            _isLoading.value = true
            authRepository.registerWithEmail(nickname, email, password)
                .onSuccess { _postAuthOnboardingActive.value = true }
                .onFailure { _uiMessage.value = it.message }
            _isLoading.value = false
        }
    }

    fun signInWithGoogle(idToken: String) {
        viewModelScope.launch {
            _isLoading.value = true
            authRepository.signInWithGoogle(idToken)
                .onSuccess { isNewAccount -> _postAuthOnboardingActive.value = isNewAccount }
                .onFailure { _uiMessage.value = it.message }
            _isLoading.value = false
        }
    }

    fun finishOnboarding() {
        _postAuthOnboardingActive.value = false
    }

    fun consumeUiMessage() {
        _uiMessage.value = null
    }
}
