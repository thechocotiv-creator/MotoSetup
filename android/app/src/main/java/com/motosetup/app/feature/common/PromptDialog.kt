package com.motosetup.app.feature.common

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import com.motosetup.app.feature.onboarding.AuthTextField
import com.motosetup.app.feature.onboarding.GlassButton
import com.motosetup.app.feature.onboarding.PrimaryButton
import com.motosetup.app.ui.theme.AppColor
import com.motosetup.app.ui.theme.AppHeadline
import com.motosetup.app.ui.theme.AppRadius
import com.motosetup.app.ui.theme.AppSpacing
import com.motosetup.app.ui.theme.appGlass

/**
 * Overlay di input compatti (equivalente dei window.prompt del mockup HTML),
 * scoped alla singola schermata — non passano dal sistema globale di
 * sheet/dialog di RootScaffold perché non servono ad altre schermate. Riusa
 * AuthTextField/PrimaryButton/GlassButton per restare visivamente coerente
 * con onboarding e AppAlertDialogHost.
 */
@Composable
private fun PromptContainer(onDismissRequest: () -> Unit, content: @Composable ColumnScope.() -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.4f))
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = onDismissRequest,
            ),
        contentAlignment = Alignment.Center,
    ) {
        Column(
            modifier = Modifier
                .padding(AppSpacing.xl)
                .fillMaxWidth()
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null,
                    onClick = {},
                )
                .appGlass(cornerRadius = AppRadius.alert)
                .padding(AppSpacing.xl),
            content = content,
        )
    }
}

@Composable
fun TextPromptDialog(
    title: String,
    label: String,
    initialValue: String = "",
    confirmLabel: String = "Salva",
    onConfirm: (String) -> Unit,
    onDismiss: () -> Unit,
) {
    var value by remember { mutableStateOf(initialValue) }
    PromptContainer(onDismissRequest = onDismiss) {
        Text(text = title, style = AppHeadline, color = AppColor.textPrimary)
        Box(modifier = Modifier.padding(top = AppSpacing.lg)) {
            AuthTextField(value = value, onValueChange = { value = it }, label = label)
        }
        PromptActions(
            confirmLabel = confirmLabel,
            confirmEnabled = value.isNotBlank(),
            onConfirm = { onConfirm(value.trim()) },
            onDismiss = onDismiss,
        )
    }
}

@Composable
fun NumberPromptDialog(
    title: String,
    label: String,
    initialValue: Int,
    confirmLabel: String = "Salva",
    onConfirm: (Int) -> Unit,
    onDismiss: () -> Unit,
) {
    var value by remember { mutableStateOf(initialValue.toString()) }
    val parsed = value.toIntOrNull()
    PromptContainer(onDismissRequest = onDismiss) {
        Text(text = title, style = AppHeadline, color = AppColor.textPrimary)
        Box(modifier = Modifier.padding(top = AppSpacing.lg)) {
            AuthTextField(value = value, onValueChange = { value = it }, label = label, keyboardType = KeyboardType.Number)
        }
        PromptActions(
            confirmLabel = confirmLabel,
            confirmEnabled = parsed != null && parsed >= 0,
            onConfirm = { parsed?.let(onConfirm) },
            onDismiss = onDismiss,
        )
    }
}

@Composable
fun NewMaintenanceItemPromptDialog(
    onConfirm: (name: String, intervalDays: Int) -> Unit,
    onDismiss: () -> Unit,
) {
    var name by remember { mutableStateOf("") }
    var interval by remember { mutableStateOf("30") }
    val parsedInterval = interval.toIntOrNull()
    PromptContainer(onDismissRequest = onDismiss) {
        Text(text = "Nuova voce manutenzione", style = AppHeadline, color = AppColor.textPrimary)
        Box(modifier = Modifier.padding(top = AppSpacing.lg)) {
            AuthTextField(value = name, onValueChange = { name = it }, label = "Nome")
        }
        Box(modifier = Modifier.padding(top = AppSpacing.md)) {
            AuthTextField(
                value = interval,
                onValueChange = { interval = it },
                label = "Ogni quanti giorni",
                keyboardType = KeyboardType.Number,
            )
        }
        PromptActions(
            confirmLabel = "Aggiungi",
            confirmEnabled = name.isNotBlank() && parsedInterval != null && parsedInterval > 0,
            onConfirm = { parsedInterval?.let { onConfirm(name.trim(), it) } },
            onDismiss = onDismiss,
        )
    }
}

@Composable
private fun PromptActions(confirmLabel: String, confirmEnabled: Boolean, onConfirm: () -> Unit, onDismiss: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = AppSpacing.lg),
        horizontalArrangement = Arrangement.spacedBy(AppSpacing.sm),
    ) {
        GlassButton(text = "Annulla", onClick = onDismiss, modifier = Modifier.weight(1f))
        Box(modifier = Modifier.weight(1f)) {
            PrimaryButton(text = confirmLabel, onClick = onConfirm, enabled = confirmEnabled)
        }
    }
}
