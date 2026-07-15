package com.motosetup.app.data.repository

import com.motosetup.app.model.MaintenanceItem
import kotlinx.coroutines.flow.Flow

interface MaintenanceRepository {
    fun observeItems(bikeId: String): Flow<List<MaintenanceItem>>
    suspend fun addItem(bikeId: String, item: MaintenanceItem): Result<Unit>
    suspend fun updateItem(bikeId: String, item: MaintenanceItem): Result<Unit>
}
