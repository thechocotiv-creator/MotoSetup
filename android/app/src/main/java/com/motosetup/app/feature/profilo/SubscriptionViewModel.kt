package com.motosetup.app.feature.profilo

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.motosetup.app.data.repository.EntitlementStore
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

/** Usato dalla sheet "Abbonamento" — vedi design_handoff_motosetup_app/README.md #6c. */
@HiltViewModel
class SubscriptionViewModel @Inject constructor(
    private val entitlementStore: EntitlementStore,
) : ViewModel() {

    val isPremium: StateFlow<Boolean> = entitlementStore.isPremium
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), false)

    /** Acquisto fake/debug finché Play Billing non è integrato — vedi android/CLAUDE.md rischio #4. */
    fun purchasePremium() {
        viewModelScope.launch { entitlementStore.purchasePremium() }
    }
}
