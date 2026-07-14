package com.motosetup.app.feature.onboarding

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import com.motosetup.app.ui.theme.AppCaption
import com.motosetup.app.ui.theme.AppColor
import com.motosetup.app.ui.theme.AppLargeTitle
import com.motosetup.app.ui.theme.AppSpacing

@Composable
fun RegisterScreen(
    isLoading: Boolean,
    errorMessage: String?,
    onRegister: (nickname: String, email: String, password: String, confirmPassword: String) -> Unit,
    onNavigateToLogin: () -> Unit,
) {
    var nickname by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = AppSpacing.xl, vertical = AppSpacing.xxl),
    ) {
        Text(text = "MotoSetup", style = AppLargeTitle, color = AppColor.textPrimary)

        Spacer(Modifier.height(AppSpacing.xl))

        AuthPillToggle(
            selected = OnboardingStep.Register,
            onSelect = { if (it == OnboardingStep.Login) onNavigateToLogin() },
        )

        Spacer(Modifier.height(AppSpacing.xl))

        AuthTextField(value = nickname, onValueChange = { nickname = it }, label = "Nickname")

        Spacer(Modifier.height(AppSpacing.md))

        AuthTextField(
            value = email,
            onValueChange = { email = it },
            label = "Email",
            keyboardType = KeyboardType.Email,
        )

        Spacer(Modifier.height(AppSpacing.md))

        AuthTextField(value = password, onValueChange = { password = it }, label = "Password", isPassword = true)

        Spacer(Modifier.height(AppSpacing.md))

        AuthTextField(
            value = confirmPassword,
            onValueChange = { confirmPassword = it },
            label = "Conferma password",
            isPassword = true,
        )

        if (errorMessage != null) {
            Spacer(Modifier.height(AppSpacing.sm))
            Text(text = errorMessage, style = AppCaption, color = AppColor.red)
        }

        Spacer(Modifier.height(AppSpacing.xl))

        PrimaryButton(
            text = "Crea account",
            onClick = { onRegister(nickname, email, password, confirmPassword) },
            enabled = !isLoading,
        )

        Spacer(Modifier.height(AppSpacing.lg))

        Text(
            text = "Hai già un account? Accedi",
            style = AppCaption,
            color = AppColor.accentBlue,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .fillMaxWidth()
                .clickable(onClick = onNavigateToLogin)
                .padding(vertical = AppSpacing.sm),
        )
    }
}
