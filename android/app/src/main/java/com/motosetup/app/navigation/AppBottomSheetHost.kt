package com.motosetup.app.navigation

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.windowInsetsPadding
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
                    SheetContent(latest)
                }
            }
        }
    }
}

@Composable
private fun SheetContent(sheet: AppSheet) {
    val title = when (sheet) {
        is AppSheet.ModificaMoto -> "Modifica moto"
        AppSheet.ModificaProfilo -> "Modifica profilo"
        AppSheet.ModificaPassword -> "Modifica password"
        AppSheet.Abbonamento -> "Abbonamento"
    }
    Text(text = title, style = AppHeadline, color = AppColor.textPrimary)
}
