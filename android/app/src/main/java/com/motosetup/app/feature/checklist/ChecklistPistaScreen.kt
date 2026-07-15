package com.motosetup.app.feature.checklist

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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
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
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.motosetup.app.feature.common.TextPromptDialog
import com.motosetup.app.model.ChecklistItem
import com.motosetup.app.navigation.BackButton
import com.motosetup.app.ui.theme.AppBody
import com.motosetup.app.ui.theme.AppColor
import com.motosetup.app.ui.theme.AppLargeTitle
import com.motosetup.app.ui.theme.AppRadius
import com.motosetup.app.ui.theme.AppSpacing

/** Vedi design_handoff_motosetup_app/README.md #2a. */
@Composable
fun ChecklistPistaScreen() {
    val viewModel: ChecklistViewModel = hiltViewModel()
    val items by viewModel.items.collectAsState()
    var showAddPrompt by remember { mutableStateOf(false) }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(modifier = Modifier.fillMaxSize().padding(AppSpacing.xl)) {
            Text(text = "Checklist pista", style = AppLargeTitle, color = AppColor.textPrimary)

            LazyColumn(modifier = Modifier.weight(1f).padding(top = AppSpacing.lg)) {
                items(items, key = { it.id }) { item ->
                    ChecklistRow(item = item, onToggle = { viewModel.toggle(item) }, onDelete = { viewModel.delete(item) })
                }
            }

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(AppRadius.card))
                    .border(1.dp, AppColor.textPrimary.copy(alpha = 0.28f), RoundedCornerShape(AppRadius.card))
                    .background(AppColor.textPrimary.copy(alpha = 0.1f))
                    .clickable { showAddPrompt = true }
                    .padding(AppSpacing.lg),
                contentAlignment = Alignment.Center,
            ) {
                Text(text = "+ Aggiungi voce", style = AppBody, color = AppColor.textPrimary)
            }

            BackButton(modifier = Modifier.padding(top = AppSpacing.lg))
        }

        if (showAddPrompt) {
            TextPromptDialog(
                title = "Nuova voce checklist",
                label = "Voce",
                confirmLabel = "Aggiungi",
                onConfirm = { label ->
                    viewModel.addItem(label)
                    showAddPrompt = false
                },
                onDismiss = { showAddPrompt = false },
            )
        }
    }
}

@Composable
private fun ChecklistRow(item: ChecklistItem, onToggle: () -> Unit, onDelete: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = AppSpacing.sm),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(AppSpacing.md),
    ) {
        Box(
            modifier = Modifier
                .size(24.dp)
                .clip(RoundedCornerShape(9.dp))
                .border(
                    width = 1.5.dp,
                    color = if (item.done) AppColor.accentBlue else AppColor.textSecondary.copy(alpha = 0.4f),
                    shape = RoundedCornerShape(9.dp),
                )
                .background(if (item.done) AppColor.accentBlue.copy(alpha = 0.3f) else AppColor.panel)
                .clickable(onClick = onToggle),
            contentAlignment = Alignment.Center,
        ) {
            if (item.done) {
                Icon(Icons.Filled.Check, contentDescription = null, tint = AppColor.textPrimary, modifier = Modifier.size(14.dp))
            }
        }
        Text(
            text = item.label,
            style = AppBody,
            color = if (item.done) AppColor.textSecondary else AppColor.textPrimary,
            textDecoration = if (item.done) TextDecoration.LineThrough else TextDecoration.None,
            modifier = Modifier.weight(1f).clickable(onClick = onToggle),
        )
        Box(
            modifier = Modifier
                .size(28.dp)
                .clip(CircleShape)
                .clickable(onClick = onDelete),
            contentAlignment = Alignment.Center,
        ) {
            Icon(Icons.Filled.Close, contentDescription = "Elimina voce", tint = AppColor.textSecondary, modifier = Modifier.size(13.dp))
        }
    }
}
