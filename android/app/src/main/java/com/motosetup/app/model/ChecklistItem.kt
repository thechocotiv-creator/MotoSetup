package com.motosetup.app.model

import com.google.firebase.firestore.DocumentId

data class ChecklistItem(
    @DocumentId val id: String = "",
    val label: String = "",
    // "done", non "isDone": i getter Kotlin isXxx() per Boolean confondono il mapper POJO di
    // Firestore in lettura (constatato in verifica manuale — vedi FirebaseChecklistRepository).
    val done: Boolean = false,
    val createdAt: Long = System.currentTimeMillis(),
)
