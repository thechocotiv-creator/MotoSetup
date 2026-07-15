package com.motosetup.app.data.repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.motosetup.app.model.Meteo
import com.motosetup.app.model.Run
import com.motosetup.app.model.Session
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

@Singleton
class FirebaseSessionRepository @Inject constructor(
    private val auth: FirebaseAuth,
    private val firestore: FirebaseFirestore,
) : SessionRepository {

    private fun sessionsCollection() =
        firestore.collection("users").document(auth.currentUser?.uid.orEmpty()).collection("sessions")

    private fun runsCollection(sessionId: String) = sessionsCollection().document(sessionId).collection("runs")

    override fun observeSessions(): Flow<List<Session>> = callbackFlow {
        val listener = sessionsCollection()
            .orderBy("date", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null) return@addSnapshotListener
                trySend(snapshot?.toObjects(Session::class.java).orEmpty())
            }
        awaitClose { listener.remove() }
    }

    override fun observeSession(sessionId: String): Flow<Session?> = callbackFlow {
        val listener = sessionsCollection().document(sessionId).addSnapshotListener { snapshot, error ->
            if (error != null) return@addSnapshotListener
            trySend(snapshot?.toObject(Session::class.java))
        }
        awaitClose { listener.remove() }
    }

    override fun observeRuns(sessionId: String): Flow<List<Run>> = callbackFlow {
        val listener = runsCollection(sessionId)
            .orderBy("index", Query.Direction.ASCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null) return@addSnapshotListener
                trySend(snapshot?.toObjects(Run::class.java).orEmpty())
            }
        awaitClose { listener.remove() }
    }

    override fun observeRun(sessionId: String, runId: String): Flow<Run?> = callbackFlow {
        val listener = runsCollection(sessionId).document(runId).addSnapshotListener { snapshot, error ->
            if (error != null) return@addSnapshotListener
            trySend(snapshot?.toObject(Run::class.java))
        }
        awaitClose { listener.remove() }
    }

    /** Scrive sessione + run 1 in un batch: le due letture successive (observeSessions/observeRuns) devono vedere sempre uno stato coerente, mai una sessione senza run. */
    override suspend fun createSession(session: Session): Result<String> = runCatching {
        val sessionDoc = sessionsCollection().document()
        val runDoc = sessionDoc.collection("runs").document()
        firestore.batch()
            .set(sessionDoc, session)
            .set(runDoc, Run(index = 1))
            .commit()
            .await()
        sessionDoc.id
    }

    override suspend fun addRun(sessionId: String, run: Run): Result<String> = runCatching {
        runsCollection(sessionId).add(run).await().id
    }

    override suspend fun updateSessionWeather(sessionId: String, weather: Meteo): Result<Unit> = runCatching {
        sessionsCollection().document(sessionId).update("weather", weather.name).await()
        Unit
    }

    override suspend fun updateRunField(sessionId: String, runId: String, path: String, value: Any): Result<Unit> = runCatching {
        runsCollection(sessionId).document(runId).update(path, value).await()
        Unit
    }
}
