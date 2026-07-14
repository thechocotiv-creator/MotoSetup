package com.motosetup.app.ui.theme

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals
import kotlin.test.assertTrue

/**
 * Test di base per la Fase 0: confermano che il target di test e il design
 * system compilino ed eseguano correttamente (build locale + CI).
 */
class DesignSystemTest {
    @Test
    fun radiiAreWithinDesignTokenRange() {
        assertTrue(AppRadius.button.value in 12f..16f)
        assertTrue(AppRadius.bottomSheet.value in 20f..26f)
        assertEquals(26f, AppRadius.wheelPicker.value)
        assertEquals(20f, AppRadius.alert.value)
    }

    @Test
    fun bikeCardColorHasFiveOptions() {
        assertEquals(5, BikeCardColor.entries.size)
    }

    @Test
    fun maintenanceStatusColorScaleUsesThreeStates() {
        val ok = AppColor.status(AppStatus.Ok)
        val upcoming = AppColor.status(AppStatus.Upcoming)
        val expired = AppColor.status(AppStatus.Expired)
        assertNotEquals(ok, upcoming)
        assertNotEquals(upcoming, expired)
        assertNotEquals(ok, expired)
    }
}
