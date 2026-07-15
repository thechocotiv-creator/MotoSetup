package com.motosetup.app.data.entitlement

/** Vedi CLAUDE.md alla radice, tabella "Piano Premium — regole". */
private const val FREE_BIKE_LIMIT = 1
private const val FREE_RUN_LIMIT = 3

fun canAddBike(currentBikeCount: Int, isPremium: Boolean): Boolean =
    isPremium || currentBikeCount < FREE_BIKE_LIMIT

fun canAddRun(currentRunCount: Int, isPremium: Boolean): Boolean =
    isPremium || currentRunCount < FREE_RUN_LIMIT
