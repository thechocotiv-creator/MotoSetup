package com.motosetup.app.feature.manutenzione

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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.motosetup.app.feature.common.NewMaintenanceItemPromptDialog
import com.motosetup.app.feature.common.NumberPromptDialog
import com.motosetup.app.model.MaintenanceItem
import com.motosetup.app.navigation.BackButton
import com.motosetup.app.ui.theme.AppBody
import com.motosetup.app.ui.theme.AppCaption
import com.motosetup.app.ui.theme.AppColor
import com.motosetup.app.ui.theme.AppLargeTitle
import com.motosetup.app.ui.theme.AppRadius
import com.motosetup.app.ui.theme.AppSpacing
import com.motosetup.app.ui.theme.appGlass

/** Vedi design_handoff_motosetup_app/README.md #2b. */
@Composable
fun ManutenzioneMotoScreen(bikeId: String) {
    val viewModel: ManutenzioneViewModel = hiltViewModel()
    LaunchedEffect(bikeId) { viewModel.load(bikeId) }
    val state by viewModel.uiState.collectAsState()

    var editingIntervalFor by remember { mutableStateOf<MaintenanceItem?>(null) }
    var markingPastFor by remember { mutableStateOf<MaintenanceItem?>(null) }
    var showAddItemPrompt by remember { mutableStateOf(false) }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(modifier = Modifier.fillMaxSize().padding(AppSpacing.xl)) {
            Text(text = "Manutenzione", style = AppLargeTitle, color = AppColor.textPrimary, textAlign = TextAlign.Center, modifier = Modifier.fillMaxWidth())
            Text(
                text = state.bikeName,
                style = AppBody,
                color = AppColor.textSecondary,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth().padding(top = AppSpacing.xs, bottom = AppSpacing.lg),
            )

            LazyColumn(modifier = Modifier.weight(1f)) {
                items(state.items, key = { it.id }) { item ->
                    MaintenanceRow(
                        item = item,
                        onEditInterval = { editingIntervalFor = item },
                        onMarkDoneToday = { viewModel.markDoneToday(item) },
                        onMarkDonePast = { markingPastFor = item },
                    )
                }
            }

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(AppRadius.card))
                    .border(1.dp, AppColor.textSecondary.copy(alpha = 0.2f), RoundedCornerShape(AppRadius.card))
                    .background(AppColor.background.copy(alpha = 0.2f))
                    .clickable { showAddItemPrompt = true }
                    .padding(AppSpacing.lg),
                contentAlignment = Alignment.Center,
            ) {
                Text(text = "+ Nuova voce", style = AppBody, color = AppColor.textSecondary)
            }

            BackButton(modifier = Modifier.padding(top = AppSpacing.lg))
        }

        editingIntervalFor?.let { item ->
            NumberPromptDialog(
                title = "Modifica intervallo",
                label = "Ogni quanti giorni va eseguita questa manutenzione?",
                initialValue = item.intervalDays,
                onConfirm = { days ->
                    viewModel.updateInterval(item, days)
                    editingIntervalFor = null
                },
                onDismiss = { editingIntervalFor = null },
            )
        }

        markingPastFor?.let { item ->
            NumberPromptDialog(
                title = "Data precedente",
                label = "Eseguita quanti giorni fa?",
                initialValue = 1,
                onConfirm = { daysAgo ->
                    viewModel.markDonePast(item, daysAgo)
                    markingPastFor = null
                },
                onDismiss = { markingPastFor = null },
            )
        }

        if (showAddItemPrompt) {
            NewMaintenanceItemPromptDialog(
                onConfirm = { name, intervalDays ->
                    viewModel.addItem(name, intervalDays)
                    showAddItemPrompt = false
                },
                onDismiss = { showAddItemPrompt = false },
            )
        }
    }
}

@Composable
private fun MaintenanceRow(
    item: MaintenanceItem,
    onEditInterval: () -> Unit,
    onMarkDoneToday: () -> Unit,
    onMarkDonePast: () -> Unit,
) {
    val status = maintenanceStatus(item.daysSinceService, item.intervalDays)
    Column(modifier = Modifier.fillMaxWidth().padding(vertical = AppSpacing.md)) {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text(text = item.name, style = AppBody, color = AppColor.textPrimary)
            Text(text = status.label().uppercase(), style = AppCaption, color = AppColor.status(status))
        }
        Row(modifier = Modifier.padding(top = AppSpacing.xs, bottom = AppSpacing.md)) {
            Text(text = "Ultimo: ${item.daysSinceService} giorni fa · ", style = AppCaption, color = AppColor.textSecondary)
            Text(
                text = "ogni ${item.intervalDays} giorni",
                style = AppCaption,
                color = AppColor.accentBlue,
                textDecoration = TextDecoration.Underline,
                modifier = Modifier.clickable(onClick = onEditInterval),
            )
        }
        Row(horizontalArrangement = Arrangement.spacedBy(AppSpacing.sm)) {
            MaintenanceActionButton(text = "Eseguito oggi", onClick = onMarkDoneToday, modifier = Modifier.weight(1f))
            MaintenanceActionButton(text = "Data precedente", onClick = onMarkDonePast, modifier = Modifier.weight(1f))
        }
    }
}

@Composable
private fun MaintenanceActionButton(text: String, onClick: () -> Unit, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .appGlass(cornerRadius = AppRadius.button)
            .clickable(onClick = onClick)
            .padding(vertical = AppSpacing.sm),
        contentAlignment = Alignment.Center,
    ) {
        Text(text = text, style = AppCaption, color = AppColor.textPrimary)
    }
}
