package com.motosetup.app.data.repository

import com.motosetup.app.model.AIAdviceEntry
import kotlinx.coroutines.flow.Flow

interface AIAdviceRepository {
    fun observeEntries(): Flow<List<AIAdviceEntry>>
    suspend fun askAdvice(question: String): Result<AIAdviceEntry>
}
