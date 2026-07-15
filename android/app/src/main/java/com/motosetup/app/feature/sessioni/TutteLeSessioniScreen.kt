package com.motosetup.app.feature.sessioni

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.hilt.navigation.compose.hiltViewModel
import com.motosetup.app.feature.setup.SessionRow
import com.motosetup.app.feature.setup.relativeDayLabel
import com.motosetup.app.navigation.AppRoute
import com.motosetup.app.navigation.BackButton
import com.motosetup.app.navigation.LocalAppNavActions
import com.motosetup.app.ui.theme.AppBody
import com.motosetup.app.ui.theme.AppColor
import com.motosetup.app.ui.theme.AppLargeTitle
import com.motosetup.app.ui.theme.AppRadius
import com.motosetup.app.ui.theme.AppSpacing
import com.motosetup.app.ui.theme.appGlass

/** Vedi design_handoff_motosetup_app/README.md #3b. */
@Composable
fun TutteLeSessioniScreen() {
    val actions = LocalAppNavActions.current
    val viewModel: TutteLeSessioniViewModel = hiltViewModel()
    val state by viewModel.uiState.collectAsState()

    Column(modifier = Modifier.fillMaxSize().padding(AppSpacing.xl)) {
        Text(
            text = "Tutte le sessioni",
            style = AppLargeTitle,
            color = AppColor.textPrimary,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth(),
        )

        Row(
            modifier = Modifier.fillMaxWidth().padding(top = AppSpacing.lg),
            horizontalArrangement = Arrangement.spacedBy(AppSpacing.sm),
        ) {
            FilterChip(label = "Moto", value = state.selectedBike ?: "Tutte", onClick = viewModel::cycleBikeFilter, modifier = Modifier.weight(1f))
            FilterChip(label = "Pista", value = state.selectedTrack ?: "Tutte", onClick = viewModel::cycleTrackFilter, modifier = Modifier.weight(1f))
            FilterChip(label = "Data", value = state.selectedDay ?: "Tutte", onClick = viewModel::cycleDayFilter, modifier = Modifier.weight(1f))
        }

        LazyColumn(modifier = Modifier.weight(1f).padding(top = AppSpacing.lg)) {
            items(state.filtered, key = { it.sessionId }) { summary ->
                SessionRow(
                    summary = summary,
                    dayLabel = relativeDayLabel(summary.date),
                    modifier = Modifier.padding(bottom = AppSpacing.md),
                    onClick = { actions.navigate(AppRoute.DettaglioRun(summary.sessionId, summary.latestRunId)) },
                )
            }
        }

        BackButton()
    }
}

@Composable
private fun FilterChip(label: String, value: String, onClick: () -> Unit, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .appGlass(cornerRadius = AppRadius.button)
            .clickable(onClick = onClick)
            .padding(vertical = AppSpacing.sm),
    ) {
        Text(text = "$label $value", style = AppBody, color = AppColor.textPrimary, textAlign = TextAlign.Center, modifier = Modifier.fillMaxWidth())
    }
}
