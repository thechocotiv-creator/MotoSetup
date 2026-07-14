package com.motosetup.app.navigation

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.motosetup.app.ui.theme.AppCaption
import com.motosetup.app.ui.theme.AppColor
import com.motosetup.app.ui.theme.AppSpacing
import com.motosetup.app.ui.theme.appGlass

/**
 * Tab bar custom stile "vetro": pill dietro l'icona attiva.
 * Vedi CLAUDE.md — "Navigazione — mapping presentazioni".
 */
@Composable
fun GlassTabBar(
    selected: AppTab,
    onSelect: (AppTab) -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .padding(horizontal = AppSpacing.lg)
            .appGlass(cornerRadius = 28.dp)
            .padding(6.dp),
        horizontalArrangement = Arrangement.SpaceEvenly,
    ) {
        AppTab.entries.forEach { tab ->
            TabButton(
                tab = tab,
                isSelected = tab == selected,
                onClick = { onSelect(tab) },
                modifier = Modifier.weight(1f),
            )
        }
    }
}

@Composable
private fun TabButton(
    tab: AppTab,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val contentColor by animateColorAsState(
        targetValue = if (isSelected) AppColor.textPrimary else AppColor.textSecondary,
        animationSpec = spring(stiffness = Spring.StiffnessMedium),
        label = "tabContentColor",
    )
    val pillColor by animateColorAsState(
        targetValue = if (isSelected) AppColor.accentBlue.copy(alpha = 0.25f) else Color.Transparent,
        animationSpec = spring(stiffness = Spring.StiffnessMedium),
        label = "tabPillColor",
    )

    Column(
        modifier = modifier
            .clip(RoundedCornerShape(20.dp))
            .background(pillColor)
            .clickable(onClick = onClick)
            .padding(vertical = AppSpacing.sm),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Icon(
            imageVector = tab.icon,
            contentDescription = tab.title,
            tint = contentColor,
            modifier = Modifier.size(22.dp),
        )
        Text(text = tab.title, style = AppCaption, color = contentColor)
    }
}
