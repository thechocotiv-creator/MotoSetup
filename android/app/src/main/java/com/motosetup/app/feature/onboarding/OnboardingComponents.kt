package com.motosetup.app.feature.onboarding

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import com.motosetup.app.ui.theme.AppBody
import com.motosetup.app.ui.theme.AppColor
import com.motosetup.app.ui.theme.AppHeadline
import com.motosetup.app.ui.theme.AppRadius
import com.motosetup.app.ui.theme.AppSpacing
import com.motosetup.app.ui.theme.appGlass

@Composable
fun AuthTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    modifier: Modifier = Modifier,
    isPassword: Boolean = false,
    keyboardType: KeyboardType = KeyboardType.Text,
    singleLine: Boolean = true,
) {
    TextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        singleLine = singleLine,
        visualTransformation = if (isPassword) PasswordVisualTransformation() else androidx.compose.ui.text.input.VisualTransformation.None,
        keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
        colors = TextFieldDefaults.colors(
            focusedContainerColor = Color.Transparent,
            unfocusedContainerColor = Color.Transparent,
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent,
            focusedTextColor = AppColor.textPrimary,
            unfocusedTextColor = AppColor.textPrimary,
            focusedLabelColor = AppColor.textSecondary,
            unfocusedLabelColor = AppColor.textSecondary,
            cursorColor = AppColor.accentBlue,
        ),
        modifier = modifier
            .fillMaxWidth()
            .appGlass(cornerRadius = AppRadius.button),
    )
}

@Composable
fun PrimaryButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(AppRadius.button))
            .background(if (enabled) AppColor.textPrimary else AppColor.textPrimary.copy(alpha = 0.4f))
            .clickable(enabled = enabled, onClick = onClick)
            .padding(vertical = AppSpacing.md),
        contentAlignment = Alignment.Center,
    ) {
        Text(text = text, style = AppHeadline, color = AppColor.background)
    }
}

@Composable
fun GlassButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .appGlass(cornerRadius = AppRadius.button)
            .clickable(onClick = onClick)
            .padding(vertical = AppSpacing.md),
        contentAlignment = Alignment.Center,
    ) {
        Text(text = text, style = AppHeadline, color = AppColor.textPrimary)
    }
}

@Composable
fun AuthPillToggle(
    selected: OnboardingStep,
    onSelect: (OnboardingStep) -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .appGlass(cornerRadius = 20.dp)
            .padding(4.dp),
        horizontalArrangement = Arrangement.SpaceEvenly,
    ) {
        PillOption("Accedi", selected == OnboardingStep.Login, Modifier.weight(1f)) {
            onSelect(OnboardingStep.Login)
        }
        PillOption("Registrati", selected == OnboardingStep.Register, Modifier.weight(1f)) {
            onSelect(OnboardingStep.Register)
        }
    }
}

@Composable
private fun PillOption(
    text: String,
    isSelected: Boolean,
    modifier: Modifier,
    onClick: () -> Unit,
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .background(if (isSelected) AppColor.accentBlue.copy(alpha = 0.25f) else Color.Transparent)
            .clickable(onClick = onClick)
            .padding(vertical = AppSpacing.sm),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = text,
            style = AppBody,
            color = if (isSelected) AppColor.textPrimary else AppColor.textSecondary,
        )
    }
}
