package com.motosetup.app.model

/** Stesso shape per Forcella e Mono (Sospensioni), vedi design_handoff_motosetup_app/README.md #4. */
data class ForcellaMonoSetup(
    val molla: String = "",
    val altezza: Int = 0,
    val compressione: Int = 0,
    val estensione: Int = 0,
    val precarico: Int = 0,
    val note: String = "",
)
