package com.epam.gym.crm.util;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CredentialGeneratorTest {

    @Test
    void testGeneratePassword_ReturnsNonNull() {
        String password = CredentialGenerator.generatePassword();
        assertNotNull(password);
    }

    @Test
    void testGeneratePassword_ReturnsCorrectLength() {
        String password = CredentialGenerator.generatePassword();
        assertEquals(10, password.length(), "Password should be exactly 10 characters long");
    }

    @Test
    void testGeneratePassword_ReturnsNonEmpty() {
        String password = CredentialGenerator.generatePassword();
        assertFalse(password.isBlank(), "Password should not be empty or blank");
    }

    @Test
    void testGeneratePassword_ContainsOnlyValidCharacters() {
        String password = CredentialGenerator.generatePassword();
        assertTrue(password.matches("[a-zA-Z0-9]+"), "Password should contain only alphanumeric characters");
    }
}

