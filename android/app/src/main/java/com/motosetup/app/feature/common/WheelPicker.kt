package com.motosetup.app.feature.common

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.snapping.rememberSnapFlingBehavior
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.motosetup.app.ui.theme.AppBody
import com.motosetup.app.ui.theme.AppCaption
import com.motosetup.app.ui.theme.AppColor
import com.motosetup.app.ui.theme.AppHeadline
import com.motosetup.app.ui.theme.AppSpacing
import kotlin.math.abs

private val WheelItemHeight = 40.dp
private const val WHEEL_VISIBLE_ROWS = 3

/**
 * Colonna a scorrimento stile iOS wheel — vedi design_handoff_motosetup_app/README.md #4a,
 * rischio #5 in android/CLAUDE.md (nessun Picker(.wheel) nativo in Compose).
 * L'indice selezionato si aggiorna solo a scroll fermo (via visibleItemsInfo più vicino al
 * centro del viewport), non ad ogni frame: coerente con l'header "Fatto" del picker, che
 * legge lo stato finale invece di committare in streaming.
 */
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun AppWheelPicker(
    items: List<String>,
    selectedIndex: Int,
    onSelectedIndexChange: (Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    val listState = rememberLazyListState()

    LaunchedEffect(items) {
        listState.scrollToItem(selectedIndex.coerceIn(0, (items.size - 1).coerceAtLeast(0)))
    }

    LaunchedEffect(listState.isScrollInProgress) {
        if (!listState.isScrollInProgress) {
            val viewportCenter = listState.layoutInfo.viewportSize.height / 2
            val centered = listState.layoutInfo.visibleItemsInfo.minByOrNull { info ->
                abs((info.offset + info.size / 2) - viewportCenter)
            }
            if (centered != null && centered.index != selectedIndex) {
                onSelectedIndexChange(centered.index)
            }
        }
    }

    Box(modifier = modifier.width(64.dp).height(WheelItemHeight * WHEEL_VISIBLE_ROWS), contentAlignment = Alignment.Center) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(WheelItemHeight)
                .background(AppColor.textPrimary.copy(alpha = 0.08f), RoundedCornerShape(8.dp)),
        )
        LazyColumn(
            state = listState,
            flingBehavior = rememberSnapFlingBehavior(listState),
            contentPadding = PaddingValues(vertical = WheelItemHeight),
            modifier = Modifier.fillMaxHeight(),
        ) {
            itemsIndexed(items) { index, label ->
                val isSelected = index == selectedIndex
                Box(
                    modifier = Modifier.fillMaxWidth().height(WheelItemHeight),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        text = label,
                        style = if (isSelected) AppHeadline else AppBody,
                        color = if (isSelected) AppColor.textPrimary else AppColor.textSecondary.copy(alpha = 0.6f),
                        textAlign = TextAlign.Center,
                    )
                }
            }
        }
    }
}

/**
 * Bottom sheet "Annulla / titolo / Fatto" con N colonne di [AppWheelPicker] separate da
 * separatori testuali (":" per Ora, "." per Best lap) — vedi #4a.
 */
@Composable
fun AppPickerSheetContent(
    title: String,
    columns: List<List<String>>,
    separators: List<String?>,
    initialIndices: List<Int>,
    onDone: (List<Int>) -> Unit,
    onCancel: () -> Unit,
) {
    var indices by remember { mutableStateOf(initialIndices) }

    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Text(
                text = "Annulla",
                style = AppBody,
                color = AppColor.textSecondary,
                modifier = Modifier.clickable(onClick = onCancel),
            )
            Text(text = title, style = AppHeadline, color = AppColor.textPrimary)
            Text(
                text = "Fatto",
                style = AppBody,
                color = AppColor.accentBlue,
                modifier = Modifier.clickable { onDone(indices) },
            )
        }

        Row(
            modifier = Modifier.fillMaxWidth().padding(top = AppSpacing.lg),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            columns.forEachIndexed { columnIndex, values ->
                AppWheelPicker(
                    items = values,
                    selectedIndex = indices.getOrElse(columnIndex) { 0 },
                    onSelectedIndexChange = { newIndex ->
                        indices = indices.toMutableList().also { it[columnIndex] = newIndex }
                    },
                    modifier = Modifier.padding(horizontal = AppSpacing.sm),
                )
                separators.getOrNull(columnIndex)?.let { separator ->
                    Text(text = separator, style = AppCaption, color = AppColor.textSecondary)
                }
            }
        }
    }
}
