package com.motosetup.app.navigation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import com.motosetup.app.data.repository.AuthState
import com.motosetup.app.feature.auth.AuthViewModel
import com.motosetup.app.feature.onboarding.OnboardingHost
import com.motosetup.app.ui.theme.AppColor

/**
 * Root: switch Loading / LoggedOut / LoggedIn guidato da AuthViewModel.
 *
 * Firebase notifica il suo authStateListener non appena signIn/createUser
 * completa lato server — prima ancora che AuthViewModel.register/login abbia
 * finito di scrivere su Firestore e impostato postAuthOnboardingActive. Senza
 * il controllo su isLoading qui sotto, questa race porterebbe l'utente dritto
 * in Home saltando l'offerta Premium e "Sei dentro!" dopo una registrazione.
 */
@Composable
fun AppRoot() {
    val authViewModel: AuthViewModel = hiltViewModel()
    val authState by authViewModel.authState.collectAsState()
    val postAuthOnboardingActive by authViewModel.postAuthOnboardingActive.collectAsState()
    val isLoading by authViewModel.isLoading.collectAsState()

    // Un solo call-site per OnboardingHost: se fosse chiamato da due branch diversi
    // del when, Compose lo tratterebbe come due composable distinti e ne resetterebbe
    // lo stato interno (step) ogni volta che la condizione "vera" cambia branch.
    val showOnboarding = authState is AuthState.LoggedOut || isLoading || postAuthOnboardingActive

    when {
        authState is AuthState.Loading -> LoadingScreen()
        showOnboarding -> OnboardingHost(authViewModel)
        else -> RootScaffold()
    }
}

@Composable
private fun LoadingScreen() {
    Box(
        modifier = Modifier.fillMaxSize().background(AppColor.background),
        contentAlignment = Alignment.Center,
    ) {
        CircularProgressIndicator(color = AppColor.accentBlue)
    }
}
