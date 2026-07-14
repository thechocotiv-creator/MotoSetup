package com.motosetup.app.ui.theme

import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

/**
 * Inter (pesi 400-900) è il font del design; finché i file non sono stati
 * aggiunti a res/font/, si ricade su FontFamily.Default (Roboto/system).
 * Il fallback è esplicito: AppFont.usingFallback lo segnala per parità con iOS.
 */
object AppFont {
    // TODO: sostituire con FontFamily(Font(R.font.inter_regular, FontWeight.Normal), ...)
    // quando i file Inter (condivisi con ios/MotoSetup/Resources/Fonts/) saranno aggiunti
    // a res/font/ (nomi minuscolo+underscore, es. inter_semibold.ttf).
    val family: FontFamily = FontFamily.Default
    const val usingFallback: Boolean = true

    fun style(weight: FontWeight, size: Int): TextStyle =
        TextStyle(fontFamily = family, fontWeight = weight, fontSize = size.sp)
}

val AppLargeTitle = AppFont.style(FontWeight.ExtraBold, 28) // "Home", "Setup", "Profilo"
val AppTitle = AppFont.style(FontWeight.Bold, 20)
val AppHeadline = AppFont.style(FontWeight.SemiBold, 17)
val AppBody = AppFont.style(FontWeight.Medium, 15)
val AppCaption = AppFont.style(FontWeight.Medium, 13)
val AppEyebrow = AppFont.style(FontWeight.Bold, 11) // label sezione, uppercase, tracking
