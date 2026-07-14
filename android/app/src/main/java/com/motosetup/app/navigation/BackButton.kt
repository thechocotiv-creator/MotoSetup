package com.motosetup.app.navigation

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.motosetup.app.ui.theme.AppColor
import com.motosetup.app.ui.theme.AppHeadline
import com.motosetup.app.ui.theme.AppRadius
import com.motosetup.app.ui.theme.AppSpacing
import com.motosetup.app.ui.theme.appGlass

/** Bottone "Indietro" condiviso dai placeholder delle destinazioni push (Fase 2). */
@Composable
fun BackButton(modifier: Modifier = Modifier) {
    val actions = LocalAppNavActions.current
    Box(
        modifier = modifier
            .fillMaxWidth()
            .appGlass(cornerRadius = AppRadius.button)
            .clickable(onClick = actions.navigateBack)
            .padding(vertical = AppSpacing.md),
        contentAlignment = Alignment.Center,
    ) {
        Text(text = "Indietro", style = AppHeadline, color = AppColor.textPrimary)
    }
}
