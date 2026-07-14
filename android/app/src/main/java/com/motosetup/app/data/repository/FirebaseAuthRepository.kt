package com.motosetup.app.data.repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.firestore.FirebaseFirestore
import com.motosetup.app.model.AppUser
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

@Singleton
class FirebaseAuthRepository @Inject constructor(
    private val auth: FirebaseAuth,
    private val firestore: FirebaseFirestore,
) : AuthRepository {

    override val authState: Flow<AuthState> = callbackFlow {
        val listener = FirebaseAuth.AuthStateListener { firebaseAuth ->
            val user = firebaseAuth.currentUser
            trySend(if (user == null) AuthState.LoggedOut else AuthState.LoggedIn(user.uid))
        }
        auth.addAuthStateListener(listener)
        awaitClose { auth.removeAuthStateListener(listener) }
    }

    override suspend fun signInWithEmail(email: String, password: String): Result<Unit> =
        runCatching {
            auth.signInWithEmailAndPassword(email, password).await()
            Unit
        }

    override suspend fun registerWithEmail(
        nickname: String,
        email: String,
        password: String,
    ): Result<Boolean> = runCatching {
        val result = auth.createUserWithEmailAndPassword(email, password).await()
        val uid = requireNotNull(result.user?.uid)
        createUserDocumentIfMissing(uid, nickname, email)
        true
    }

    override suspend fun signInWithGoogle(idToken: String): Result<Boolean> = runCatching {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        val result = auth.signInWithCredential(credential).await()
        val user = requireNotNull(result.user)
        createUserDocumentIfMissing(
            uid = user.uid,
            nickname = user.displayName.orEmpty(),
            email = user.email.orEmpty(),
        )
    }

    override fun signOut() = auth.signOut()

    /** Ritorna true se il documento non esisteva ed è stato appena creato (nuovo account). */
    private suspend fun createUserDocumentIfMissing(uid: String, nickname: String, email: String): Boolean {
        val userDoc = firestore.collection("users").document(uid)
        val snapshot = userDoc.get().await()
        if (snapshot.exists()) return false

        userDoc.set(AppUser(uid = uid, nickname = nickname, email = email, plan = "free")).await()
        return true
    }
}
