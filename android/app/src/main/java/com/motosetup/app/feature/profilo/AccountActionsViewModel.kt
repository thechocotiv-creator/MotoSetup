package com.motosetup.app.feature.profilo

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.motosetup.app.data.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.launch

/** Usato dal dialog "Elimina account" in AppAlertDialogHost — vive fuori dal back stack. */
@HiltViewModel
class AccountActionsViewModel @Inject constructor(
    private val authRepository: AuthRepository,
) : ViewModel() {
    fun deleteAccount() {
        viewModelScope.launch { authRepository.deleteAccount() }
    }
}
