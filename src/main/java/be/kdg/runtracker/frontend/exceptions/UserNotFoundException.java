package be.kdg.runtracker.frontend.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Created by Alexander on 5/02/17.
 */
@ResponseStatus(HttpStatus.NOT_FOUND)
public class UserNotFoundException extends RuntimeException {

    public UserNotFoundException(String userId) {
        super("Could not find user with user id: '" + userId + "'.");
    }

}