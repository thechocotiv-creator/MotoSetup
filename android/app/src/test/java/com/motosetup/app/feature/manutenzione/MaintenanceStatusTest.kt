package com.motosetup.app.feature.manutenzione

import com.motosetup.app.model.MaintenanceItem
import com.motosetup.app.ui.theme.AppStatus
import kotlin.test.Test
import kotlin.test.assertEquals

class MaintenanceStatusTest {
    @Test
    fun expiredWhenRatioAtLeastOne() {
        assertEquals(AppStatus.Expired, maintenanceStatus(daysSinceService = 95, intervalDays = 90))
        assertEquals(AppStatus.Expired, maintenanceStatus(daysSinceService = 90, intervalDays = 90))
    }

    @Test
    fun upcomingWhenRatioAtLeastEightyPercent() {
        assertEquals(AppStatus.Upcoming, maintenanceStatus(daysSinceService = 25, intervalDays = 30))
        assertEquals(AppStatus.Upcoming, maintenanceStatus(daysSinceService = 24, intervalDays = 30))
    }

    @Test
    fun okBelowEightyPercent() {
        assertEquals(AppStatus.Ok, maintenanceStatus(daysSinceService = 3, intervalDays = 10))
    }

    @Test
    fun sortedByUrgencyOrdersExpiredFirstThenUpcomingThenOk() {
        val ok = MaintenanceItem(id = "ok", daysSinceService = 3, intervalDays = 10)
        val upcoming = MaintenanceItem(id = "upcoming", daysSinceService = 25, intervalDays = 30)
        val expired = MaintenanceItem(id = "expired", daysSinceService = 95, intervalDays = 90)

        val sorted = listOf(ok, upcoming, expired).sortedByUrgency()

        assertEquals(listOf("expired", "upcoming", "ok"), sorted.map { it.id })
    }
}
