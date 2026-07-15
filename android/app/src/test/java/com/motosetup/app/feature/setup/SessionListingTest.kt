package com.motosetup.app.feature.setup

import com.motosetup.app.model.Meteo
import java.util.concurrent.TimeUnit
import kotlin.test.Test
import kotlin.test.assertEquals

class SessionListingTest {
    private val now = 1_000_000_000_000L

    private fun daysAgo(days: Long) = now - TimeUnit.DAYS.toMillis(days)

    @Test
    fun relativeDayLabelForToday() {
        assertEquals("OGGI", relativeDayLabel(now, now))
    }

    @Test
    fun relativeDayLabelForYesterday() {
        assertEquals("IERI", relativeDayLabel(daysAgo(1), now))
    }

    @Test
    fun relativeDayLabelForThisWeek() {
        assertEquals("3 GIORNI FA", relativeDayLabel(daysAgo(3), now))
    }

    @Test
    fun relativeDayLabelForOneWeekAgo() {
        assertEquals("1 SETTIMANA FA", relativeDayLabel(daysAgo(8), now))
    }

    @Test
    fun filterSessionsMatchesOnAllProvidedCriteria() {
        val mugello = SessionSummary("1", "Mugello", "Yamaha R6", Meteo.Sole, now, 5, "1:52.340", "run1")
        val misano = SessionSummary("2", "Misano", "Ducati Panigale V2", Meteo.Nuvole, daysAgo(3), 4, "1:33.210", "run2")

        assertEquals(listOf(mugello), filterSessions(listOf(mugello, misano), bikeName = "Yamaha R6", trackName = null, dayLabel = null))
        assertEquals(listOf(misano), filterSessions(listOf(mugello, misano), bikeName = null, trackName = "Misano", dayLabel = null))
        assertEquals(listOf(mugello, misano), filterSessions(listOf(mugello, misano), bikeName = null, trackName = null, dayLabel = null))
    }
}
