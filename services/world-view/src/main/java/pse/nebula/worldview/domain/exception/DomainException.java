package pse.nebula.worldview.domain.exception;

/**
 * Base exception for all domain-level exceptions in the world-view service.
 * All domain-specific exceptions should extend this class.
 */
public abstract class DomainException extends RuntimeException {

    protected DomainException(String message) {
        super(message);
    }

    protected DomainException(String message, Throwable cause) {
        super(message, cause);
    }
}
