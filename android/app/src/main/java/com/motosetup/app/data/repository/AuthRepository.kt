package com.motosetup.app.data.repository

import kotlinx.coroutines.flow.Flow

sealed class AuthState {
    data object Loading : AuthState()
    data object LoggedOut : AuthState()
    data class LoggedIn(val uid: String) : AuthState()
}

interface AuthRepository {
    val authState: Flow<AuthState>

    suspend fun signInWithEmail(email: String, password: String): Result<Unit>

    /** Ritorna true se è stato creato un account nuovo (sempre true qui). */
    suspend fun registerWithEmail(nickname: String, email: String, password: String): Result<Boolean>

    /** Ritorna true se l'idToken Google corrisponde a un account mai visto prima. */
    suspend fun signInWithGoogle(idToken: String): Result<Boolean>

    fun signOut()
}
