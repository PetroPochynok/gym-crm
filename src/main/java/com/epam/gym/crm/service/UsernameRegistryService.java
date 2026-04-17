package com.epam.gym.crm.service;

import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class UsernameRegistryService {

    private static final Pattern USERNAME_PATTERN = Pattern.compile("^(.+?)(\\d+)?$");

    private final Set<String> usedUsernames = new HashSet<>();
    private final Map<String, Integer> nextSuffixByBase = new HashMap<>();

    public synchronized String reserveUsername(String firstName, String lastName) {
        String base = (firstName + "." + lastName).toLowerCase(Locale.ROOT);
        int nextSuffix = nextSuffixByBase.getOrDefault(base, 0);

        String username;
        if (nextSuffix == 0 && !usedUsernames.contains(base)) {
            username = base;
        } else {
            int candidateSuffix = Math.max(nextSuffix, 1);
            while (usedUsernames.contains(base + candidateSuffix)) {
                candidateSuffix++;
            }
            username = base + candidateSuffix;
        }

        register(username);
        return username;
    }

    public synchronized void releaseUsername(String username) {
        if (username != null) {
            usedUsernames.remove(username.toLowerCase(Locale.ROOT));
        }
    }

    public synchronized void initializeFromExisting(Collection<String> usernames) {
        for (String username : usernames) {
            if (username != null && !username.isBlank()) {
                register(username.toLowerCase(Locale.ROOT));
            }
        }
    }

    private void register(String username) {
        usedUsernames.add(username);

        Matcher matcher = USERNAME_PATTERN.matcher(username);
        if (!matcher.matches()) {
            return;
        }

        String base = matcher.group(1);
        String suffixValue = matcher.group(2);
        int suffix = suffixValue == null ? 0 : Integer.parseInt(suffixValue);
        int nextSuffix = suffix + 1;

        nextSuffixByBase.merge(base, nextSuffix, Math::max);
    }
}