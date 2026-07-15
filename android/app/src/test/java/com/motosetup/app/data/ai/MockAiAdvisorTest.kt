package com.motosetup.app.data.ai

import kotlin.test.Test
import kotlin.test.assertEquals

class MockAiAdvisorTest {
    @Test
    fun matchesSottosterzoKeywordCaseInsensitive() {
        val advice = generateMockAdvice("Sottosterzo in staccata forte")
        assertEquals("Forcella — Compressione", advice.parameterName)
    }

    @Test
    fun matchesSovrasterzoKeyword() {
        val advice = generateMockAdvice("la moto sovrasterza in uscita di curva")
        assertEquals("Mono — Precarico", advice.parameterName)
    }

    @Test
    fun fallsBackToGenericAdviceWhenNoKeywordMatches() {
        val advice = generateMockAdvice("boh, non saprei come descriverlo")
        assertEquals("Sospensioni — Compressione", advice.parameterName)
    }
}
