package com.motosetup.app.feature.consigliai

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.motosetup.app.feature.onboarding.AuthTextField
import com.motosetup.app.model.AIAdviceEntry
import com.motosetup.app.navigation.LocalAppNavActions
import com.motosetup.app.navigation.PaywallReason
import com.motosetup.app.ui.theme.AppBody
import com.motosetup.app.ui.theme.AppCaption
import com.motosetup.app.ui.theme.AppColor
import com.motosetup.app.ui.theme.AppEyebrow
import com.motosetup.app.ui.theme.AppLargeTitle
import com.motosetup.app.ui.theme.AppRadius
import com.motosetup.app.ui.theme.AppTitle
import com.motosetup.app.ui.theme.AppSpacing
import com.motosetup.app.ui.theme.appGlass

/** Vedi design_handoff_motosetup_app/README.md #5. */
@Composable
fun ConsigliAIScreen() {
    val actions = LocalAppNavActions.current
    val viewModel: ConsigliAIViewModel = hiltViewModel()
    val state by viewModel.uiState.collectAsState()

    Column(modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(horizontal = AppSpacing.xl)) {
        Text(
            text = "Consigli AI",
            style = AppLargeTitle,
            color = AppColor.textPrimary,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth().padding(top = AppSpacing.xxl, bottom = AppSpacing.lg),
        )

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .appGlass(cornerRadius = 22.dp, tint = AppColor.accentBlue)
                .border(1.dp, AppColor.accentBlue.copy(alpha = 0.45f), RoundedCornerShape(22.dp))
                .padding(AppSpacing.lg),
        ) {
            Text(text = "Descrivi il problema di guida", style = AppCaption, color = AppColor.textSecondary)
            AuthTextField(
                value = state.question,
                onValueChange = viewModel::updateQuestion,
                label = "Es. \"Sottosterzo in staccata forte\"",
                singleLine = false,
                modifier = Modifier.padding(top = AppSpacing.sm, bottom = AppSpacing.md),
            )
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(AppRadius.button))
                    .background(AppColor.accentBlue)
                    .clickable(enabled = state.question.isNotBlank() && !state.isSubmitting) {
                        viewModel.askAdvice(onLimitReached = { actions.showPaywall(PaywallReason.AiAdviceLimit) })
                    }
                    .padding(vertical = AppSpacing.md),
                contentAlignment = Alignment.Center,
            ) {
                Text(text = "Chiedi consiglio", style = AppBody, color = AppColor.background)
            }
            Text(
                text = state.usageLabel,
                style = AppCaption,
                color = AppColor.textSecondary,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth().padding(top = AppSpacing.sm),
            )
        }

        state.latestEntry?.let { latest ->
            Column(modifier = Modifier.fillMaxWidth().padding(top = AppSpacing.xl)) {
                Text(text = "PARAMETRO CONSIGLIATO", style = AppEyebrow, color = AppColor.textPrimary)
                Text(text = latest.parameterName, style = AppTitle, color = AppColor.textPrimary, modifier = Modifier.padding(top = AppSpacing.sm))
                Text(text = latest.parameterValue, style = AppBody, color = AppColor.accentBlue, modifier = Modifier.padding(top = AppSpacing.xs, bottom = AppSpacing.sm))
                Text(text = latest.explanation, style = AppBody, color = AppColor.textSecondary)
            }
        }

        Column(modifier = Modifier.fillMaxWidth().padding(top = AppSpacing.xl, bottom = AppSpacing.xxl)) {
            Text(text = "CRONOLOGIA", style = AppEyebrow, color = AppColor.textSecondary)
            if (state.entries.isEmpty()) {
                Text(
                    text = "Nessun consiglio richiesto finora",
                    style = AppBody,
                    color = AppColor.textSecondary,
                    modifier = Modifier.padding(top = AppSpacing.sm),
                )
            } else {
                state.entries.forEachIndexed { index, entry ->
                    AdviceHistoryRow(entry)
                    if (index < state.entries.lastIndex) {
                        Box(modifier = Modifier.fillMaxWidth().height(1.dp).background(AppColor.textSecondary.copy(alpha = 0.15f)))
                    }
                }
            }
        }
    }
}

@Composable
private fun AdviceHistoryRow(entry: AIAdviceEntry) {
    Column(modifier = Modifier.fillMaxWidth().padding(vertical = AppSpacing.md)) {
        Text(text = entry.question, style = AppBody, color = AppColor.textPrimary)
        Row(
            modifier = Modifier.fillMaxWidth().padding(top = AppSpacing.xs),
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Text(text = "${entry.parameterName} · ${entry.parameterValue}", style = AppCaption, color = AppColor.accentBlue)
            Text(text = relativeAdviceTimeLabel(entry.createdAt), style = AppCaption, color = AppColor.textSecondary)
        }
    }
}
