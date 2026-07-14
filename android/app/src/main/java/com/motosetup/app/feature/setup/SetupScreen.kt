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
import com.motosetup.app.feature.onboarding.GlassButton
import com.motosetup.app.navigation.AppRoute
import com.motosetup.app.navigation.LocalAppNavActions
import com.motosetup.app.ui.theme.AppColor
import com.motosetup.app.ui.theme.AppLargeTitle
import com.motosetup.app.ui.theme.AppSpacing

/**
 * Placeholder Fase 0 — contenuto reale (sessioni, nuova sessione, dettaglio
 * run) arriva in Fase 4. Vedi design_handoff_motosetup_app/README.md #3-4.
 * I bottoni sotto sono cablaggio temporaneo Fase 2 per esercitare i push di
 * Navigation 3 — la Fase 4 li sostituirà con i trigger UI reali (lista
 * sessioni, run selector) nello stesso punto.
 */
@Composable
fun SetupScreen() {
    val actions = LocalAppNavActions.current
    Column(
        modifier = Modifier.fillMaxSize().padding(top = AppSpacing.xl, start = AppSpacing.xl, end = AppSpacing.xl),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            text = "Setup",
            style = AppLargeTitle,
            color = AppColor.textPrimary,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth(),
        )
        GlassButton(
            text = "Nuova sessione",
            onClick = { actions.navigate(AppRoute.NuovaSessione) },
            modifier = Modifier.padding(top = AppSpacing.xl),
        )
        GlassButton(
            text = "Tutte le sessioni",
            onClick = { actions.navigate(AppRoute.TutteLeSessioni) },
            modifier = Modifier.padding(top = AppSpacing.sm),
        )
        GlassButton(
            text = "Dettaglio run",
            onClick = { actions.navigate(AppRoute.DettaglioRun(sessionId = "demo-session-id", runId = "demo-run-id")) },
            modifier = Modifier.padding(top = AppSpacing.sm),
        )
    }
}
