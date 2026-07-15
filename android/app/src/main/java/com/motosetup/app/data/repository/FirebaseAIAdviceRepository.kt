package com.motosetup.app.data.repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.motosetup.app.data.ai.generateMockAdvice
import com.motosetup.app.model.AIAdviceEntry
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

@Singleton
class FirebaseAIAdviceRepository @Inject constructor(
    private val auth: FirebaseAuth,
    private val firestore: FirebaseFirestore,
    private val entitlementStore: EntitlementStore,
) : AIAdviceRepository {

    private fun adviceCollection() =
        firestore.collection("users").document(auth.currentUser?.uid.orEmpty()).collection("aiAdvice")

    override fun observeEntries(): Flow<List<AIAdviceEntry>> = callbackFlow {
        val listener = adviceCollection()
            .orderBy("createdAt", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null) return@addSnapshotListener
                trySend(snapshot?.toObjects(AIAdviceEntry::class.java).orEmpty())
            }
        awaitClose { listener.remove() }
    }

    /** Genera il consiglio con l'euristica locale (vedi MockAiAdvisor) finché la Cloud Function LLM non è pronta — CLAUDE.md radice. */
    override suspend fun askAdvice(question: String): Result<AIAdviceEntry> = runCatching {
        val mock = generateMockAdvice(question)
        val entry = AIAdviceEntry(
            question = question,
            parameterName = mock.parameterName,
            parameterValue = mock.parameterValue,
            explanation = mock.explanation,
        )
        adviceCollection().add(entry).await()
        entitlementStore.recordAiAdviceUsage().getOrThrow()
        entry
    }
}
