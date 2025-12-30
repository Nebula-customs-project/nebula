package pse.nebula.worldview.domain.exception;

/**
 * Exception thrown when attempting to create a journey that already exists.
 */
public class JourneyAlreadyExistsException extends DomainException {

    public JourneyAlreadyExistsException(String journeyId) {
        super("Journey already exists with ID: " + journeyId);
    }

    public JourneyAlreadyExistsException(String message, Throwable cause) {
        super(message, cause);
    }
}
