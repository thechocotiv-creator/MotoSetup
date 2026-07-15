package com.motosetup.app.feature.sessioni

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.motosetup.app.feature.common.DoubleStepperField
import com.motosetup.app.feature.common.IntStepperField
import com.motosetup.app.feature.onboarding.AuthTextField
import com.motosetup.app.feature.setup.meteoIcon
import com.motosetup.app.model.ForcellaMonoSetup
import com.motosetup.app.model.PneumaticoSetup
import com.motosetup.app.navigation.AppSheet
import com.motosetup.app.navigation.BackButton
import com.motosetup.app.navigation.LocalAppNavActions
import com.motosetup.app.navigation.PaywallReason
import com.motosetup.app.navigation.PickerKind
import com.motosetup.app.ui.theme.AppBody
import com.motosetup.app.ui.theme.AppCaption
import com.motosetup.app.ui.theme.AppColor
import com.motosetup.app.ui.theme.AppEyebrow
import com.motosetup.app.ui.theme.AppHeadline
import com.motosetup.app.ui.theme.AppLargeTitle
import com.motosetup.app.ui.theme.AppRadius
import com.motosetup.app.ui.theme.AppSpacing
import com.motosetup.app.ui.theme.appGlass

private enum class SetupCategory(val label: String) {
    Sospensioni("Sospensioni"),
    Gomme("Gomme"),
    Rapporti("Rapporti"),
    Elettronica("Elettronica"),
}

private enum class SospensioneSub(val label: String, val path: String) {
    Forcella("Forcella", "forcella"),
    Mono("Mono", "mono"),
}

private enum class GommaSub(val label: String, val path: String) {
    Anteriore("Anteriore", "anteriore"),
    Posteriore("Posteriore", "posteriore"),
}

