package com.motosetup.app.model

import com.google.firebase.firestore.DocumentId

data class Run(
    @DocumentId val id: String = "",
    val index: Int = 1,
    val time: String = "12:00",
    val temperature: Int = 20,
    val bestLap: String = "",
    val laps: Int = 0,
    val sospensione: SospensioneSetup = SospensioneSetup(),
    val gomme: GommeSetup = GommeSetup(),
    val rapporti: RapportiSetup = RapportiSetup(),
    val elettronica: ElettronicaSetup = ElettronicaSetup(),
)
