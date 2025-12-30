package pse.nebula.worldview.domain.exception;

/**
 * Exception thrown when a journey cannot be found by its ID.
 */
public class JourneyNotFoundException extends DomainException {

    public JourneyNotFoundException(String journeyId) {
        super("Journey not found with ID: " + journeyId);
    }

    public JourneyNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