/** Vedi design_handoff_motosetup_app/README.md #4. */
@Composable
fun DettaglioRunScreen(sessionId: String, runId: String) {
    val actions = LocalAppNavActions.current
    val viewModel: DettaglioRunViewModel = hiltViewModel()
    LaunchedEffect(sessionId, runId) { viewModel.load(sessionId, runId) }
    val state by viewModel.uiState.collectAsState()
    val run = state.selectedRun

    var category by remember { mutableStateOf(SetupCategory.Sospensioni) }
    var sospensioneSub by remember { mutableStateOf(SospensioneSub.Forcella) }
    var gommaSub by remember { mutableStateOf(GommaSub.Anteriore) }

    Column(modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(AppSpacing.xl)) {
        Row(
            modifier = Modifier.fillMaxWidth().clickable { viewModel.cycleWeather() },
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Icon(meteoIcon(state.weather), contentDescription = null, tint = AppColor.textSecondary, modifier = Modifier.padding(end = AppSpacing.xs))
            Text(text = state.weather.label(), style = AppCaption, color = AppColor.textSecondary)
        }
        Text(
            text = state.trackName,
            style = AppLargeTitle,
            color = AppColor.textPrimary,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth().padding(top = AppSpacing.xs, bottom = AppSpacing.lg),
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Icon(
                Icons.AutoMirrored.Filled.KeyboardArrowLeft,
                contentDescription = "Run precedente",
                tint = AppColor.textSecondary,
                modifier = Modifier.clickable(onClick = viewModel::selectPreviousRun),
            )
            Text(
                text = "RUN ${state.selectedRunPosition}",
                style = AppHeadline,
                color = AppColor.textPrimary,
                modifier = Modifier.padding(horizontal = AppSpacing.lg),
            )
            Icon(
                Icons.AutoMirrored.Filled.KeyboardArrowRight,
                contentDescription = "Run successivo / nuovo run",
                tint = AppColor.textSecondary,
                modifier = Modifier.clickable {
                    val atLast = state.selectedRunPosition == state.runs.size
                    if (atLast) {
                        if (state.canAddRun) viewModel.addRun() else actions.showPaywall(PaywallReason.RunLimit)
                    } else {
                        viewModel.selectNextRun()
                    }
                },
            )
        }

        Row(modifier = Modifier.fillMaxWidth().padding(top = AppSpacing.lg), horizontalArrangement = Arrangement.spacedBy(AppSpacing.md)) {
            StatBox(
                label = "ORA",
                value = run?.time.orEmpty(),
                modifier = Modifier.weight(1f),
                onClick = { actions.openSheet(AppSheet.Picker(PickerKind.Ora, sessionId, state.selectedRunId)) },
            )
            StatBox(
                label = "TEMPERATURA",
                value = run?.let { "${it.temperature}°C" }.orEmpty(),
                modifier = Modifier.weight(1f),
                onClick = { actions.openSheet(AppSheet.Picker(PickerKind.Temperatura, sessionId, state.selectedRunId)) },
            )
        }
        Row(modifier = Modifier.fillMaxWidth().padding(top = AppSpacing.md), horizontalArrangement = Arrangement.spacedBy(AppSpacing.md)) {
            StatBox(
                label = "BEST LAP",
                value = run?.bestLap.orEmpty(),
                valueColor = AppColor.gold,
                modifier = Modifier.weight(1f),
                onClick = { actions.openSheet(AppSheet.Picker(PickerKind.BestLap, sessionId, state.selectedRunId)) },
            )
            StatBox(
                label = "GIRI",
                value = run?.laps?.toString().orEmpty(),
                modifier = Modifier.weight(1f),
                onClick = { actions.openSheet(AppSheet.Picker(PickerKind.Giri, sessionId, state.selectedRunId)) },
            )
        }

        Row(
            modifier = Modifier.fillMaxWidth().padding(top = AppSpacing.xl).appGlass(cornerRadius = 20.dp).padding(4.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
        ) {
            SetupCategory.entries.forEach { entry ->
                CategoryPill(text = entry.label, isSelected = entry == category, onClick = { category = entry }, modifier = Modifier.weight(1f))
            }
        }

        when (category) {
            SetupCategory.Sospensioni -> {
                Row(modifier = Modifier.fillMaxWidth().padding(top = AppSpacing.md), horizontalArrangement = Arrangement.spacedBy(AppSpacing.sm)) {
                    SospensioneSub.entries.forEach { entry ->
                        SubTabPill(text = entry.label, isSelected = entry == sospensioneSub, onClick = { sospensioneSub = entry })
                    }
                }
            }
            SetupCategory.Gomme -> {
                Row(modifier = Modifier.fillMaxWidth().padding(top = AppSpacing.md), horizontalArrangement = Arrangement.spacedBy(AppSpacing.sm)) {
                    GommaSub.entries.forEach { entry ->
                        SubTabPill(text = entry.label, isSelected = entry == gommaSub, onClick = { gommaSub = entry })
                    }
                }
            }
            else -> Unit
        }

        if (run != null) {
            Column(modifier = Modifier.padding(top = AppSpacing.lg)) {
                when (category) {
                    SetupCategory.Sospensioni -> {
                        val sub = if (sospensioneSub == SospensioneSub.Forcella) run.sospensione.forcella else run.sospensione.mono
                        val prefix = "sospensione.${sospensioneSub.path}"
                        ForcellaMonoFields(sub, prefix, viewModel::updateField)
                    }
                    SetupCategory.Gomme -> {
                        val sub = if (gommaSub == GommaSub.Anteriore) run.gomme.anteriore else run.gomme.posteriore
                        val prefix = "gomme.${gommaSub.path}"
                        PneumaticoFields(sub, prefix, viewModel::updateField)
                    }
                    SetupCategory.Rapporti -> {
                        IntStepperField(label = "Pignone", value = run.rapporti.pignone, onValueChange = { viewModel.updateField("rapporti.pignone", it) })
                        IntStepperField(label = "Corona", value = run.rapporti.corona, onValueChange = { viewModel.updateField("rapporti.corona", it) })
                        SetupTextField(label = "Passo catena", value = run.rapporti.passoCatena, onValueChange = { viewModel.updateField("rapporti.passoCatena", it) })
                        NoteField(value = run.rapporti.note, onValueChange = { viewModel.updateField("rapporti.note", it) })
                    }
                    SetupCategory.Elettronica -> {
                        SetupTextField(label = "Mappa", value = run.elettronica.mappa, onValueChange = { viewModel.updateField("elettronica.mappa", it) })
                        IntStepperField(label = "TC", value = run.elettronica.tc, onValueChange = { viewModel.updateField("elettronica.tc", it) })
                        IntStepperField(label = "Engine Brake", value = run.elettronica.engineBrake, onValueChange = { viewModel.updateField("elettronica.engineBrake", it) })
                        IntStepperField(label = "Anti Wheelie", value = run.elettronica.antiWheelie, onValueChange = { viewModel.updateField("elettronica.antiWheelie", it) })
                        NoteField(value = run.elettronica.note, onValueChange = { viewModel.updateField("elettronica.note", it) })
                    }
                }
            }
        }

        BackButton(modifier = Modifier.padding(top = AppSpacing.xl))
    }
}

@Composable
private fun ForcellaMonoFields(sub: ForcellaMonoSetup, prefix: String, onUpdate: (String, Any) -> Unit) {
    SetupTextField(label = "Molla", value = sub.molla, onValueChange = { onUpdate("$prefix.molla", it) })
    IntStepperField(label = "Altezza", value = sub.altezza, onValueChange = { onUpdate("$prefix.altezza", it) })
    IntStepperField(label = "Compressione", value = sub.compressione, onValueChange = { onUpdate("$prefix.compressione", it) })
    IntStepperField(label = "Estensione", value = sub.estensione, onValueChange = { onUpdate("$prefix.estensione", it) })
    IntStepperField(label = "Precarico", value = sub.precarico, onValueChange = { onUpdate("$prefix.precarico", it) })
    NoteField(value = sub.note, onValueChange = { onUpdate("$prefix.note", it) })
}

@Composable
private fun PneumaticoFields(sub: PneumaticoSetup, prefix: String, onUpdate: (String, Any) -> Unit) {
    IntStepperField(label = "Giri", value = sub.giri, onValueChange = { onUpdate("$prefix.giri", it) })
    DoubleStepperField(label = "Pressione ingresso", value = sub.pressioneIngresso, onValueChange = { onUpdate("$prefix.pressioneIngresso", it) })
    DoubleStepperField(label = "Pressione uscita", value = sub.pressioneUscita, onValueChange = { onUpdate("$prefix.pressioneUscita", it) })
    NoteField(value = sub.note, onValueChange = { onUpdate("$prefix.note", it) })
}

@Composable
private fun SetupTextField(label: String, value: String, onValueChange: (String) -> Unit) {
    AuthTextField(value = value, onValueChange = onValueChange, label = label, modifier = Modifier.padding(bottom = AppSpacing.sm))
}

@Composable
private fun NoteField(value: String, onValueChange: (String) -> Unit) {
    AuthTextField(
        value = value,
        onValueChange = onValueChange,
        label = "Note",
        singleLine = false,
        modifier = Modifier.padding(top = AppSpacing.xs, bottom = AppSpacing.lg),
    )
}

@Composable
private fun StatBox(label: String, value: String, modifier: Modifier = Modifier, valueColor: Color = AppColor.textPrimary, onClick: () -> Unit) {
    Column(
        modifier = modifier.appGlass(cornerRadius = AppRadius.card).clickable(onClick = onClick).padding(AppSpacing.md),
    ) {
        Text(text = label, style = AppEyebrow, color = AppColor.textSecondary)
        Text(text = value, style = AppHeadline, color = valueColor, modifier = Modifier.padding(top = AppSpacing.xs))
    }
}

@Composable
private fun CategoryPill(text: String, isSelected: Boolean, onClick: () -> Unit, modifier: Modifier = Modifier) {
    val contentColor by animateColorAsState(
        targetValue = if (isSelected) AppColor.textPrimary else AppColor.textSecondary,
        animationSpec = spring(stiffness = Spring.StiffnessMedium),
        label = "categoryContentColor",
    )
    val pillColor by animateColorAsState(
        targetValue = if (isSelected) AppColor.accentBlue.copy(alpha = 0.25f) else Color.Transparent,
        animationSpec = spring(stiffness = Spring.StiffnessMedium),
        label = "categoryPillColor",
    )
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .background(pillColor)
            .clickable(onClick = onClick)
            .padding(vertical = AppSpacing.sm),
        contentAlignment = Alignment.Center,
    ) {
        Text(text = text, style = AppCaption, color = contentColor)
    }
}

@Composable
private fun SubTabPill(text: String, isSelected: Boolean, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(AppRadius.button))
            .background(if (isSelected) AppColor.textPrimary.copy(alpha = 0.12f) else Color.Transparent)
            .clickable(onClick = onClick)
            .padding(horizontal = AppSpacing.md, vertical = AppSpacing.sm),
    ) {
        Text(text = text, style = AppBody, color = if (isSelected) AppColor.textPrimary else AppColor.textSecondary)
    }
}
