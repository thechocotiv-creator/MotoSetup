package com.motosetup.app.data.repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow

@Singleton
class FirebaseEntitlementStore @Inject constructor(
    private val auth: FirebaseAuth,
    private val firestore: FirebaseFirestore,
) : EntitlementStore {

    override val isPremium: Flow<Boolean> = callbackFlow {
        val userDoc = firestore.collection("users").document(auth.currentUser?.uid.orEmpty())
        val listener = userDoc.addSnapshotListener { snapshot, error ->
            if (error != null) return@addSnapshotListener
            trySend(snapshot?.getString("plan") == "premium")
        }
        awaitClose { listener.remove() }
    }
}
