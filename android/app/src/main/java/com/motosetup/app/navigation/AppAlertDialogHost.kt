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
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
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
                    DialogContent(latest)
                }
            }
        }
    }
}

@Composable
private fun DialogContent(dialog: AppDialog) {
    val title = when (dialog) {
        is AppDialog.EliminaMoto -> "Eliminare la moto?"
        AppDialog.EliminaAccount -> "Eliminare l'account?"
    }
    Text(text = title, style = AppHeadline, color = AppColor.textPrimary)
}
