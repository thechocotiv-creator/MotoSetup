package com.motosetup.app.data.repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.motosetup.app.model.MaintenanceItem
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

@Singleton
class FirebaseMaintenanceRepository @Inject constructor(
    private val auth: FirebaseAuth,
    private val firestore: FirebaseFirestore,
) : MaintenanceRepository {

    private fun maintenanceCollection(bikeId: String) = firestore
        .collection("users").document(auth.currentUser?.uid.orEmpty())
        .collection("bikes").document(bikeId)
        .collection("maintenance")

    override fun observeItems(bikeId: String): Flow<List<MaintenanceItem>> = callbackFlow {
        val listener = maintenanceCollection(bikeId).addSnapshotListener { snapshot, error ->
            if (error != null) return@addSnapshotListener
            trySend(snapshot?.toObjects(MaintenanceItem::class.java).orEmpty())
        }
        awaitClose { listener.remove() }
    }

    override suspend fun addItem(bikeId: String, item: MaintenanceItem): Result<Unit> = runCatching {
        maintenanceCollection(bikeId).add(item).await()
        Unit
    }

    override suspend fun updateItem(bikeId: String, item: MaintenanceItem): Result<Unit> = runCatching {
        maintenanceCollection(bikeId).document(item.id).set(item).await()
        Unit
    }
}
