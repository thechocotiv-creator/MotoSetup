package com.motosetup.app.feature.sessioni

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.motosetup.app.navigation.BackButton
import com.motosetup.app.ui.theme.AppColor
import com.motosetup.app.ui.theme.AppLargeTitle
import com.motosetup.app.ui.theme.AppSpacing

/**
 * Placeholder Fase 2 — contenuto reale (selettore run, categorie
 * Sospensioni/Gomme/Rapporti/Elettronica) arriva in Fase 4. Vedi
 * design_handoff_motosetup_app/README.md #4.
 */
@Composable
fun DettaglioRunScreen(sessionId: String, runId: String) {
    Column(
        modifier = Modifier.fillMaxSize().padding(AppSpacing.xl),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(text = "Dettaglio run", style = AppLargeTitle, color = AppColor.textPrimary)
        Text(text = "sessionId: $sessionId, runId: $runId", color = AppColor.textSecondary)
        BackButton(modifier = Modifier.padding(top = AppSpacing.xl))
    }
}
