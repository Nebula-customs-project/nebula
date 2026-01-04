package pse.nebula.worldview.infrastructure.adapter.inbound.web.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.async.AsyncRequestNotUsableException;
import pse.nebula.worldview.domain.exception.JourneyAlreadyExistsException;
import pse.nebula.worldview.domain.exception.JourneyNotFoundException;
import pse.nebula.worldview.domain.exception.RouteNotFoundException;

import java.io.IOException;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

/**
 * Global exception handler for REST controllers.
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Handle IOException (e.g., "Broken pipe") that occurs when client disconnects during SSE streaming.
     * This is expected behavior when clients close their browser or navigate away.
     * We log it at debug level and return null to avoid further processing.
     */
    @ExceptionHandler(IOException.class)
    public ResponseEntity<Void> handleIOException(IOException ex) {
        if (isBrokenPipeException(ex)) {
            log.debug("Client disconnected (broken pipe): {}", ex.getMessage());
        } else {
            log.warn("IO error occurred: {}", ex.getMessage());
        }
        // Return null to prevent Spring from trying to write a response to a closed connection
        return null;
    }

    /**
     * Handle AsyncRequestNotUsableException that occurs when async request (like SSE) becomes unusable.
     * This happens when the client disconnects during streaming.
     */
    @ExceptionHandler(AsyncRequestNotUsableException.class)
    public ResponseEntity<Void> handleAsyncRequestNotUsable(AsyncRequestNotUsableException ex) {
        log.debug("Async request no longer usable (client disconnected): {}", ex.getMessage());
        return null;
    }

    @ExceptionHandler(RouteNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handleRouteNotFound(RouteNotFoundException ex) {
        log.warn("Route not found: {}", ex.getMessage());
        return buildErrorResponse(HttpStatus.NOT_FOUND, "ROUTE_NOT_FOUND", ex.getMessage());
    }

    @ExceptionHandler(JourneyNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handleJourneyNotFound(JourneyNotFoundException ex) {
        log.warn("Journey not found: {}", ex.getMessage());
        return buildErrorResponse(HttpStatus.NOT_FOUND, "JOURNEY_NOT_FOUND", ex.getMessage());
    }

    @ExceptionHandler(JourneyAlreadyExistsException.class)
    public ResponseEntity<Map<String, Object>> handleJourneyAlreadyExists(JourneyAlreadyExistsException ex) {
        log.warn("Journey already exists: {}", ex.getMessage());
        return buildErrorResponse(HttpStatus.CONFLICT, "JOURNEY_ALREADY_EXISTS", ex.getMessage());
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, Object>> handleIllegalArgument(IllegalArgumentException ex) {
        log.warn("Invalid argument: {}", ex.getMessage());
        return buildErrorResponse(HttpStatus.BAD_REQUEST, "INVALID_ARGUMENT", ex.getMessage());
    }

    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<Map<String, Object>> handleIllegalState(IllegalStateException ex) {
        log.warn("Invalid state: {}", ex.getMessage());
        return buildErrorResponse(HttpStatus.BAD_REQUEST, "INVALID_STATE", ex.getMessage());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidationErrors(MethodArgumentNotValidException ex) {
        Map<String, String> fieldErrors = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(error ->
            fieldErrors.put(error.getField(), error.getDefaultMessage())
        );

        Map<String, Object> response = new HashMap<>();
        response.put("timestamp", Instant.now().toString());
        response.put("status", HttpStatus.BAD_REQUEST.value());
        response.put("error", "VALIDATION_ERROR");
        response.put("message", "Validation failed");
        response.put("field_errors", fieldErrors);

        return ResponseEntity.badRequest().body(response);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleGenericException(Exception ex) {
        log.error("Unexpected error occurred", ex);
        return buildErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, "INTERNAL_ERROR",
            "An unexpected error occurred. Please try again later.");
    }

    private ResponseEntity<Map<String, Object>> buildErrorResponse(HttpStatus status, String error, String message) {
        Map<String, Object> response = new HashMap<>();
        response.put("timestamp", Instant.now().toString());
        response.put("status", status.value());
        response.put("error", error);
        response.put("message", message);

        return ResponseEntity.status(status).body(response);
    }

    /**
     * Check if the exception is a "Broken pipe" or similar connection closed exception.
     * These are common when clients disconnect during SSE streaming.
     */
    private boolean isBrokenPipeException(IOException ex) {
        String message = ex.getMessage();
        if (message == null) {
            return false;
        }
        String lowerMessage = message.toLowerCase();
        return lowerMessage.contains("broken pipe")
            || lowerMessage.contains("connection reset")
            || lowerMessage.contains("connection closed")
            || lowerMessage.contains("stream closed");
    }
}

