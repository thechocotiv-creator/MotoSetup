package com.motosetup.app.feature.manutenzione

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.motosetup.app.data.repository.BikeRepository
import com.motosetup.app.data.repository.MaintenanceRepository
import com.motosetup.app.model.MaintenanceItem
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class ManutenzioneUiState(
    val bikeName: String = "",
    val items: List<MaintenanceItem> = emptyList(),
)

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class ManutenzioneViewModel @Inject constructor(
    private val bikeRepository: BikeRepository,
    private val maintenanceRepository: MaintenanceRepository,
) : ViewModel() {

    private val bikeId = MutableStateFlow<String?>(null)

    val uiState: StateFlow<ManutenzioneUiState> = bikeId.filterNotNull().flatMapLatest { id ->
        combine(bikeRepository.observeBike(id), maintenanceRepository.observeItems(id)) { bike, items ->
            ManutenzioneUiState(bikeName = bike?.name.orEmpty(), items = items.sortedByUrgency())
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), ManutenzioneUiState())

    fun load(bikeId: String) {
        this.bikeId.value = bikeId
    }

    fun addItem(name: String, intervalDays: Int) {
        val id = bikeId.value ?: return
        if (name.isBlank() || intervalDays <= 0) return
        viewModelScope.launch {
            maintenanceRepository.addItem(id, MaintenanceItem(name = name.trim(), daysSinceService = 0, intervalDays = intervalDays))
        }
    }

    fun markDoneToday(item: MaintenanceItem) = updateDaysSinceService(item, days = 0)

    fun markDonePast(item: MaintenanceItem, daysAgo: Int) = updateDaysSinceService(item, days = daysAgo)

    fun updateInterval(item: MaintenanceItem, intervalDays: Int) {
        val id = bikeId.value ?: return
        if (intervalDays <= 0) return
        viewModelScope.launch { maintenanceRepository.updateItem(id, item.copy(intervalDays = intervalDays)) }
    }

    private fun updateDaysSinceService(item: MaintenanceItem, days: Int) {
        val id = bikeId.value ?: return
        if (days < 0) return
        viewModelScope.launch { maintenanceRepository.updateItem(id, item.copy(daysSinceService = days)) }
    }
}
