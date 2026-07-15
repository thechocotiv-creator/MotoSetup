package com.motosetup.app.feature.setup

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Cloud
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.motosetup.app.model.Meteo
import com.motosetup.app.navigation.AppRoute
import com.motosetup.app.navigation.LocalAppNavActions
import com.motosetup.app.ui.theme.AppBody
import com.motosetup.app.ui.theme.AppCaption
import com.motosetup.app.ui.theme.AppColor
import com.motosetup.app.ui.theme.AppEyebrow
import com.motosetup.app.ui.theme.AppHeadline
import com.motosetup.app.ui.theme.AppLargeTitle
import com.motosetup.app.ui.theme.AppRadius
import com.motosetup.app.ui.theme.AppSpacing
import com.motosetup.app.ui.theme.appGlass

/** Vedi design_handoff_motosetup_app/README.md #3. */
@Composable
fun SetupScreen() {
    val actions = LocalAppNavActions.current
    val viewModel: SetupViewModel = hiltViewModel()
    val state by viewModel.uiState.collectAsState()

    Box(modifier = Modifier.fillMaxSize()) {
        Column(modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(horizontal = AppSpacing.xl)) {
            Text(
                text = "Setup",
                style = AppLargeTitle,
                color = AppColor.textPrimary,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth().padding(top = AppSpacing.xxl, bottom = AppSpacing.lg),
            )

            state.today?.let { today ->
                Text(text = "SESSIONE DI OGGI", style = AppEyebrow, color = AppColor.textSecondary)
                SessionRow(
                    summary = today,
                    dayLabel = "OGGI",
                    modifier = Modifier.padding(top = AppSpacing.sm, bottom = AppSpacing.lg),
                    onClick = { actions.navigate(AppRoute.DettaglioRun(today.sessionId, today.latestRunId)) },
                )
            }

            if (state.previous.isNotEmpty()) {
                Text(text = "SESSIONI PRECEDENTI", style = AppEyebrow, color = AppColor.textSecondary)
                Column(modifier = Modifier.padding(top = AppSpacing.sm)) {
                    state.previous.forEach { summary ->
                        SessionRow(
                            summary = summary,
                            dayLabel = relativeDayLabel(summary.date),
                            modifier = Modifier.padding(bottom = AppSpacing.md),
                            onClick = { actions.navigate(AppRoute.DettaglioRun(summary.sessionId, summary.latestRunId)) },
                        )
                    }
                }
            }

            if (state.today == null && state.previous.isEmpty()) {
                Text(
                    text = "Nessuna sessione registrata",
                    style = AppBody,
                    color = AppColor.textSecondary,
                    modifier = Modifier.padding(top = AppSpacing.lg),
                )
            }

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = AppSpacing.lg, bottom = AppSpacing.xxl)
                    .clip(RoundedCornerShape(AppRadius.button))
                    .border(1.dp, AppColor.textPrimary.copy(alpha = 0.24f), RoundedCornerShape(AppRadius.button))
                    .clickable { actions.navigate(AppRoute.TutteLeSessioni) }
                    .padding(AppSpacing.lg),
                contentAlignment = Alignment.Center,
            ) {
                Text(text = "Vedi tutte le sessioni", style = AppBody, color = AppColor.textPrimary)
            }
        }

        FloatingActionButton(
            onClick = { actions.navigate(AppRoute.NuovaSessione) },
            containerColor = AppColor.accentBlue,
            contentColor = AppColor.textPrimary,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(end = AppSpacing.xl, bottom = AppSpacing.tabBarClearance + AppSpacing.lg),
        ) {
            Icon(Icons.Filled.Add, contentDescription = "Nuova sessione")
        }
    }
}

@Composable
fun SessionRow(summary: SessionSummary, dayLabel: String, modifier: Modifier = Modifier, onClick: () -> Unit) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .appGlass(cornerRadius = AppRadius.card)
            .clickable(onClick = onClick)
            .padding(AppSpacing.lg),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(text = dayLabel, style = AppCaption, color = AppColor.textSecondary)
            Text(text = summary.trackName, style = AppHeadline, color = AppColor.textPrimary, modifier = Modifier.padding(top = AppSpacing.xs))
            Text(
                text = "${summary.bikeName} · ${summary.runCount} run",
                style = AppCaption,
                color = AppColor.textSecondary,
                modifier = Modifier.padding(top = AppSpacing.xs),
            )
            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(top = AppSpacing.xs)) {
                Icon(meteoIcon(summary.weather), contentDescription = null, tint = AppColor.textSecondary, modifier = Modifier.size(14.dp))
                Text(
                    text = summary.weather.label(),
                    style = AppCaption,
                    color = AppColor.textSecondary,
                    modifier = Modifier.padding(start = AppSpacing.xs),
                )
            }
        }
        if (summary.bestLap.isNotBlank()) {
            Column(horizontalAlignment = Alignment.End) {
                Text(text = summary.bestLap, style = AppHeadline, color = AppColor.gold)
                Text(text = "best lap", style = AppCaption, color = AppColor.textSecondary)
            }
        }
    }
}

fun meteoIcon(weather: Meteo): ImageVector = when (weather) {
    Meteo.Sole -> Icons.Filled.WbSunny
    Meteo.Nuvole -> Icons.Filled.Cloud
    Meteo.Pioggia -> Icons.Filled.WaterDrop
}
