package com.motosetup.app.model

data class GommeSetup(
    val anteriore: PneumaticoSetup = PneumaticoSetup(),
    val posteriore: PneumaticoSetup = PneumaticoSetup(),
)
