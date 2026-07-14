package com.motosetup.app.feature.manutenzione

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
 * Placeholder Fase 2 — contenuto reale (lista MaintenanceItem con stato
 * Scaduta/In scadenza/OK) arriva in Fase 3. Vedi design_handoff_motosetup_app/README.md #2b.
 */
@Composable
fun ManutenzioneMotoScreen(bikeId: String) {
    Column(
        modifier = Modifier.fillMaxSize().padding(AppSpacing.xl),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(text = "Manutenzione moto", style = AppLargeTitle, color = AppColor.textPrimary)
        Text(text = "bikeId: $bikeId", color = AppColor.textSecondary)
        BackButton(modifier = Modifier.padding(top = AppSpacing.xl))
    }
}
