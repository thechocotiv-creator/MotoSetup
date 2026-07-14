package com.motosetup.app.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue

/**
 * Trattiene l'ultimo valore non nullo durante la transizione di uscita di un
 * AnimatedVisibility: senza questo, il contenuto sparirebbe subito (invece di
 * scivolare via) perché `sheet`/`dialog` diventa null nello stesso frame in
 * cui parte l'animazione di dismiss.
 */
@Composable
internal fun <T> rememberLatestNonNull(value: T?): T? {
    var latest by remember { mutableStateOf(value) }
    if (value != null) latest = value
    return latest
}
