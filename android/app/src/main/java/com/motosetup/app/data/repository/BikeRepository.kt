package com.motosetup.app.data.repository

import com.motosetup.app.model.Bike
import kotlinx.coroutines.flow.Flow

interface BikeRepository {
    fun observeBikes(): Flow<List<Bike>>
    fun observeBike(bikeId: String): Flow<Bike?>
    suspend fun addBike(bike: Bike): Result<Unit>
    suspend fun updateBike(bike: Bike): Result<Unit>
    suspend fun deleteBike(bikeId: String): Result<Unit>
}
