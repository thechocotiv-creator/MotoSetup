package com.motosetup.app.model

import com.google.firebase.firestore.DocumentId

data class AppUser(
    @DocumentId val uid: String = "",
    val nickname: String = "",
    val email: String = "",
    val plan: String = "free",
    val aiUsedToday: Int = 0,
    val aiUsageDate: String? = null,
    val avatarURL: String? = null,
)
