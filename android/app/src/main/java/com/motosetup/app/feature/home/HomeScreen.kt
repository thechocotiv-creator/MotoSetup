package com.motosetup.app.feature.home

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.Add
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.motosetup.app.feature.common.TextPromptDialog
import com.motosetup.app.navigation.AppDialog
import com.motosetup.app.navigation.AppRoute
import com.motosetup.app.navigation.AppSheet
import com.motosetup.app.navigation.LocalAppNavActions
import com.motosetup.app.navigation.PaywallReason
import com.motosetup.app.ui.theme.AppBody
import com.motosetup.app.ui.theme.AppCaption
import com.motosetup.app.ui.theme.AppColor
import com.motosetup.app.ui.theme.AppEyebrow
import com.motosetup.app.ui.theme.AppHeadline
import com.motosetup.app.ui.theme.AppLargeTitle
import com.motosetup.app.ui.theme.AppRadius
import com.motosetup.app.ui.theme.AppSpacing
import com.motosetup.app.ui.theme.appGlass

/** Vedi design_handoff_motosetup_app/README.md #2. "Ultima sessione" resta uno stub finché il model Session non arriva in Fase 4. */
@Composable
fun HomeScreen() {
    val actions = LocalAppNavActions.current
    val viewModel: HomeViewModel = hiltViewModel()
    val state by viewModel.uiState.collectAsState()
    var showAddBikePrompt by remember { mutableStateOf(false) }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState())) {
            Text(
                text = "Home",
                style = AppLargeTitle,
                color = AppColor.textPrimary,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth().padding(top = AppSpacing.xxl, start = AppSpacing.xl, end = AppSpacing.xl),
            )

            Column(modifier = Modifier.fillMaxWidth().padding(horizontal = AppSpacing.xl, vertical = AppSpacing.lg)) {
                Text(text = "ULTIMA SESSIONE", style = AppEyebrow, color = AppColor.textSecondary)
                Text(
                    text = "Nessuna sessione registrata",
                    style = AppBody,
                    color = AppColor.textSecondary,
                    modifier = Modifier.padding(top = AppSpacing.xs),
                )
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = AppSpacing.xl)
                    .appGlass(cornerRadius = 18.dp)
                    .clickable { actions.navigate(AppRoute.ChecklistPista) }
                    .padding(horizontal = AppSpacing.lg, vertical = AppSpacing.md),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(AppSpacing.sm)) {
                    Icon(Icons.AutoMirrored.Filled.List, contentDescription = null, tint = AppColor.textSecondary)
                    Text(text = "Checklist pista", style = AppHeadline, color = AppColor.textPrimary)
                }
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(AppSpacing.xs)) {
                    Text(text = "${state.checklistDone}/${state.checklistTotal}", style = AppBody, color = AppColor.textPrimary)
                    Icon(Icons.AutoMirrored.Filled.KeyboardArrowRight, contentDescription = null, tint = AppColor.textSecondary)
                }
            }

            LazyRow(
                modifier = Modifier.fillMaxWidth().padding(top = AppSpacing.lg, bottom = AppSpacing.xxl),
                contentPadding = PaddingValues(horizontal = AppSpacing.xl),
                horizontalArrangement = Arrangement.spacedBy(AppSpacing.md),
            ) {
                items(state.bikes, key = { it.id }) { bike ->
                    BikeCard(
                        bike = bike,
                        maintenanceItems = state.maintenanceByBike[bike.id].orEmpty(),
                        onClick = { actions.navigate(AppRoute.ManutenzioneMoto(bike.id)) },
                        onEdit = { actions.openSheet(AppSheet.ModificaMoto(bike.id)) },
                        onDelete = { actions.openDialog(AppDialog.EliminaMoto(bike.id)) },
                    )
                }
                item {
                    AddBikeCard(
                        captionText = if (state.isPremium) "Illimitate · Premium" else "${state.bikes.size}/1 · piano Free",
                        onClick = {
                            if (state.canAddBike) {
                                showAddBikePrompt = true
                            } else {
                                actions.showPaywall(PaywallReason.BikeLimit)
                            }
                        },
                    )
                }
            }
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
    }
}

@Composable
private fun AddBikeCard(captionText: String, onClick: () -> Unit) {
    Column(
        modifier = Modifier
            .width(260.dp)
            .clip(RoundedCornerShape(AppRadius.card))
            .background(AppColor.panel.copy(alpha = 0.4f))
            .clickable(onClick = onClick)
            .padding(AppSpacing.xl),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Box(
            modifier = Modifier
                .size(42.dp)
                .clip(RoundedCornerShape(50))
                .background(AppColor.textPrimary.copy(alpha = 0.08f)),
            contentAlignment = Alignment.Center,
        ) {
            Icon(Icons.Filled.Add, contentDescription = null, tint = AppColor.textPrimary)
        }
        Text(
            text = "Aggiungi nuova moto",
            style = AppHeadline,
            color = AppColor.textPrimary,
            modifier = Modifier.padding(top = AppSpacing.sm),
        )
        Text(text = captionText, style = AppCaption, color = AppColor.textSecondary, modifier = Modifier.padding(top = AppSpacing.xs))
    }
}
