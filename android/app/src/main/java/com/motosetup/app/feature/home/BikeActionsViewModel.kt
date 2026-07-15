package com.motosetup.app.feature.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.motosetup.app.data.repository.BikeRepository
import com.motosetup.app.model.Bike
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

/** Usato dal dialog "Elimina moto" in AppAlertDialogHost — vive fuori dal back stack, quindi non condivide HomeViewModel. */
@HiltViewModel
class BikeActionsViewModel @Inject constructor(
    private val bikeRepository: BikeRepository,
) : ViewModel() {
    fun observeBike(bikeId: String): Flow<Bike?> = bikeRepository.observeBike(bikeId)

    fun deleteBike(bikeId: String) {
        viewModelScope.launch { bikeRepository.deleteBike(bikeId) }
    }
}
