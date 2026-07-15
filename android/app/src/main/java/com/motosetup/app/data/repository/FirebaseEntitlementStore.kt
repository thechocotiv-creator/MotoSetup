package com.motosetup.app.data.repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.time.LocalDate
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

@Singleton
class FirebaseEntitlementStore @Inject constructor(
    private val auth: FirebaseAuth,
    private val firestore: FirebaseFirestore,
) : EntitlementStore {

    private fun userDoc() = firestore.collection("users").document(auth.currentUser?.uid.orEmpty())

    override val isPremium: Flow<Boolean> = callbackFlow {
        val listener = userDoc().addSnapshotListener { snapshot, error ->
            if (error != null) return@addSnapshotListener
            trySend(snapshot?.getString("plan") == "premium")
        }
        awaitClose { listener.remove() }
    }

    override val aiAdviceUsedToday: Flow<Int> = callbackFlow {
        val listener = userDoc().addSnapshotListener { snapshot, error ->
            if (error != null) return@addSnapshotListener
            val usedToday = if (snapshot?.getString("aiUsageDate") == todayDateString()) {
                (snapshot.getLong("aiUsedToday") ?: 0L).toInt()
            } else {
                0
            }
            trySend(usedToday)
        }
        awaitClose { listener.remove() }
    }

    /** Transazione: legge il contatore e lo azzera se l'ultimo utilizzo non è di oggi, prima di incrementarlo. */
    override suspend fun recordAiAdviceUsage(): Result<Unit> = runCatching {
        val doc = userDoc()
        val today = todayDateString()
        firestore.runTransaction { transaction ->
            val snapshot = transaction.get(doc)
            val usedToday = if (snapshot.getString("aiUsageDate") == today) (snapshot.getLong("aiUsedToday") ?: 0L) else 0L
            transaction.update(doc, mapOf("aiUsedToday" to usedToday + 1, "aiUsageDate" to today))
            Unit
        }.await()
        Unit
    }

    private fun todayDateString(): String = LocalDate.now().toString()
}
