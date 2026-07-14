package com.motosetup.app.feature.auth

private val emailRegex = Regex("^[^\\s@]+@[^\\s@]+\\.[^\\s@]+$")

fun isValidEmail(email: String): Boolean = emailRegex.matches(email)

fun validateLoginForm(email: String, password: String): List<String> = buildList {
    if (!isValidEmail(email)) add("Inserisci un'email valida.")
    if (password.isEmpty()) add("Inserisci la password.")
}

fun validateRegisterForm(
    nickname: String,
    email: String,
    password: String,
    confirmPassword: String,
): List<String> = buildList {
    if (nickname.isBlank()) add("Inserisci un nickname.")
    if (!isValidEmail(email)) add("Inserisci un'email valida.")
    if (password.isEmpty()) add("Inserisci la password.")
    if (password != confirmPassword) add("Le password non coincidono.")
}
