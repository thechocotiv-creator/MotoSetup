package com.motosetup.app.feature.onboarding

import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import androidx.credentials.exceptions.GetCredentialException
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.motosetup.app.R
import com.motosetup.app.feature.auth.AuthViewModel
import com.motosetup.app.ui.theme.AppColor
import java.security.MessageDigest
import java.util.UUID
import kotlinx.coroutines.launch

@Composable
fun OnboardingHost(authViewModel: AuthViewModel) {
    var step by remember { mutableStateOf(OnboardingStep.Login) }
    val postAuthOnboardingActive by authViewModel.postAuthOnboardingActive.collectAsState()
    val isLoading by authViewModel.isLoading.collectAsState()
    val uiMessage by authViewModel.uiMessage.collectAsState()

    LaunchedEffect(postAuthOnboardingActive) {
        if (postAuthOnboardingActive && step != OnboardingStep.Welcome) {
            step = OnboardingStep.PremiumOffer
        }
    }

    val context = LocalContext.current
    val credentialManager = remember { CredentialManager.create(context) }
    val scope = rememberCoroutineScope()
    var googleErrorMessage by remember { mutableStateOf<String?>(null) }

    fun launchGoogleSignIn() {
        googleErrorMessage = null
        scope.launch {
            try {
                val idToken = requestGoogleIdToken(context, credentialManager)
                authViewModel.signInWithGoogle(idToken)
            } catch (e: GetCredentialException) {
                googleErrorMessage = "Accesso con Google non disponibile su questo dispositivo."
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(AppColor.background)
            .windowInsetsPadding(WindowInsets.safeDrawing),
    ) {
        when (step) {
            OnboardingStep.Login -> LoginScreen(
                isLoading = isLoading,
                errorMessage = uiMessage ?: googleErrorMessage,
                onLogin = authViewModel::login,
                onGoogleSignIn = ::launchGoogleSignIn,
                onNavigateToRegister = { step = OnboardingStep.Register },
            )
            OnboardingStep.Register -> RegisterScreen(
                isLoading = isLoading,
                errorMessage = uiMessage,
                onRegister = authViewModel::register,
                onNavigateToLogin = { step = OnboardingStep.Login },
            )
            OnboardingStep.PremiumOffer -> PremiumOfferScreen(
                onContinue = { step = OnboardingStep.Welcome },
            )
            OnboardingStep.Welcome -> WelcomeScreen(
                onContinue = { authViewModel.finishOnboarding() },
            )
        }
    }
}

/** Nonce SHA-256 per protezione replay, come raccomandato da Google per il Credential Manager. */
private suspend fun requestGoogleIdToken(
    context: Context,
    credentialManager: CredentialManager,
): String {
    val rawNonce = UUID.randomUUID().toString()
    val hashedNonce = MessageDigest.getInstance("SHA-256")
        .digest(rawNonce.toByteArray())
        .joinToString("") { "%02x".format(it) }

    val googleIdOption = GetGoogleIdOption.Builder()
        .setFilterByAuthorizedAccounts(false)
        .setServerClientId(context.getString(R.string.default_web_client_id))
        .setNonce(hashedNonce)
        .build()

    val request = GetCredentialRequest.Builder()
        .addCredentialOption(googleIdOption)
        .build()

    val response = credentialManager.getCredential(context, request)
    val credential = response.credential
    require(credential is CustomCredential && credential.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL) {
        "Credenziale inattesa dal Credential Manager"
    }
    return GoogleIdTokenCredential.createFrom(credential.data).idToken
}
