package be.kdg.runtracker.backend.exceptions;

public class UserNotFoundException extends RuntimeException {

    /**
     * This exception is thrown when a {@link be.kdg.runtracker.backend.dom.profile.User} is not found.
     * @param message Message describing the exception.
     */
    public UserNotFoundException(String message) {
        super(message);
    }

}
