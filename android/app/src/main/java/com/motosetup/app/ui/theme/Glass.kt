package com.motosetup.app.ui.theme

import androidx.compose.foundation.border
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import dev.chrisbanes.haze.HazeState
import dev.chrisbanes.haze.HazeStyle
import dev.chrisbanes.haze.HazeTint
import dev.chrisbanes.haze.hazeEffect
import dev.chrisbanes.haze.hazeSource

/**
 * Stato Haze condiviso a livello root: ogni superficie "vetro" campiona lo
 * stesso backdrop — equivalente strutturale del GlassEffectContainer di iOS
 * (il glass non può campionare altro glass senza una sorgente condivisa).
 * Fornito da RootScaffold; il default qui è solo un fallback per le Preview.
 */
val LocalHazeState = staticCompositionLocalOf { HazeState() }

/** Marca il contenuto principale come sorgente da sfocare per le superfici glass sovrapposte. */
fun Modifier.appGlassBackdrop(): Modifier = composed {
    hazeSource(state = LocalHazeState.current)
}

/**
 * Primitiva unica per l'effetto vetro dell'app — non duplicare blur/bordo
 * nelle singole schermate, usare sempre questo modifier.
 */
fun Modifier.appGlass(cornerRadius: Dp = AppRadius.button, tint: Color = AppColor.panel): Modifier =
    composed {
        val state = LocalHazeState.current
        val style = HazeStyle(
            backgroundColor = AppColor.background,
            tints = listOf(HazeTint(tint.copy(alpha = 0.35f))),
            blurRadius = 20.dp,
        )
        this
            .clip(RoundedCornerShape(cornerRadius))
            .hazeEffect(state = state, style = style)
            .border(1.dp, Color.White.copy(alpha = 0.18f), RoundedCornerShape(cornerRadius))
    }
