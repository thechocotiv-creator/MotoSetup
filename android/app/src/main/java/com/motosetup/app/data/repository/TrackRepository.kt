package com.motosetup.app.data.repository

import com.motosetup.app.model.Track
import kotlinx.coroutines.flow.Flow

interface TrackRepository {
    fun observeTracks(): Flow<List<Track>>
    suspend fun addCustomTrack(name: String): Result<Unit>
}
