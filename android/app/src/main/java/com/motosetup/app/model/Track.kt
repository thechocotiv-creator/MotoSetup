package com.motosetup.app.model

import com.google.firebase.firestore.DocumentId

data class Track(
    @DocumentId val id: String = "",
    val name: String = "",
)
