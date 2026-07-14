package com.motosetup.app.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable

private val MotoSetupColorScheme = darkColorScheme(
    background = AppColor.background,
    surface = AppColor.panel,
    primary = AppColor.accentBlue,
    onBackground = AppColor.textPrimary,
    onSurface = AppColor.textPrimary,
    error = AppColor.red,
)

/** Tema dark-mode fisso — nessuna light mode, per design. */
@Composable
fun MotoSetupTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = MotoSetupColorScheme,
        content = content,
    )
}
