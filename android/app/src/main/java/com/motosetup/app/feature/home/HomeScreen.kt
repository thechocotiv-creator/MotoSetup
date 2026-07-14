package com.motosetup.app.feature.home

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
 * Placeholder Fase 0 — contenuto reale (ultima sessione, checklist, garage
 * moto) arriva in Fase 3. Vedi design_handoff_motosetup_app/README.md #2.
 * I bottoni sotto sono cablaggio temporaneo Fase 2 per esercitare i push di
 * Navigation 3 — la Fase 3 li sostituirà con i trigger UI reali (card
 * checklist, card moto) nello stesso punto.
 */
@Composable
fun HomeScreen() {
    val actions = LocalAppNavActions.current
    Column(
        modifier = Modifier.fillMaxSize().padding(top = AppSpacing.xl, start = AppSpacing.xl, end = AppSpacing.xl),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            text = "Home",
            style = AppLargeTitle,
            color = AppColor.textPrimary,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth(),
        )
        GlassButton(
            text = "Checklist pista",
            onClick = { actions.navigate(AppRoute.ChecklistPista) },
            modifier = Modifier.padding(top = AppSpacing.xl),
        )
        GlassButton(
            text = "Manutenzione moto",
            onClick = { actions.navigate(AppRoute.ManutenzioneMoto(bikeId = "demo-bike-id")) },
            modifier = Modifier.padding(top = AppSpacing.sm),
        )
    }
}
