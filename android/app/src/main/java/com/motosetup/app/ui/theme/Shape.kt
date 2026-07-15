package com.motosetup.app.ui.theme

import androidx.compose.ui.unit.dp

/** Raggi angolo — CLAUDE.md alla radice, sezione "Design tokens". */
object AppRadius {
    val button = 14.dp
    val card = 16.dp
    val bottomSheet = 24.dp
    val wheelPicker = 26.dp
    val alert = 20.dp
}

object AppSpacing {
    val xs = 4.dp
    val sm = 8.dp
    val md = 12.dp
    val lg = 16.dp
    val xl = 24.dp
    val xxl = 32.dp

    /** Riserva sotto il contenuto di NavDisplay perché GlassTabBar è un overlay, non consuma spazio di layout — vedi RootScaffold. */
    val tabBarClearance = 96.dp
}
