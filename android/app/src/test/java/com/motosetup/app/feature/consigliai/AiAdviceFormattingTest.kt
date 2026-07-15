package com.motosetup.app.feature.consigliai

import java.util.concurrent.TimeUnit
import kotlin.test.Test
import kotlin.test.assertEquals

class AiAdviceFormattingTest {
    private val now = 1_700_000_000_000L

    @Test
    fun labelsToday() {
        assertEquals("oggi", relativeAdviceTimeLabel(now, now))
    }

    @Test
    fun labelsTwoWeeksAgo() {
        val fourteenDaysAgo = now - TimeUnit.DAYS.toMillis(14)
        assertEquals("2 settimane fa", relativeAdviceTimeLabel(fourteenDaysAgo, now))
    }

    @Test
    fun labelsThreeDaysAgo() {
        val threeDaysAgo = now - TimeUnit.DAYS.toMillis(3)
        assertEquals("3 giorni fa", relativeAdviceTimeLabel(threeDaysAgo, now))
    }
}
