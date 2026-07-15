package com.motosetup.app.data.repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.motosetup.app.model.ChecklistItem
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

@Singleton
class FirebaseChecklistRepository @Inject constructor(
    private val auth: FirebaseAuth,
    private val firestore: FirebaseFirestore,
) : ChecklistRepository {

    private fun checklistCollection() =
        firestore.collection("users").document(auth.currentUser?.uid.orEmpty()).collection("checklist")

    override fun observeItems(): Flow<List<ChecklistItem>> = callbackFlow {
        val listener = checklistCollection()
            .orderBy("createdAt", Query.Direction.ASCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null) return@addSnapshotListener
                trySend(snapshot?.toObjects(ChecklistItem::class.java).orEmpty())
            }
        awaitClose { listener.remove() }
    }

    override suspend fun addItem(label: String): Result<Unit> = runCatching {
        checklistCollection().add(ChecklistItem(label = label)).await()
        Unit
    }

    /**
     * `.set()` sull'oggetto intero, non un update per field-path: più robusto rispetto a nomi di
     * campo che potrebbero non corrispondere esattamente al nome della proprietà Kotlin nel
     * mapping POJO di Firestore (vedi commento su ChecklistItem.done — bug osservato in verifica
     * manuale con un campo chiamato "isDone"). `.set()` passa dallo stesso percorso di
     * serializzazione di `addItem`, coerente in entrambe le direzioni.
     */
    override suspend fun updateItem(item: ChecklistItem): Result<Unit> = runCatching {
        checklistCollection().document(item.id).set(item).await()
        Unit
    }

    override suspend fun deleteItem(itemId: String): Result<Unit> = runCatching {
        checklistCollection().document(itemId).delete().await()
        Unit
    }
}
