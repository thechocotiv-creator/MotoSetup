package com.motosetup.app.feature.consigliai

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.motosetup.app.data.entitlement.canAskAiAdvice
import com.motosetup.app.data.repository.AIAdviceRepository
import com.motosetup.app.data.repository.EntitlementStore
import com.motosetup.app.model.AIAdviceEntry
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class ConsigliAIUiState(
    val question: String = "",
    val entries: List<AIAdviceEntry> = emptyList(),
    val isPremium: Boolean = false,
    val aiAdviceUsedToday: Int = 0,
    val isSubmitting: Boolean = false,
) {
    val canAskAdvice: Boolean get() = canAskAiAdvice(aiAdviceUsedToday, isPremium)
    val latestEntry: AIAdviceEntry? get() = entries.firstOrNull()
    val usageLabel: String get() = if (isPremium) "Illimitati · Premium" else "$aiAdviceUsedToday/1 oggi · Free"
}

/** Vedi design_handoff_motosetup_app/README.md #5. */
@HiltViewModel
class ConsigliAIViewModel @Inject constructor(
    private val aiAdviceRepository: AIAdviceRepository,
    private val entitlementStore: EntitlementStore,
) : ViewModel() {

    private val question = MutableStateFlow("")
    private val isSubmitting = MutableStateFlow(false)

    val uiState: StateFlow<ConsigliAIUiState> = combine(
        question,
        aiAdviceRepository.observeEntries(),
        entitlementStore.isPremium,
        entitlementStore.aiAdviceUsedToday,
        isSubmitting,
    ) { currentQuestion, entries, isPremium, usedToday, submitting ->
        ConsigliAIUiState(
            question = currentQuestion,
            entries = entries,
            isPremium = isPremium,
            aiAdviceUsedToday = usedToday,
            isSubmitting = submitting,
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), ConsigliAIUiState())

    fun updateQuestion(value: String) {
        question.value = value
    }

    fun askAdvice(onLimitReached: () -> Unit) {
        val state = uiState.value
        if (state.question.isBlank() || state.isSubmitting) return
        if (!state.canAskAdvice) {
            onLimitReached()
            return
        }
        isSubmitting.value = true
        viewModelScope.launch {
            aiAdviceRepository.askAdvice(state.question.trim())
            question.value = ""
            isSubmitting.value = false
        }
    }
}
