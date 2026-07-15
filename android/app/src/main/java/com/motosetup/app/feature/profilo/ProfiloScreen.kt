package com.motosetup.app.feature.profilo

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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.motosetup.app.navigation.AppDialog
import com.motosetup.app.navigation.AppSheet
import com.motosetup.app.navigation.LocalAppNavActions
import com.motosetup.app.ui.theme.AppCaption
import com.motosetup.app.ui.theme.AppColor
import com.motosetup.app.ui.theme.AppEyebrow
import com.motosetup.app.ui.theme.AppHeadline
import com.motosetup.app.ui.theme.AppLargeTitle
import com.motosetup.app.ui.theme.AppSpacing
import com.motosetup.app.ui.theme.AppTitle

/** Vedi design_handoff_motosetup_app/README.md #6. */
@Composable
fun ProfiloScreen() {
    val actions = LocalAppNavActions.current
    val viewModel: ProfiloViewModel = hiltViewModel()
    val state by viewModel.uiState.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = AppSpacing.xl),
    ) {
        Text(
            text = "Profilo",
            style = AppLargeTitle,
            color = AppColor.textPrimary,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth().padding(top = AppSpacing.xxl, bottom = AppSpacing.xl),
        )

        Column(modifier = Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
            Box(
                modifier = Modifier
                    .size(88.dp)
                    .clip(CircleShape)
                    .background(AppColor.panel)
                    .border(2.dp, AppColor.textPrimary.copy(alpha = 0.16f), CircleShape),
                contentAlignment = Alignment.Center,
            ) {
                Icon(Icons.Filled.Person, contentDescription = null, tint = AppColor.textSecondary, modifier = Modifier.size(40.dp))
            }
            Text(
                text = state.nickname,
                style = AppTitle,
                color = AppColor.textPrimary,
                modifier = Modifier.padding(top = AppSpacing.md),
            )
            Text(text = state.email, style = AppCaption, color = AppColor.textSecondary)
        }

        SectionLabel(text = "Account", topPadding = AppSpacing.xxl)
        ProfileRow(
            label = "Nickname ed email",
            value = state.nickname,
            onClick = { actions.openSheet(AppSheet.ModificaProfilo) },
        )
        ProfileRow(
            label = "Password",
            value = "••••••••",
            onClick = { actions.openSheet(AppSheet.ModificaPassword) },
        )

        SectionLabel(text = "Abbonamento", topPadding = AppSpacing.xl)
        PlanCard(isPremium = state.isPremium, onClick = { actions.openSheet(AppSheet.Abbonamento) })

        SectionLabel(text = "Sessione", topPadding = AppSpacing.xl)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable(onClick = viewModel::logout)
                .padding(vertical = AppSpacing.md),
        ) {
            Text(text = "Esci", style = AppHeadline, color = AppColor.textPrimary)
        }
        Box(modifier = Modifier.fillMaxWidth().height(1.dp).background(AppColor.textSecondary.copy(alpha = 0.15f)))
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { actions.openDialog(AppDialog.EliminaAccount) }
                .padding(vertical = AppSpacing.md),
        ) {
            Text(text = "Elimina account", style = AppHeadline, color = AppColor.red)
        }

        Box(modifier = Modifier.fillMaxWidth().height(AppSpacing.tabBarClearance))
    }
}

@Composable
private fun SectionLabel(text: String, topPadding: Dp) {
    Text(
        text = text.uppercase(),
        style = AppEyebrow,
        color = AppColor.textSecondary,
        modifier = Modifier.padding(top = topPadding, bottom = AppSpacing.xs),
    )
}

@Composable
private fun ProfileRow(label: String, value: String, onClick: () -> Unit) {
    Column {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable(onClick = onClick)
                .padding(vertical = AppSpacing.md),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(text = label, style = AppHeadline, color = AppColor.textPrimary)
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(AppSpacing.xs)) {
                Text(text = value, style = AppCaption, color = AppColor.textSecondary)
                Icon(Icons.AutoMirrored.Filled.KeyboardArrowRight, contentDescription = null, tint = AppColor.textSecondary)
            }
        }
        Box(modifier = Modifier.fillMaxWidth().height(1.dp).background(AppColor.textSecondary.copy(alpha = 0.15f)))
    }
}

@Composable
private fun PlanCard(isPremium: Boolean, onClick: () -> Unit) {
    val borderColor = if (isPremium) AppColor.accentBlue.copy(alpha = 0.45f) else AppColor.textPrimary.copy(alpha = 0.16f)
    val backgroundColor = if (isPremium) AppColor.accentBlue.copy(alpha = 0.08f) else AppColor.textPrimary.copy(alpha = 0.05f)
    val badgeFg = if (isPremium) AppColor.background else AppColor.textPrimary
    val badgeBg = if (isPremium) AppColor.accentBlue else AppColor.textPrimary.copy(alpha = 0.14f)

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
            .background(backgroundColor)
            .border(1.dp, borderColor, RoundedCornerShape(18.dp))
            .clickable(onClick = onClick)
            .padding(AppSpacing.lg),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(text = "Piano ${if (isPremium) "Premium" else "Free"}", style = AppHeadline, color = AppColor.textPrimary)
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(6.dp))
                    .background(badgeBg)
                    .padding(horizontal = AppSpacing.sm, vertical = 5.dp),
            ) {
                Text(text = if (isPremium) "Sbloccato" else "Gratuito", style = AppEyebrow, color = badgeFg)
            }
        }
        Text(
            text = if (isPremium) {
                "Pagamento unico effettuato · nessun rinnovo"
            } else {
                "Sblocca moto, consigli AI e run illimitati con un pagamento unico di €9,99"
            },
            style = AppCaption,
            color = AppColor.textSecondary,
            modifier = Modifier.padding(top = AppSpacing.xs),
        )
    }
}
