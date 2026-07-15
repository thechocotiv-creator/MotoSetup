package com.motosetup.app.data.repository

import kotlinx.coroutines.flow.Flow

/** Gating centralizzato del piano — vedi CLAUDE.md alla radice, "Piano Premium — regole". Non duplicare i controlli nei ViewModel. */
interface EntitlementStore {
    val isPremium: Flow<Boolean>

    /** Già "azzerato" se l'ultimo utilizzo registrato non è di oggi. */
    val aiAdviceUsedToday: Flow<Int>

    suspend fun recordAiAdviceUsage(): Result<Unit>
}
