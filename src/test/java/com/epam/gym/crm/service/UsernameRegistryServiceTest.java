package com.epam.gym.crm.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertEquals;

class UsernameRegistryServiceTest {

    private UsernameRegistryService usernameRegistryService;

    @BeforeEach
    void setUp() {
        usernameRegistryService = new UsernameRegistryService();
    }

    @Test
    void testReserveUsername_FirstUserWithoutSuffix() {
        String username = usernameRegistryService.reserveUsername("John", "Doe");
        assertEquals("john.doe", username);
    }

    @Test
    void testReserveUsername_SecondUserWithSameName_GetsSuffix1() {
        usernameRegistryService.reserveUsername("John", "Doe");
        String username2 = usernameRegistryService.reserveUsername("John", "Doe");
        assertEquals("john.doe1", username2);
    }

    @Test
    void testReserveUsername_ThirdUserWithSameName_GetsSuffix2() {
        usernameRegistryService.reserveUsername("John", "Doe");
        usernameRegistryService.reserveUsername("John", "Doe");
        String username3 = usernameRegistryService.reserveUsername("John", "Doe");
        assertEquals("john.doe2", username3);
    }

    @Test
    void testReserveUsername_CaseInsensitive() {
        String username1 = usernameRegistryService.reserveUsername("John", "Doe");
        String username2 = usernameRegistryService.reserveUsername("JOHN", "DOE");
        assertEquals("john.doe", username1);
        assertEquals("john.doe1", username2);
    }

    @Test
    void testReleaseUsername_RemovesUsername() {
        usernameRegistryService.reserveUsername("John", "Doe");
        usernameRegistryService.releaseUsername("john.doe");

        String newUsername = usernameRegistryService.reserveUsername("John", "Doe");
        assertEquals("john.doe1", newUsername);
    }

    @Test
    void testReleaseUsername_WithNull() {
        // Should not throw exception
        usernameRegistryService.releaseUsername(null);
        usernameRegistryService.releaseUsername("");
    }

    @Test
    void testInitializeFromExisting_RegistersUsernames() {
        usernameRegistryService.initializeFromExisting(Arrays.asList("john.doe", "john.doe1", "jane.smith"));
        
        String newUsername = usernameRegistryService.reserveUsername("John", "Doe");
        assertEquals("john.doe2", newUsername);
    }

    @Test
    void testInitializeFromExisting_IgnoresNullAndBlank() {
        usernameRegistryService.initializeFromExisting(Arrays.asList("john.doe", null, "", "jane.smith"));
        
        String newUsername = usernameRegistryService.reserveUsername("John", "Doe");
        assertEquals("john.doe1", newUsername);
    }

    @Test
    void testInitializeFromExisting_HandlesComplexScenario() {
        // Simulate previous state: john.doe, john.doe1, john.doe3 (2 was deleted)
        usernameRegistryService.initializeFromExisting(Arrays.asList("john.doe", "john.doe1", "john.doe3"));
        
        // Next should be john.doe4 (not 2, because we don't reuse deleted suffixes)
        String newUsername = usernameRegistryService.reserveUsername("John", "Doe");
        assertEquals("john.doe4", newUsername);
    }

    @Test
    void testReserveUsername_MultipleUsers_DifferentNames() {
        String user1 = usernameRegistryService.reserveUsername("John", "Doe");
        String user2 = usernameRegistryService.reserveUsername("Jane", "Smith");
        String user3 = usernameRegistryService.reserveUsername("John", "Doe");
        
        assertEquals("john.doe", user1);
        assertEquals("jane.smith", user2);
        assertEquals("john.doe1", user3);
    }

    @Test
    void testReserveUsername_ComplexMultipleSuffixes() {
        String user1 = usernameRegistryService.reserveUsername("Mary", "Dou");
        String user2 = usernameRegistryService.reserveUsername("Mary", "Dou");
        String user3 = usernameRegistryService.reserveUsername("Mary", "Dou");
        
        assertEquals("mary.dou", user1);
        assertEquals("mary.dou1", user2);
        assertEquals("mary.dou2", user3);

        usernameRegistryService.releaseUsername("mary.dou1");

        String user4 = usernameRegistryService.reserveUsername("Mary", "Dou");
        assertEquals("mary.dou3", user4);
    }
}

