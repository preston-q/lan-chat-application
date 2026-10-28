package com.lanchat.lanchat;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.time.LocalTime;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class ConnectionMonitor {

    private static final Logger log = LoggerFactory.getLogger("lanchat.monitor");

    public record Connection(String id, String ip, String room, LocalTime joinedAt) {}

    private final Map<String, Connection> connections = new ConcurrentHashMap<>();

    public void joined(String id, String ip, String room) {
        connections.put(id, new Connection(id, ip, room, LocalTime.now().withNano(0)));
        render("JOINED " + ip);
    }

    public void left(String id) {
        Connection c = connections.remove(id);
        if (c != null) render("LEFT " + c.ip());
    }

    private synchronized void render(String event) {
        StringBuilder sb = new StringBuilder();
        sb.append("\n--- ").append(event).append(" ---\n");
        sb.append("Active connections: ").append(connections.size()).append("\n");
        connections.values().forEach(c ->
                sb.append("  ").append(c.ip())
                  .append("  room ").append(c.room())
                  .append("  since ").append(c.joinedAt()).append("\n"));
        log.info(sb.toString());
    }
}