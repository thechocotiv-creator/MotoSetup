package com.motosetup.app.feature.checklist

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.motosetup.app.data.repository.ChecklistRepository
import com.motosetup.app.model.ChecklistItem
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

@HiltViewModel
class ChecklistViewModel @Inject constructor(
    private val checklistRepository: ChecklistRepository,
) : ViewModel() {

    val items: StateFlow<List<ChecklistItem>> = checklistRepository.observeItems()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    fun addItem(label: String) {
        if (label.isBlank()) return
        viewModelScope.launch { checklistRepository.addItem(label.trim()) }
    }

    fun toggle(item: ChecklistItem) {
        viewModelScope.launch { checklistRepository.updateItem(item.copy(done = !item.done)) }
    }

    fun delete(item: ChecklistItem) {
        viewModelScope.launch { checklistRepository.deleteItem(item.id) }
    }
}
