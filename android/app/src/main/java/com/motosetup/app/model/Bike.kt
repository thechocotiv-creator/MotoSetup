package com.motosetup.app.model

import com.google.firebase.firestore.DocumentId
import com.motosetup.app.ui.theme.BikeCardColor

enum class BikeRarity { Comune, Rara, Leggendaria }

data class Bike(
    @DocumentId val id: String = "",
    val name: String = "",
    val subtitle: String = "",
    val cardColor: BikeCardColor = BikeCardColor.Blu,
    val rarity: BikeRarity = BikeRarity.Comune,
    val photoURL: String? = null,
)
