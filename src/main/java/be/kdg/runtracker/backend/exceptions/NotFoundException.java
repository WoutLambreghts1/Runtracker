package be.kdg.runtracker.backend.exceptions;

/**
 * Created by Alexander on 16/02/17.
 */
public class NotFoundException extends RuntimeException {

    /**
     * This exception is thrown when no content is found.
     * @param message
     */
    public NotFoundException(String message) {
        super(message);
    }

}
