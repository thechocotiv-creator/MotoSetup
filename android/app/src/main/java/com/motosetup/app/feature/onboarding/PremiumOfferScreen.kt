package com.motosetup.app.feature.onboarding

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import com.motosetup.app.ui.theme.AppBody
import com.motosetup.app.ui.theme.AppCaption
import com.motosetup.app.ui.theme.AppColor
import com.motosetup.app.ui.theme.AppFont
import com.motosetup.app.ui.theme.AppRadius
import com.motosetup.app.ui.theme.AppSpacing
import com.motosetup.app.ui.theme.AppTitle
import com.motosetup.app.ui.theme.appGlass

private data class PlanComparisonRow(val feature: String, val free: String, val premium: String)

private val comparisonRows = listOf(
    PlanComparisonRow("Moto in garage", "1 sola moto", "Illimitate"),
    PlanComparisonRow("Consigli AI", "1 al giorno", "Illimitati"),
    PlanComparisonRow("Run per sessione", "Massimo 3", "Illimitati"),
)

@Composable
fun PremiumOfferScreen(
    onContinue: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = AppSpacing.xl, vertical = AppSpacing.xxl),
    ) {
        Text(
            text = "Account creato — passa a Premium?",
            style = AppTitle,
            color = AppColor.textPrimary,
        )

        Spacer(Modifier.height(AppSpacing.xl))

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .appGlass(cornerRadius = AppRadius.card)
                .padding(AppSpacing.lg),
        ) {
            Row(modifier = Modifier.fillMaxWidth()) {
                Text("", modifier = Modifier.weight(1f))
                Text("Free", style = AppFont.style(FontWeight.SemiBold, 13), color = AppColor.textSecondary, modifier = Modifier.weight(1f), textAlign = TextAlign.Center)
                Text("Premium", style = AppFont.style(FontWeight.SemiBold, 13), color = AppColor.accentBlue, modifier = Modifier.weight(1f), textAlign = TextAlign.Center)
            }
            comparisonRows.forEach { row ->
                Spacer(Modifier.height(AppSpacing.md))
                Row(modifier = Modifier.fillMaxWidth()) {
                    Text(row.feature, style = AppCaption, color = AppColor.textPrimary, modifier = Modifier.weight(1f))
                    Text(row.free, style = AppCaption, color = AppColor.textSecondary, modifier = Modifier.weight(1f), textAlign = TextAlign.Center)
                    Text(row.premium, style = AppCaption, color = AppColor.accentBlue, modifier = Modifier.weight(1f), textAlign = TextAlign.Center)
                }
            }
        }

        Spacer(Modifier.height(AppSpacing.xxl))

        // Play Billing resta fake fino a Fase 7: nessun acquisto reale qui, solo navigazione.
        PrimaryButton(text = "Sblocca Premium — €9,99 una tantum", onClick = onContinue)

        Spacer(Modifier.height(AppSpacing.lg))

        Text(
            text = "Continua con Free",
            style = AppBody,
            color = AppColor.textSecondary,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .fillMaxWidth()
                .clickable(onClick = onContinue)
                .padding(vertical = AppSpacing.sm),
        )
    }
}
