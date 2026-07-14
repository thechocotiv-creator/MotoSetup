package com.motosetup.app.feature.profilo

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
import com.motosetup.app.navigation.AppDialog
import com.motosetup.app.navigation.AppSheet
import com.motosetup.app.navigation.LocalAppNavActions
import com.motosetup.app.navigation.PaywallReason
import com.motosetup.app.ui.theme.AppColor
import com.motosetup.app.ui.theme.AppLargeTitle
import com.motosetup.app.ui.theme.AppSpacing

/**
 * Placeholder Fase 0 — contenuto reale arriva in Fase 6.
 * Vedi design_handoff_motosetup_app/README.md #6.
 * I bottoni sotto sono cablaggio temporaneo Fase 2 per esercitare
 * sheet/dialog/paywall — le fasi 6/7 li sostituiranno con i trigger UI reali
 * (card Abbonamento, voce "Elimina account", ecc.) nello stesso punto.
 */
@Composable
fun ProfiloScreen() {
    val actions = LocalAppNavActions.current
    Column(
        modifier = Modifier.fillMaxSize().padding(top = AppSpacing.xl, start = AppSpacing.xl, end = AppSpacing.xl),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            text = "Profilo",
            style = AppLargeTitle,
            color = AppColor.textPrimary,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth(),
        )
        GlassButton(
            text = "Modifica profilo",
            onClick = { actions.openSheet(AppSheet.ModificaProfilo) },
            modifier = Modifier.padding(top = AppSpacing.xl),
        )
        GlassButton(
            text = "Abbonamento",
            onClick = { actions.openSheet(AppSheet.Abbonamento) },
            modifier = Modifier.padding(top = AppSpacing.sm),
        )
        GlassButton(
            text = "Elimina account",
            onClick = { actions.openDialog(AppDialog.EliminaAccount) },
            modifier = Modifier.padding(top = AppSpacing.sm),
        )
        GlassButton(
            text = "Mostra paywall",
            onClick = { actions.showPaywall(PaywallReason.RunLimit) },
            modifier = Modifier.padding(top = AppSpacing.sm),
        )
    }
}
