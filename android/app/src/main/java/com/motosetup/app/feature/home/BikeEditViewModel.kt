package com.motosetup.app.feature.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.motosetup.app.data.repository.BikeRepository
import com.motosetup.app.model.Bike
import com.motosetup.app.model.BikeRarity
import com.motosetup.app.ui.theme.BikeCardColor
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

data class BikeEditUiState(
    val name: String = "",
    val subtitle: String = "",
    val cardColor: BikeCardColor = BikeCardColor.Blu,
    val rarity: BikeRarity = BikeRarity.Comune,
    val isLoaded: Boolean = false,
)

@HiltViewModel
class BikeEditViewModel @Inject constructor(
    private val bikeRepository: BikeRepository,
) : ViewModel() {

    private var bikeId: String? = null
    private val _uiState = MutableStateFlow(BikeEditUiState())
    val uiState: StateFlow<BikeEditUiState> = _uiState.asStateFlow()

    /**
     * Ricarica sempre da Firestore, senza guardia "già caricato": il ViewModel è scoped
     * all'Activity (hiltViewModel() richiamato da AppBottomSheetHost, fuori da un
     * NavBackStackEntry), quindi l'istanza sopravvive alla chiusura della sheet — una guardia
     * per bikeId mostrerebbe bozze scartate (mai salvate) invece dei dati reali alla riapertura.
     */
    fun load(bikeId: String) {
        this.bikeId = bikeId
        _uiState.value = BikeEditUiState()
        viewModelScope.launch {
            bikeRepository.observeBike(bikeId).first()?.let { bike ->
                _uiState.value = BikeEditUiState(
                    name = bike.name,
                    subtitle = bike.subtitle,
                    cardColor = bike.cardColor,
                    rarity = bike.rarity,
                    isLoaded = true,
                )
            }
        }
    }

    fun updateName(name: String) {
        _uiState.value = _uiState.value.copy(name = name)
    }

    fun updateSubtitle(subtitle: String) {
        _uiState.value = _uiState.value.copy(subtitle = subtitle)
    }

    fun updateCardColor(color: BikeCardColor) {
        _uiState.value = _uiState.value.copy(cardColor = color)
    }

    fun updateRarity(rarity: BikeRarity) {
        _uiState.value = _uiState.value.copy(rarity = rarity)
    }

    fun save(onSaved: () -> Unit) {
        val id = bikeId ?: return
        val state = _uiState.value
        if (state.name.isBlank()) return
        viewModelScope.launch {
            bikeRepository.updateBike(
                Bike(
                    id = id,
                    name = state.name.trim(),
                    subtitle = state.subtitle.trim(),
                    cardColor = state.cardColor,
                    rarity = state.rarity,
                ),
            ).onSuccess { onSaved() }
        }
    }
}
