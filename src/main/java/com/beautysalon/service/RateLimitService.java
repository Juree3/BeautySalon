package com.beautysalon.service;

import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class RateLimitService {

    private static class Entry {
        int count;
        Instant windowStart;
        Instant blockedUntil;
    }

    private final ConcurrentHashMap<String, Entry> store = new ConcurrentHashMap<>();

    // Za register i forgot-password: max pokušaja po satu
    public boolean isAllowedPerHour(String key, int maxAttempts) {

        Entry entry = store.computeIfAbsent(key, k -> new Entry());
        Instant now = Instant.now();

        synchronized (entry) {
            if (entry.windowStart == null || now.isAfter(entry.windowStart.plusSeconds(3600))) {
                entry.windowStart = now;
                entry.count = 1;
                return true;
            }

            entry.count++;
            return entry.count <= maxAttempts;
        }
    }

    // Za login: broji samo neuspješne, blokira nakon max, s timeoutom
    public boolean isLoginAllowed(String key) {

        Entry entry = store.computeIfAbsent(key, k -> new Entry());
        Instant now = Instant.now();

        synchronized (entry) {
            if (entry.blockedUntil != null && now.isBefore(entry.blockedUntil)) {
                return false;
            }
            return true;
        }
    }

    public void recordFailedLogin(String key) {

        Entry entry = store.computeIfAbsent(key, k -> new Entry());
        Instant now = Instant.now();

        synchronized (entry) {
            entry.count++;
            if (entry.count >= 3) {
                entry.blockedUntil = now.plusSeconds(15 * 60);
                entry.count = 0;
            }
        }
    }

    public void recordSuccessfulLogin(String key) {

        Entry entry = store.computeIfAbsent(key, k -> new Entry());

        synchronized (entry) {
            entry.count = 0;
            entry.blockedUntil = null;
        }
    }
}