package com.motosetup.app.feature.setup

import com.motosetup.app.data.repository.BikeRepository
import com.motosetup.app.data.repository.SessionRepository
import com.motosetup.app.data.repository.TrackRepository
import com.motosetup.app.model.Run
import com.motosetup.app.model.Session
import javax.inject.Inject
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map

/** Combina sessioni + run + nomi moto/pista in [SessionSummary], condiviso da SetupViewModel e TutteLeSessioniViewModel. */
class SessionSummaries @Inject constructor(
    private val sessionRepository: SessionRepository,
    private val bikeRepository: BikeRepository,
    private val trackRepository: TrackRepository,
) {
    @OptIn(ExperimentalCoroutinesApi::class)
    private val sessionsWithRuns: Flow<List<Pair<Session, List<Run>>>> = sessionRepository.observeSessions().flatMapLatest { sessions ->
        if (sessions.isEmpty()) {
            flowOf(emptyList())
        } else {
            combine(sessions.map { session -> sessionRepository.observeRuns(session.id).map { session to it } }) { it.toList() }
        }
    }

    val flow: Flow<List<SessionSummary>> = combine(
        sessionsWithRuns,
        bikeRepository.observeBikes(),
        trackRepository.observeTracks(),
    ) { sessionsWithRuns, bikes, tracks ->
        val bikeNames = bikes.associate { it.id to it.name }
        val trackNames = tracks.associate { it.id to it.name }
        sessionsWithRuns
            .map { (session, runs) -> session.toSummary(runs, bikeNames, trackNames) }
            .sortedByDescending { it.date }
    }
}

private fun Session.toSummary(runs: List<Run>, bikeNames: Map<String, String>, trackNames: Map<String, String>): SessionSummary {
    val bestLap = runs.mapNotNull { it.bestLap.takeIf(String::isNotBlank) }.minOrNull().orEmpty()
    val latestRun = runs.maxByOrNull { it.index }
    return SessionSummary(
        sessionId = id,
        trackName = trackNames[trackId].orEmpty(),
        bikeName = bikeNames[bikeId].orEmpty(),
        weather = weather,
        date = date,
        runCount = runs.size,
        bestLap = bestLap,
        latestRunId = latestRun?.id.orEmpty(),
    )
}
