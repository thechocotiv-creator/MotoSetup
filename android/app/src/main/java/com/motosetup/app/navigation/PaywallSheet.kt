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
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import com.motosetup.app.ui.theme.AppBody
import com.motosetup.app.ui.theme.AppColor
import com.motosetup.app.ui.theme.AppHeadline
import com.motosetup.app.ui.theme.AppRadius
import com.motosetup.app.ui.theme.AppSpacing
import com.motosetup.app.ui.theme.appGlass

/**
 * Paywall condiviso a livello root, riusato da qualunque schermata tocchi un
 * limite del piano Free (CLAUDE.md radice, tabella "Piano Premium"). Contenuto
 * ("Limite piano Free raggiunto" + messaggio + bottoni Chiudi/Sblocca Premium)
 * fisso da design_handoff #7 — non un content-slot generico come AppSheet.
 */
@Composable
fun PaywallSheet(reason: PaywallReason?, onDismiss: () -> Unit, onUnlockPremium: () -> Unit) {
    val latest = rememberLatestNonNull(reason)
    BackHandler(enabled = reason != null, onBack = onDismiss)

    AnimatedVisibility(visible = reason != null, enter = fadeIn(), exit = fadeOut()) {
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
        visible = reason != null,
        enter = slideInVertically(initialOffsetY = { it }) + fadeIn(),
        exit = slideOutVertically(targetOffsetY = { it }) + fadeOut(),
    ) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.BottomCenter) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .windowInsetsPadding(WindowInsets.navigationBars)
                    .padding(AppSpacing.lg)
                    .appGlass(cornerRadius = AppRadius.bottomSheet)
                    .padding(AppSpacing.xl),
            ) {
                if (latest != null) {
                    Column {
                        Text(text = "Limite piano Free raggiunto", style = AppHeadline, color = AppColor.textPrimary)
                        Text(
                            text = latest.message,
                            style = AppBody,
                            color = AppColor.textSecondary,
                            modifier = Modifier.padding(top = AppSpacing.sm),
                        )
                        Row(
                            modifier = Modifier.fillMaxWidth().padding(top = AppSpacing.lg),
                            horizontalArrangement = Arrangement.spacedBy(AppSpacing.sm),
                        ) {
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .appGlass(cornerRadius = AppRadius.button)
                                    .clickable(onClick = onDismiss)
                                    .padding(vertical = AppSpacing.md),
                                contentAlignment = Alignment.Center,
                            ) {
                                Text(text = "Chiudi", style = AppHeadline, color = AppColor.textPrimary)
                            }
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(AppRadius.button))
                                    .background(AppColor.accentBlue)
                                    .clickable(onClick = onUnlockPremium)
                                    .padding(vertical = AppSpacing.md),
                                contentAlignment = Alignment.Center,
                            ) {
                                Text(text = "Sblocca Premium", style = AppHeadline, color = AppColor.background)
                            }
                        }
                    }
                }
            }
        }
    }
}
