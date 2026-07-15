package com.motosetup.app.model

import com.google.firebase.firestore.DocumentId

data class MaintenanceItem(
    @DocumentId val id: String = "",
    val name: String = "",
    val daysSinceService: Int = 0,
    val intervalDays: Int = 30,
)
