package com.motosetup.app.data.repository

import com.motosetup.app.model.Meteo
import com.motosetup.app.model.Run
import com.motosetup.app.model.Session
import kotlinx.coroutines.flow.Flow

interface SessionRepository {
    fun observeSessions(): Flow<List<Session>>
    fun observeSession(sessionId: String): Flow<Session?>
    fun observeRuns(sessionId: String): Flow<List<Run>>
    fun observeRun(sessionId: String, runId: String): Flow<Run?>
    suspend fun createSession(session: Session): Result<String>
    suspend fun addRun(sessionId: String, run: Run): Result<String>
    suspend fun updateSessionWeather(sessionId: String, weather: Meteo): Result<Unit>
    suspend fun updateRunField(sessionId: String, runId: String, path: String, value: Any): Result<Unit>
}
