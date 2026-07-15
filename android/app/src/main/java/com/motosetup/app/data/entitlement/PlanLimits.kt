package com.motosetup.app.data.entitlement

/** Vedi CLAUDE.md alla radice, tabella "Piano Premium — regole". */
private const val FREE_BIKE_LIMIT = 1

fun canAddBike(currentBikeCount: Int, isPremium: Boolean): Boolean =
    isPremium || currentBikeCount < FREE_BIKE_LIMIT
