package pse.nebula.worldview.infrastructure.adapter.web.sse;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import pse.nebula.worldview.infrastructure.adapter.web.dto.CoordinateUpdateDto;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Manages SSE (Server-Sent Events) emitters for streaming coordinate updates to clients.
 * Each journey can have multiple clients subscribed to receive updates.
 */
@Slf4j
@Component
public class SseEmitterManager {

    private static final long SSE_TIMEOUT = 30 * 60 * 1000L; // 30 minutes

    // Map of journeyId -> Map of clientId -> SseEmitter
    private final Map<String, Map<String, SseEmitter>> journeyEmitters = new ConcurrentHashMap<>();

    /**
     * Create a new SSE emitter for a journey.
     *
     * @param journeyId The journey to subscribe to
     * @param clientId A unique identifier for this client
     * @return The SSE emitter for the client
     */
    public SseEmitter createEmitter(String journeyId, String clientId) {
        SseEmitter emitter = new SseEmitter(SSE_TIMEOUT);

        // Register emitter
        journeyEmitters.computeIfAbsent(journeyId, k -> new ConcurrentHashMap<>())
            .put(clientId, emitter);

        // Setup cleanup callbacks
        emitter.onCompletion(() -> removeEmitter(journeyId, clientId));
        emitter.onTimeout(() -> removeEmitter(journeyId, clientId));
        emitter.onError(e -> {
            log.debug("SSE error for journey: {}, client: {}", journeyId, clientId, e);
            removeEmitter(journeyId, clientId);
        });

        log.info("Created SSE emitter for journey: {}, client: {}", journeyId, clientId);
        return emitter;
    }

    /**
     * Send a coordinate update to all clients subscribed to a journey.
     *
     * @param journeyId The journey ID
     * @param update The coordinate update to send
     */
    public void sendUpdate(String journeyId, CoordinateUpdateDto update) {
        Map<String, SseEmitter> emitters = journeyEmitters.get(journeyId);
        if (emitters == null || emitters.isEmpty()) {
            log.debug("No SSE emitters for journey: {}", journeyId);
            return;
        }

        log.debug("Sending coordinate update to {} client(s) for journey: {}",
            emitters.size(), journeyId);

        emitters.forEach((clientId, emitter) -> {
            try {
                emitter.send(SseEmitter.event()
                    .name("coordinate-update")
                    .data(update));
            } catch (IOException e) {
                log.debug("Failed to send SSE to client: {}, removing emitter", clientId);
                removeEmitter(journeyId, clientId);
            }
        });
    }

    /**
     * Send a journey started event.
     *
     * @param journeyId The journey ID
     * @param update The initial state
     */
    public void sendJourneyStarted(String journeyId, CoordinateUpdateDto update) {
        sendEvent(journeyId, "journey-started", update);
    }

    /**
     * Send a journey completed event.
     *
     * @param journeyId The journey ID
     * @param update The final state
     */
    public void sendJourneyCompleted(String journeyId, CoordinateUpdateDto update) {
        sendEvent(journeyId, "journey-completed", update);
        // Clean up all emitters for this journey
        removeAllEmitters(journeyId);
    }

    private void sendEvent(String journeyId, String eventName, CoordinateUpdateDto update) {
        Map<String, SseEmitter> emitters = journeyEmitters.get(journeyId);
        if (emitters == null || emitters.isEmpty()) {
            return;
        }

        emitters.forEach((clientId, emitter) -> {
            try {
                emitter.send(SseEmitter.event()
                    .name(eventName)
                    .data(update));
            } catch (IOException e) {
                log.debug("Failed to send SSE event to client: {}", clientId);
                removeEmitter(journeyId, clientId);
            }
        });
    }

    private void removeEmitter(String journeyId, String clientId) {
        Map<String, SseEmitter> emitters = journeyEmitters.get(journeyId);
        if (emitters != null) {
            emitters.remove(clientId);
            log.debug("Removed SSE emitter for journey: {}, client: {}", journeyId, clientId);

            // Clean up empty maps
            if (emitters.isEmpty()) {
                journeyEmitters.remove(journeyId);
            }
        }
    }

    private void removeAllEmitters(String journeyId) {
        Map<String, SseEmitter> emitters = journeyEmitters.remove(journeyId);
        if (emitters != null) {
            emitters.values().forEach(emitter -> {
                try {
                    emitter.complete();
                } catch (Exception e) {
                    // Ignore
                }
            });
            log.info("Removed all SSE emitters for journey: {}", journeyId);
        }
    }

    /**
     * Get the number of active emitters for a journey.
     *
     * @param journeyId The journey ID
     * @return The number of subscribed clients
     */
    public int getEmitterCount(String journeyId) {
        Map<String, SseEmitter> emitters = journeyEmitters.get(journeyId);
        return emitters != null ? emitters.size() : 0;
    }

    /**
     * Get the total number of active emitters.
     *
     * @return The total count
     */
    public int getTotalEmitterCount() {
        return journeyEmitters.values().stream()
            .mapToInt(Map::size)
            .sum();
    }
}