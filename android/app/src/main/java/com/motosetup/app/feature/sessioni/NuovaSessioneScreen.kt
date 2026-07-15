package com.motosetup.app.feature.sessioni

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.motosetup.app.feature.common.TextPromptDialog
import com.motosetup.app.feature.onboarding.PrimaryButton
import com.motosetup.app.feature.setup.meteoIcon
import com.motosetup.app.navigation.AppRoute
import com.motosetup.app.navigation.LocalAppNavActions
import com.motosetup.app.navigation.PaywallReason
import com.motosetup.app.ui.theme.AppCaption
import com.motosetup.app.ui.theme.AppColor
import com.motosetup.app.ui.theme.AppEyebrow
import com.motosetup.app.ui.theme.AppLargeTitle
import com.motosetup.app.ui.theme.AppSpacing

/** Vedi design_handoff_motosetup_app/README.md #3a. */
@Composable
fun NuovaSessioneScreen() {
    val actions = LocalAppNavActions.current
    val viewModel: NuovaSessioneViewModel = hiltViewModel()
    val state by viewModel.uiState.collectAsState()
    var showAddBikePrompt by remember { mutableStateOf(false) }
    var showAddTrackPrompt by remember { mutableStateOf(false) }

    Column(modifier = Modifier.fillMaxSize().padding(AppSpacing.xl)) {
        Text(text = "Nuova sessione", style = AppLargeTitle, color = AppColor.textPrimary, modifier = Modifier.padding(top = AppSpacing.lg))

        CarouselSelector(
            label = "MOTO",
            valueLabel = state.selectedBikeLabel,
            modifier = Modifier.padding(top = AppSpacing.xxl),
            onPrevious = viewModel::selectPreviousBike,
            onNext = {
                val atLastRealBike = state.selectedBikeIndex == state.bikes.size - 1
                if (atLastRealBike && !state.canAddBike) actions.showPaywall(PaywallReason.BikeLimit) else viewModel.selectNextBike()
            },
            onValueClick = { if (state.isAddBikeSelected) showAddBikePrompt = true },
        )

        CarouselSelector(
            label = "PISTA",
            valueLabel = state.selectedTrackLabel,
            modifier = Modifier.padding(top = AppSpacing.xxl),
            onPrevious = viewModel::selectPreviousTrack,
            onNext = viewModel::selectNextTrack,
            onValueClick = { if (state.isAddTrackSelected) showAddTrackPrompt = true },
        )

        Column(
            modifier = Modifier.fillMaxWidth().padding(top = AppSpacing.xxl).clickable { viewModel.cycleWeather() },
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(text = "METEO", style = AppEyebrow, color = AppColor.textSecondary)
            Icon(
                meteoIcon(state.weather),
                contentDescription = state.weather.label(),
                tint = AppColor.accentBlue,
                modifier = Modifier.size(48.dp).padding(top = AppSpacing.md),
            )
            Text(text = state.weather.label(), style = AppCaption, color = AppColor.textSecondary, modifier = Modifier.padding(top = AppSpacing.xs))
        }

        Box(modifier = Modifier.weight(1f))

        PrimaryButton(
            text = "Inizia sessione",
            enabled = state.canStartSession,
            onClick = {
                viewModel.createSession { sessionId, runId ->
                    actions.navigateBack()
                    actions.navigate(AppRoute.DettaglioRun(sessionId, runId))
                }
            },
        )
    }

    if (showAddBikePrompt) {
        TextPromptDialog(
            title = "Nuova moto",
            label = "Nome moto",
            confirmLabel = "Aggiungi",
            onConfirm = { name ->
                viewModel.addBike(name)
                showAddBikePrompt = false
            },
            onDismiss = { showAddBikePrompt = false },
        )
    }

    if (showAddTrackPrompt) {
        TextPromptDialog(
            title = "Nuova pista",
            label = "Nome pista",
            confirmLabel = "Aggiungi",
            onConfirm = { name ->
                viewModel.addTrack(name)
                showAddTrackPrompt = false
            },
            onDismiss = { showAddTrackPrompt = false },
        )
    }
}

@Composable
private fun CarouselSelector(
    label: String,
    valueLabel: String,
    modifier: Modifier = Modifier,
    onPrevious: () -> Unit,
    onNext: () -> Unit,
    onValueClick: () -> Unit,
) {
    Column(modifier = modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
        Text(text = label, style = AppEyebrow, color = AppColor.textSecondary)
        Row(
            modifier = Modifier.fillMaxWidth().padding(top = AppSpacing.sm),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Icon(
                Icons.AutoMirrored.Filled.KeyboardArrowLeft,
                contentDescription = "Precedente",
                tint = AppColor.textSecondary,
                modifier = Modifier.clickable(onClick = onPrevious),
            )
            Text(
                text = valueLabel.uppercase(),
                style = AppLargeTitle,
                color = AppColor.textPrimary,
                textAlign = TextAlign.Center,
                modifier = Modifier.weight(1f).padding(horizontal = AppSpacing.sm).clickable(onClick = onValueClick),
            )
            Icon(
                Icons.AutoMirrored.Filled.KeyboardArrowRight,
                contentDescription = "Successivo",
                tint = AppColor.textSecondary,
                modifier = Modifier.clickable(onClick = onNext),
            )
        }
    }
}
