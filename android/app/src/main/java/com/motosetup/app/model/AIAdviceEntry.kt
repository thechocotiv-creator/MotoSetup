package com.motosetup.app.model

import com.google.firebase.firestore.DocumentId

data class AIAdviceEntry(
    @DocumentId val id: String = "",
    val question: String = "",
    val parameterName: String = "",
    val parameterValue: String = "",
    val explanation: String = "",
    val createdAt: Long = System.currentTimeMillis(),
)
