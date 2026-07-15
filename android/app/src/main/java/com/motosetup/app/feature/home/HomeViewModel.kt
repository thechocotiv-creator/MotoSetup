package com.motosetup.app.feature.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.motosetup.app.data.entitlement.canAddBike
import com.motosetup.app.data.repository.BikeRepository
import com.motosetup.app.data.repository.ChecklistRepository
import com.motosetup.app.data.repository.EntitlementStore
import com.motosetup.app.data.repository.MaintenanceRepository
import com.motosetup.app.model.Bike
import com.motosetup.app.model.MaintenanceItem
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class HomeUiState(
    val bikes: List<Bike> = emptyList(),
    val maintenanceByBike: Map<String, List<MaintenanceItem>> = emptyMap(),
    val checklistDone: Int = 0,
    val checklistTotal: Int = 0,
    val canAddBike: Boolean = true,
    val isPremium: Boolean = false,
)

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class HomeViewModel @Inject constructor(
    private val bikeRepository: BikeRepository,
    private val maintenanceRepository: MaintenanceRepository,
    private val checklistRepository: ChecklistRepository,
    private val entitlementStore: EntitlementStore,
) : ViewModel() {

    private val bikesFlow = bikeRepository.observeBikes()

    private val maintenanceByBikeFlow = bikesFlow.flatMapLatest { bikes ->
        if (bikes.isEmpty()) {
            flowOf(emptyMap())
        } else {
            combine(bikes.map { bike -> maintenanceRepository.observeItems(bike.id).map { bike.id to it } }) { pairs ->
                pairs.toMap()
            }
        }
    }

    val uiState: StateFlow<HomeUiState> = combine(
        bikesFlow,
        maintenanceByBikeFlow,
        checklistRepository.observeItems(),
        entitlementStore.isPremium,
    ) { bikes, maintenanceByBike, checklist, isPremium ->
        HomeUiState(
            bikes = bikes,
            maintenanceByBike = maintenanceByBike,
            checklistDone = checklist.count { it.done },
            checklistTotal = checklist.size,
            canAddBike = canAddBike(bikes.size, isPremium),
            isPremium = isPremium,
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), HomeUiState())

    fun addBike(name: String) {
        if (name.isBlank()) return
        viewModelScope.launch { bikeRepository.addBike(Bike(name = name.trim())) }
    }
}
