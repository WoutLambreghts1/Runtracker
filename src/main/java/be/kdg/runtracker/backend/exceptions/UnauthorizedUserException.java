package be.kdg.runtracker.backend.exceptions;

public class UnauthorizedUserException extends RuntimeException {

    /**
     * This exception is thrown a {@link be.kdg.runtracker.backend.dom.profile.User} makes an unauthorized call.
     * @param message Message describing the exception.
     */
    public UnauthorizedUserException(String message) {
        super(message);
    }

}
