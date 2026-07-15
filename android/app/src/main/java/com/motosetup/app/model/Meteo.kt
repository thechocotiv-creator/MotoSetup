package com.motosetup.app.model

enum class Meteo {
    Sole,
    Nuvole,
    Pioggia;

    fun label(): String = when (this) {
        Sole -> "Soleggiato"
        Nuvole -> "Variabile"
        Pioggia -> "Piovoso"
    }

    fun next(): Meteo = entries[(ordinal + 1) % entries.size]
}
