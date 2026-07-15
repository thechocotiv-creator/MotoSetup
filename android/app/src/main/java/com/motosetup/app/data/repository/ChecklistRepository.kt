package com.motosetup.app.data.repository

import com.motosetup.app.model.ChecklistItem
import kotlinx.coroutines.flow.Flow

interface ChecklistRepository {
    fun observeItems(): Flow<List<ChecklistItem>>
    suspend fun addItem(label: String): Result<Unit>
    suspend fun updateItem(item: ChecklistItem): Result<Unit>
    suspend fun deleteItem(itemId: String): Result<Unit>
}
