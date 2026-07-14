package com.motosetup.app.navigation

import kotlin.test.Test
import kotlin.test.assertEquals

/**
 * I messaggi devono restare identici, carattere per carattere, alla tabella
 * "Piano Premium" del CLAUDE.md alla radice del repo — non parafrasare.
 */
class PaywallReasonTest {
    @Test
    fun messagesMatchExactCopyFromRootClaudeMd() {
        assertEquals(
            "Il piano Free include 1 sola moto in garage. Passa a Premium per moto illimitate.",
            PaywallReason.BikeLimit.message,
        )
        assertEquals(
            "Hai esaurito il consiglio AI gratuito di oggi. Passa a Premium per consigli illimitati.",
            PaywallReason.AiAdviceLimit.message,
        )
        assertEquals(
            "Il piano Free include massimo 3 run per sessione. Passa a Premium per run illimitati.",
            PaywallReason.RunLimit.message,
        )
    }
}
