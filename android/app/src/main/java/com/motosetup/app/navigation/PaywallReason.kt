package com.motosetup.app.navigation

/**
 * Causale di apertura del paywall. Messaggi esatti dalla tabella "Piano
 * Premium" in CLAUDE.md (radice) — non parafrasare, deve restare identico
 * a iOS.
 */
enum class PaywallReason(val message: String) {
    BikeLimit("Il piano Free include 1 sola moto in garage. Passa a Premium per moto illimitate."),
    AiAdviceLimit("Hai esaurito il consiglio AI gratuito di oggi. Passa a Premium per consigli illimitati."),
    RunLimit("Il piano Free include massimo 3 run per sessione. Passa a Premium per run illimitati."),
}
