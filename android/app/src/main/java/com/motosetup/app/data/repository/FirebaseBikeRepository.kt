package com.motosetup.app.data.repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.motosetup.app.model.Bike
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

@Singleton
class FirebaseBikeRepository @Inject constructor(
    private val auth: FirebaseAuth,
    private val firestore: FirebaseFirestore,
) : BikeRepository {

    private fun bikesCollection() =
        firestore.collection("users").document(auth.currentUser?.uid.orEmpty()).collection("bikes")

    override fun observeBikes(): Flow<List<Bike>> = callbackFlow {
        val listener = bikesCollection().addSnapshotListener { snapshot, error ->
            if (error != null) return@addSnapshotListener
            trySend(snapshot?.toObjects(Bike::class.java).orEmpty())
        }
        awaitClose { listener.remove() }
    }

    override fun observeBike(bikeId: String): Flow<Bike?> = callbackFlow {
        val listener = bikesCollection().document(bikeId).addSnapshotListener { snapshot, error ->
            if (error != null) return@addSnapshotListener
            trySend(snapshot?.toObject(Bike::class.java))
        }
        awaitClose { listener.remove() }
    }

    override suspend fun addBike(bike: Bike): Result<Unit> = runCatching {
        bikesCollection().add(bike).await()
        Unit
    }

    override suspend fun updateBike(bike: Bike): Result<Unit> = runCatching {
        bikesCollection().document(bike.id).set(bike).await()
        Unit
    }

    /** Cancella prima la subcollection maintenance, poi il documento moto — non serve una Cloud Function per un solo livello di annidamento. */
    override suspend fun deleteBike(bikeId: String): Result<Unit> = runCatching {
        val bikeDoc = bikesCollection().document(bikeId)
        val maintenanceDocs = bikeDoc.collection("maintenance").get().await()
        val batch = firestore.batch()
        maintenanceDocs.documents.forEach { batch.delete(it.reference) }
        batch.delete(bikeDoc)
        batch.commit().await()
        Unit
    }
}
