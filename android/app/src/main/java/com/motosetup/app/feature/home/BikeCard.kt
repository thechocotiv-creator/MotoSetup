package com.motosetup.app.feature.home

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.TwoWheeler
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.motosetup.app.feature.manutenzione.label
import com.motosetup.app.feature.manutenzione.maintenanceStatus
import com.motosetup.app.feature.manutenzione.sortedByUrgency
import com.motosetup.app.model.Bike
import com.motosetup.app.model.BikeRarity
import com.motosetup.app.model.MaintenanceItem
import com.motosetup.app.ui.theme.AppCaption
import com.motosetup.app.ui.theme.AppColor
import com.motosetup.app.ui.theme.AppEyebrow
import com.motosetup.app.ui.theme.AppRadius

private const val MAX_MAINTENANCE_PREVIEW = 4

@Composable
fun BikeCard(
    bike: Bike,
    maintenanceItems: List<MaintenanceItem>,
    onClick: () -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val accent = bike.cardColor.swatch
    Box(
        modifier = modifier
            .width(260.dp)
            .clip(RoundedCornerShape(18.dp))
            .background(accent)
            .padding(6.dp)
            .clickable(onClick = onClick),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(AppRadius.card))
                .background(AppColor.panel),
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(accent)
                    .padding(horizontal = 12.dp, vertical = 9.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(text = bike.name.uppercase(), style = AppCaption, color = Color.Black)
                Row(horizontalArrangement = Arrangement.spacedBy(3.dp)) {
                    repeat(bike.rarity.dotCount()) {
                        Box(modifier = Modifier.size(7.dp).rotate(45f).background(Color.Black))
                    }
                }
            }

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(16f / 9f)
                    .background(Color.White.copy(alpha = 0.05f)),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    Icons.Filled.TwoWheeler,
                    contentDescription = null,
                    tint = AppColor.textPrimary,
                    modifier = Modifier.size(56.dp),
                )
                Row(
                    modifier = Modifier.align(Alignment.TopEnd).padding(8.dp),
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                ) {
                    CircleIconButton(icon = Icons.Filled.Edit, contentDescription = "Modifica moto", onClick = onEdit)
                    CircleIconButton(
                        icon = Icons.Filled.Delete,
                        contentDescription = "Elimina moto",
                        onClick = onDelete,
                        tint = AppColor.red,
                    )
                }
            }

            Column(modifier = Modifier.padding(12.dp)) {
                if (bike.subtitle.isNotBlank()) {
                    Text(text = bike.subtitle, style = AppCaption, color = AppColor.textSecondary)
                }
                Text(
                    text = "MANUTENZIONE",
                    style = AppEyebrow,
                    color = AppColor.textSecondary,
                    modifier = Modifier.padding(top = 10.dp, bottom = 7.dp),
                )
                maintenanceItems.sortedByUrgency().take(MAX_MAINTENANCE_PREVIEW).forEach { item ->
                    val status = maintenanceStatus(item.daysSinceService, item.intervalDays)
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(vertical = 2.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                    ) {
                        Text(
                            text = item.name,
                            style = AppCaption,
                            color = AppColor.textPrimary,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier.weight(1f).padding(end = 8.dp),
                        )
                        Text(text = status.label().uppercase(), style = AppEyebrow, color = AppColor.status(status))
                    }
                }
            }
        }
    }
}

@Composable
private fun CircleIconButton(icon: ImageVector, contentDescription: String, onClick: () -> Unit, tint: Color = AppColor.textPrimary) {
    Box(
        modifier = Modifier
            .size(26.dp)
            .clip(CircleShape)
            .background(Color.Black.copy(alpha = 0.5f))
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        Icon(icon, contentDescription = contentDescription, tint = tint, modifier = Modifier.size(14.dp))
    }
}

private fun BikeRarity.dotCount(): Int = when (this) {
    BikeRarity.Comune -> 1
    BikeRarity.Rara -> 2
    BikeRarity.Leggendaria -> 3
}
