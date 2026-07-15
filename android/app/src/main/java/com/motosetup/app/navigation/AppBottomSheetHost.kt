package com.motosetup.app.navigation

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.TwoWheeler
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.motosetup.app.feature.common.AppPickerSheetContent
import com.motosetup.app.feature.home.BikeEditViewModel
import com.motosetup.app.feature.onboarding.AuthTextField
import com.motosetup.app.feature.sessioni.PickerViewModel
import com.motosetup.app.feature.sessioni.pickerColumns
import com.motosetup.app.feature.sessioni.pickerInitialIndices
import com.motosetup.app.feature.sessioni.pickerSeparators
import com.motosetup.app.feature.sessioni.pickerTitle
import com.motosetup.app.feature.onboarding.PrimaryButton
import com.motosetup.app.model.BikeRarity
import com.motosetup.app.ui.theme.AppCaption
import com.motosetup.app.ui.theme.AppColor
import com.motosetup.app.ui.theme.AppEyebrow
import com.motosetup.app.ui.theme.AppHeadline
import com.motosetup.app.ui.theme.AppRadius
import com.motosetup.app.ui.theme.AppSpacing
import com.motosetup.app.ui.theme.BikeCardColor
import com.motosetup.app.ui.theme.appGlass

/**
 * Bottom sheet custom, non Material3 ModalBottomSheet: quest'ultimo apre in
 * una Dialog (finestra separata) che Haze non può sfocare. Overlay nello
 * stesso Box/finestra di RootScaffold per mantenere il backdrop blur — vedi
 * android/CLAUDE.md, rischio "Sheet/dialog e Haze".
 */
@Composable
fun AppBottomSheetHost(sheet: AppSheet?, onDismiss: () -> Unit) {
    val latest = rememberLatestNonNull(sheet)
    BackHandler(enabled = sheet != null, onBack = onDismiss)

    AnimatedVisibility(visible = sheet != null, enter = fadeIn(), exit = fadeOut()) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.4f))
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null,
                    onClick = onDismiss,
                ),
        )
    }

    AnimatedVisibility(
        visible = sheet != null,
        enter = slideInVertically(initialOffsetY = { it }) + fadeIn(),
        exit = slideOutVertically(targetOffsetY = { it }) + fadeOut(),
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.BottomCenter,
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .windowInsetsPadding(WindowInsets.navigationBars)
                    .padding(AppSpacing.lg)
                    .appGlass(cornerRadius = AppRadius.bottomSheet)
                    .padding(AppSpacing.xl),
            ) {
                if (latest != null) {
                    SheetContent(latest, onDismiss)
                }
            }
        }
    }
}

@Composable
private fun SheetContent(sheet: AppSheet, onDismiss: () -> Unit) {
    when (sheet) {
        is AppSheet.ModificaMoto -> ModificaMotoSheetContent(sheet.bikeId, onDismiss)
        AppSheet.ModificaProfilo -> PlaceholderSheetContent("Modifica profilo") // contenuto reale in Fase 6
        AppSheet.ModificaPassword -> PlaceholderSheetContent("Modifica password") // contenuto reale in Fase 6
        AppSheet.Abbonamento -> PlaceholderSheetContent("Abbonamento") // contenuto reale in Fase 6
        is AppSheet.Picker -> PickerSheetContent(sheet, onDismiss)
    }
}

@Composable
private fun PickerSheetContent(sheet: AppSheet.Picker, onDismiss: () -> Unit) {
    val viewModel: PickerViewModel = hiltViewModel()
    LaunchedEffect(sheet.sessionId, sheet.runId) { viewModel.load(sheet.sessionId, sheet.runId) }
    val run by viewModel.run.collectAsState()

    run?.let { loadedRun ->
        AppPickerSheetContent(
            title = pickerTitle(sheet.kind),
            columns = pickerColumns(sheet.kind),
            separators = pickerSeparators(sheet.kind),
            initialIndices = pickerInitialIndices(sheet.kind, loadedRun),
            onDone = { indices ->
                viewModel.confirm(sheet.sessionId, sheet.runId, sheet.kind, indices)
                onDismiss()
            },
            onCancel = onDismiss,
        )
    }
}

@Composable
private fun PlaceholderSheetContent(title: String) {
    Text(text = title, style = AppHeadline, color = AppColor.textPrimary)
}

@Composable
private fun ModificaMotoSheetContent(bikeId: String, onDismiss: () -> Unit) {
    val viewModel: BikeEditViewModel = hiltViewModel()
    LaunchedEffect(bikeId) { viewModel.load(bikeId) }
    val state by viewModel.uiState.collectAsState()

    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Text(text = "Modifica moto", style = AppHeadline, color = AppColor.textPrimary)
        }

        Text(
            text = "FOTO MOTO",
            style = AppEyebrow,
            color = AppColor.textSecondary,
            modifier = Modifier.padding(top = AppSpacing.lg, bottom = AppSpacing.sm),
        )
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(16f / 9f)
                .clip(RoundedCornerShape(AppRadius.card))
                .background(AppColor.panel),
            contentAlignment = Alignment.Center,
        ) {
            Icon(Icons.Filled.TwoWheeler, contentDescription = null, tint = AppColor.textSecondary, modifier = Modifier.size(48.dp))
        }

        Text(
            text = "NOME MOTO",
            style = AppEyebrow,
            color = AppColor.textSecondary,
            modifier = Modifier.padding(top = AppSpacing.lg, bottom = AppSpacing.sm),
        )
        AuthTextField(value = state.name, onValueChange = viewModel::updateName, label = "Nome")

        Text(
            text = "CATEGORIA",
            style = AppEyebrow,
            color = AppColor.textSecondary,
            modifier = Modifier.padding(top = AppSpacing.lg, bottom = AppSpacing.sm),
        )
        AuthTextField(value = state.subtitle, onValueChange = viewModel::updateSubtitle, label = "Categoria")

        Text(
            text = "COLORE CARD",
            style = AppEyebrow,
            color = AppColor.textSecondary,
            modifier = Modifier.padding(top = AppSpacing.lg, bottom = AppSpacing.sm),
        )
        Row(horizontalArrangement = Arrangement.spacedBy(AppSpacing.sm)) {
            BikeCardColor.entries.forEach { color ->
                val selected = color == state.cardColor
                Box(
                    modifier = Modifier
                        .size(34.dp)
                        .clip(CircleShape)
                        .background(color.swatch)
                        .border(
                            width = if (selected) 2.dp else 0.dp,
                            color = AppColor.textPrimary,
                            shape = CircleShape,
                        )
                        .clickable { viewModel.updateCardColor(color) },
                )
            }
        }

        Text(
            text = "INDICE DI RARITÀ",
            style = AppEyebrow,
            color = AppColor.textSecondary,
            modifier = Modifier.padding(top = AppSpacing.lg, bottom = AppSpacing.sm),
        )
        Row(horizontalArrangement = Arrangement.spacedBy(AppSpacing.sm)) {
            BikeRarity.entries.forEach { rarity ->
                val selected = rarity == state.rarity
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(AppRadius.button))
                        .background(if (selected) AppColor.accentBlue.copy(alpha = 0.25f) else AppColor.panel)
                        .clickable { viewModel.updateRarity(rarity) }
                        .padding(vertical = AppSpacing.sm),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(text = rarity.name, style = AppCaption, color = if (selected) AppColor.textPrimary else AppColor.textSecondary)
                }
            }
        }

        Box(modifier = Modifier.padding(top = AppSpacing.xl)) {
            PrimaryButton(text = "Salva", onClick = { viewModel.save(onSaved = onDismiss) })
        }
    }
}
