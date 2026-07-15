package com.motosetup.app.feature.sessioni

import com.motosetup.app.model.Run
import com.motosetup.app.navigation.PickerKind
import kotlin.test.Test
import kotlin.test.assertEquals

class PickerFormattingTest {
    @Test
    fun oraRoundTrips() {
        val run = Run(time = "14:32")
        val indices = pickerInitialIndices(PickerKind.Ora, run)
        assertEquals("time" to "14:32", pickerFieldUpdate(PickerKind.Ora, indices))
    }

    @Test
    fun bestLapRoundTrips() {
        val run = Run(bestLap = "1:52.340")
        val indices = pickerInitialIndices(PickerKind.BestLap, run)
        assertEquals("bestLap" to "1:52.340", pickerFieldUpdate(PickerKind.BestLap, indices))
    }

    @Test
    fun temperaturaRoundTrips() {
        val run = Run(temperature = 24)
        val indices = pickerInitialIndices(PickerKind.Temperatura, run)
        assertEquals("temperature" to 24, pickerFieldUpdate(PickerKind.Temperatura, indices))
    }

    @Test
    fun giriRoundTrips() {
        val run = Run(laps = 8)
        val indices = pickerInitialIndices(PickerKind.Giri, run)
        assertEquals("laps" to 8, pickerFieldUpdate(PickerKind.Giri, indices))
    }
}
