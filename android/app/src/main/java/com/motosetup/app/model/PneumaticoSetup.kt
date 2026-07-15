package com.motosetup.app.model

/** Stesso shape per Anteriore e Posteriore (Gomme), vedi design_handoff_motosetup_app/README.md #4. */
data class PneumaticoSetup(
    val giri: Int = 0,
    val pressioneIngresso: Double = 0.0,
    val pressioneUscita: Double = 0.0,
    val note: String = "",
)
