package com.motosetup.app.feature.profilo

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.motosetup.app.data.repository.AuthRepository
import com.motosetup.app.data.repository.EntitlementStore
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn

data class ProfiloUiState(
    val nickname: String = "",
    val email: String = "",
    val isPremium: Boolean = false,
)

/** Vedi design_handoff_motosetup_app/README.md #6. */
@HiltViewModel
class ProfiloViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    entitlementStore: EntitlementStore,
) : ViewModel() {

    val uiState: StateFlow<ProfiloUiState> = combine(
        authRepository.observeCurrentUser(),
        entitlementStore.isPremium,
    ) { user, isPremium ->
        ProfiloUiState(
            nickname = user?.nickname.orEmpty(),
            email = user?.email.orEmpty(),
            isPremium = isPremium,
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), ProfiloUiState())

    fun logout() = authRepository.signOut()
}
