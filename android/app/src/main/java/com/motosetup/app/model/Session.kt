package com.motosetup.app.model

import com.google.firebase.firestore.DocumentId

data class Session(
    @DocumentId val id: String = "",
    val trackId: String = "",
    val bikeId: String = "",
    val weather: Meteo = Meteo.Sole,
    val date: Long = System.currentTimeMillis(),
)
