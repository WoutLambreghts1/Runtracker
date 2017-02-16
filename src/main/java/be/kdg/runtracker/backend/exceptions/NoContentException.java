package be.kdg.runtracker.backend.exceptions;

public class NoContentException extends RuntimeException {

    /**
     * This exception is thrown when there is no content.
     * @param message Message describing the exception.
     */
    public NoContentException(String message) {
        super(message);
    }

}
