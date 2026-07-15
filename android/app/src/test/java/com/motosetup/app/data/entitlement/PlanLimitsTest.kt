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

    @Test
    fun freePlanAllowsUpToThreeRuns() {
        assertTrue(canAddRun(currentRunCount = 2, isPremium = false))
        assertFalse(canAddRun(currentRunCount = 3, isPremium = false))
    }

    @Test
    fun premiumPlanHasNoRunLimit() {
        assertTrue(canAddRun(currentRunCount = 3, isPremium = true))
        assertTrue(canAddRun(currentRunCount = 99, isPremium = true))
    }
}
