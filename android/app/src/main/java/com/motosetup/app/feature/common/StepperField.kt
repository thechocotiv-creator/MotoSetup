package com.motosetup.app.feature.common

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.motosetup.app.ui.theme.AppBody
import com.motosetup.app.ui.theme.AppCaption
import com.motosetup.app.ui.theme.AppColor
import com.motosetup.app.ui.theme.AppSpacing

/** Campo "– valore +" per i parametri di setup numerici — vedi design_handoff_motosetup_app/README.md #4. */
@Composable
fun IntStepperField(label: String, value: Int, onValueChange: (Int) -> Unit, modifier: Modifier = Modifier, step: Int = 1) {
    StepperRow(
        label = label,
        valueText = value.toString(),
        onDecrement = { onValueChange(value - step) },
        onIncrement = { onValueChange(value + step) },
        modifier = modifier,
    )
}

@Composable
fun DoubleStepperField(label: String, value: Double, onValueChange: (Double) -> Unit, modifier: Modifier = Modifier, step: Double = 0.1) {
    StepperRow(
        label = label,
        valueText = "%.1f".format(value),
        onDecrement = { onValueChange(value - step) },
        onIncrement = { onValueChange(value + step) },
        modifier = modifier,
    )
}

@Composable
private fun StepperRow(label: String, valueText: String, onDecrement: () -> Unit, onIncrement: () -> Unit, modifier: Modifier = Modifier) {
    Row(
        modifier = modifier.fillMaxWidth().padding(vertical = AppSpacing.sm),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(text = label, style = AppCaption, color = AppColor.textSecondary)
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(AppSpacing.md)) {
            StepperButton(symbol = "–", onClick = onDecrement)
            Text(text = valueText, style = AppBody, color = AppColor.textPrimary, textAlign = TextAlign.Center, modifier = Modifier.width(48.dp))
            StepperButton(symbol = "+", onClick = onIncrement)
        }
    }
}

@Composable
private fun StepperButton(symbol: String, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .size(28.dp)
            .background(AppColor.textPrimary.copy(alpha = 0.1f), CircleShape)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        Text(text = symbol, style = AppBody, color = AppColor.textPrimary)
    }
}
