package com.motosetup.app.navigation

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.hilt.navigation.compose.hiltViewModel
import com.motosetup.app.feature.home.BikeActionsViewModel
import com.motosetup.app.feature.profilo.AccountActionsViewModel
import com.motosetup.app.ui.theme.AppBody
import com.motosetup.app.ui.theme.AppColor
import com.motosetup.app.ui.theme.AppHeadline
import com.motosetup.app.ui.theme.AppRadius
import com.motosetup.app.ui.theme.AppSpacing
import com.motosetup.app.ui.theme.appGlass

/**
 * Alert dialog custom (non Material3 AlertDialog) per lo stesso motivo di
 * AppBottomSheetHost: la Dialog di sistema è una finestra separata che Haze
 * non può sfocare.
 */
@Composable
fun AppAlertDialogHost(dialog: AppDialog?, onDismiss: () -> Unit) {
    val latest = rememberLatestNonNull(dialog)
    BackHandler(enabled = dialog != null, onBack = onDismiss)

    AnimatedVisibility(visible = dialog != null, enter = fadeIn(), exit = fadeOut()) {
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
        visible = dialog != null,
        enter = scaleIn(initialScale = 0.9f) + fadeIn(),
        exit = scaleOut(targetScale = 0.9f) + fadeOut(),
    ) {
        Box(modifier = Modifier.fillMaxSize().padding(AppSpacing.xl), contentAlignment = Alignment.Center) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .appGlass(cornerRadius = AppRadius.alert)
                    .padding(AppSpacing.xl),
            ) {
                if (latest != null) {
                    DialogContent(latest, onDismiss)
                }
            }
        }
    }
}

@Composable
private fun DialogContent(dialog: AppDialog, onDismiss: () -> Unit) {
    when (dialog) {
        is AppDialog.EliminaMoto -> EliminaMotoDialogContent(dialog.bikeId, onDismiss)
        AppDialog.EliminaAccount -> EliminaAccountDialogContent(onDismiss)
    }
}

@Composable
private fun EliminaAccountDialogContent(onDismiss: () -> Unit) {
    val viewModel: AccountActionsViewModel = hiltViewModel()

    ConfirmationDialogContent(
        title = "Eliminare l'account",
        message = "Questa azione è definitiva: perderai moto, sessioni e checklist salvate. Non può essere annullata.",
        onConfirm = {
            viewModel.deleteAccount()
            onDismiss()
        },
        onDismiss = onDismiss,
    )
}

@Composable
private fun EliminaMotoDialogContent(bikeId: String, onDismiss: () -> Unit) {
    val viewModel: BikeActionsViewModel = hiltViewModel()
    val bike by remember(bikeId) { viewModel.observeBike(bikeId) }.collectAsState(initial = null)

    ConfirmationDialogContent(
        title = "Eliminare moto",
        message = "Eliminare ${bike?.name.orEmpty()} dal garage? L'azione non può essere annullata.",
        onConfirm = {
            viewModel.deleteBike(bikeId)
            onDismiss()
        },
        onDismiss = onDismiss,
    )
}

@Composable
private fun ConfirmationDialogContent(title: String, message: String, onConfirm: () -> Unit, onDismiss: () -> Unit) {
    Column {
        Text(text = title, style = AppHeadline, color = AppColor.textPrimary, textAlign = TextAlign.Center, modifier = Modifier.fillMaxWidth())
        Text(
            text = message,
            style = AppBody,
            color = AppColor.textSecondary,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth().padding(top = AppSpacing.sm, bottom = AppSpacing.lg),
        )
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(AppSpacing.sm)) {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .appGlass(cornerRadius = AppRadius.button)
                    .clickable(onClick = onDismiss)
                    .padding(vertical = AppSpacing.md),
                contentAlignment = Alignment.Center,
            ) {
                Text(text = "Annulla", style = AppHeadline, color = AppColor.textPrimary)
            }
            Box(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(AppRadius.button))
                    .background(AppColor.red)
                    .clickable(onClick = onConfirm)
                    .padding(vertical = AppSpacing.md),
                contentAlignment = Alignment.Center,
            ) {
                Text(text = "Elimina", style = AppHeadline, color = AppColor.textPrimary)
            }
        }
    }
}
