package com.motosetup.app.data.entitlement

import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class PlanLimitsTest {
    @Test
    fun freePlanAllowsFirstBikeOnly() {
        assertTrue(canAddBike(currentBikeCount = 0, isPremium = false))
        assertFalse(canAddBike(currentBikeCount = 1, isPremium = false))
    }

    @Test
    fun premiumPlanHasNoLimit() {
        assertTrue(canAddBike(currentBikeCount = 0, isPremium = true))
        assertTrue(canAddBike(currentBikeCount = 42, isPremium = true))
    }
}
