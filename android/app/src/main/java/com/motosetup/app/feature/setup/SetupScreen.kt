package com.motosetup.app.feature.setup

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import com.motosetup.app.ui.theme.AppColor
import com.motosetup.app.ui.theme.AppLargeTitle
import com.motosetup.app.ui.theme.AppSpacing

/**
 * Placeholder Fase 0 — contenuto reale (sessioni, nuova sessione, dettaglio
 * run) arriva in Fase 4. Vedi design_handoff_motosetup_app/README.md #3-4.
 */
@Composable
fun SetupScreen() {
    Column(
        modifier = Modifier.fillMaxSize().padding(top = AppSpacing.xl),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            text = "Setup",
            style = AppLargeTitle,
            color = AppColor.textPrimary,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth(),
        )
    }
}
