package com.epam.gym.crm.util;

import java.security.SecureRandom;
import java.util.Set;

public class CredentialGenerator {

    private static final String CHARACTERS =
            "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";

    private static final SecureRandom random = new SecureRandom();

    public static String generatePassword() {
        StringBuilder sb = new StringBuilder(10);

        for (int i = 0; i < 10; i++) {
            sb.append(CHARACTERS.charAt(random.nextInt(CHARACTERS.length())));
        }

        return sb.toString();
    }

    public static String generateUsername(String firstName, String lastName, Set<String> existingUsernames) {
        String baseUsername = firstName + "." + lastName;

        if (!existingUsernames.contains(baseUsername)) {
            return baseUsername;
        }

        int counter = 1;
        while (existingUsernames.contains(baseUsername + counter)) {
            counter++;
        }

        return baseUsername + counter;
    }
}