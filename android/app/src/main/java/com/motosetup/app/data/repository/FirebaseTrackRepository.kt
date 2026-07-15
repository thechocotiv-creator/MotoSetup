package com.motosetup.app.data.repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.motosetup.app.model.Track
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.tasks.await

@Singleton
class FirebaseTrackRepository @Inject constructor(
    private val auth: FirebaseAuth,
    private val firestore: FirebaseFirestore,
) : TrackRepository {

    private fun curatedTracksCollection() = firestore.collection("tracks")

    private fun customTracksCollection() =
        firestore.collection("users").document(auth.currentUser?.uid.orEmpty()).collection("customTracks")

    private fun observeCollection(collection: com.google.firebase.firestore.CollectionReference): Flow<List<Track>> = callbackFlow {
        val listener = collection.addSnapshotListener { snapshot, error ->
            if (error != null) return@addSnapshotListener
            trySend(snapshot?.toObjects(Track::class.java).orEmpty())
        }
        awaitClose { listener.remove() }
    }

    override fun observeTracks(): Flow<List<Track>> =
        combine(observeCollection(curatedTracksCollection()), observeCollection(customTracksCollection())) { curated, custom ->
            curated + custom
        }

    override suspend fun addCustomTrack(name: String): Result<Unit> = runCatching {
        customTracksCollection().add(Track(name = name)).await()
        Unit
    }
}
