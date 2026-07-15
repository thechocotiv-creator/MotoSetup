package com.motosetup.app.feature.auth

import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class AuthValidationTest {
    @Test
    fun isValidEmailAcceptsWellFormedAddresses() {
        assertTrue(isValidEmail("pilota@motosetup.it"))
        assertFalse(isValidEmail("non-una-email"))
        assertFalse(isValidEmail("manca@dominio"))
    }

    @Test
    fun loginFormRequiresValidEmailAndNonEmptyPassword() {
        assertTrue(validateLoginForm("pilota@motosetup.it", "").isNotEmpty())
        assertTrue(validateLoginForm("non-valida", "password123").isNotEmpty())
        assertTrue(validateLoginForm("pilota@motosetup.it", "password123").isEmpty())
    }

    @Test
    fun registerFormRequiresNicknameAndMatchingPasswords() {
        assertTrue(validateRegisterForm("", "pilota@motosetup.it", "password123", "password123").isNotEmpty())
        assertTrue(validateRegisterForm("Rossi", "pilota@motosetup.it", "password123", "diversa").isNotEmpty())
        assertTrue(validateRegisterForm("Rossi", "pilota@motosetup.it", "password123", "password123").isEmpty())
    }

    @Test
    fun passwordChangeFormRequiresCurrentPasswordAndMatchingNewPasswords() {
        assertTrue(validatePasswordChangeForm("", "password123", "password123").isNotEmpty())
        assertTrue(validatePasswordChangeForm("attuale", "corta", "corta").isNotEmpty())
        assertTrue(validatePasswordChangeForm("attuale", "password123", "diversa").isNotEmpty())
        assertTrue(validatePasswordChangeForm("attuale", "password123", "password123").isEmpty())
    }
}
