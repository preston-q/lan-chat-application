package com.lanchat.lanchat;

import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.time.Instant;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class RoomService {

    private static final String ALPHABET = "ABCDEFGHJKLMNPQRSTUVWXYZ23456789";

    public record Room(String code, Instant createdAt) {}

    private final SecureRandom random = new SecureRandom();
    private final Map<String, Room> rooms = new ConcurrentHashMap<>();

    public Room create() {
        Room room;
        do {
            room = new Room(generateCode(), Instant.now());
        } while (rooms.putIfAbsent(room.code(), room) != null);
        return room;
    }

    public Optional<Room> find(String code) {
        if (code == null) return Optional.empty();
        String clean = code.replaceAll("[^A-Za-z0-9]", "").toUpperCase();
        if (clean.length() != 12) return Optional.empty();
        String formatted = clean.substring(0, 4) + "-"
                + clean.substring(4, 8) + "-"
                + clean.substring(8);
        return Optional.ofNullable(rooms.get(formatted));
    }

    @EventListener(ApplicationReadyEvent.class)
    void createStartupRoom() {
        Room room = create();
        System.out.println("\n=== ROOM READY ===");
        System.out.println(" Code: " + room.code());
        System.out.println(" Path: /room/" + room.code());
    }

   private String generateCode() {
    StringBuilder sb = new StringBuilder();
    for (int i = 0; i < 12; i++) {
        if (i > 0 && i % 4 == 0) sb.append('-');
        sb.append(ALPHABET.charAt(random.nextInt(ALPHABET.length())));
    }
    return sb.toString();
}
}