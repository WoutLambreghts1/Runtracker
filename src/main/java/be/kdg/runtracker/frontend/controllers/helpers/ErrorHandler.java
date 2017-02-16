package be.kdg.runtracker.frontend.controllers.helpers;

import be.kdg.runtracker.backend.exceptions.NoContentException;
import be.kdg.runtracker.backend.exceptions.NotFoundException;
import be.kdg.runtracker.backend.exceptions.UnauthorizedUserException;
import be.kdg.runtracker.backend.exceptions.UserNotFoundException;
import be.kdg.runtracker.frontend.util.CustomErrorType;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

/**
 * Helper class for all Controllers
 * Supplies all ExceptionHandler methodes
 */
@ControllerAdvice
public class ErrorHandler {

    @ExceptionHandler(UnauthorizedUserException.class)
    public ResponseEntity<?> processUnauthorizedUserException(UnauthorizedUserException exception) {
        return new ResponseEntity(new CustomErrorType(exception.getMessage()), HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<?> processUserNotFoundException(UserNotFoundException exception) {
        return new ResponseEntity(new CustomErrorType(exception.getMessage()), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(NoContentException.class)
    public ResponseEntity<?> processNoContentFoundException(NoContentException exception) {
        return new ResponseEntity(new CustomErrorType(exception.getMessage()), HttpStatus.NO_CONTENT);
    }

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<?> processNoContentFoundException(NotFoundException exception) {
        return new ResponseEntity(new CustomErrorType(exception.getMessage()), HttpStatus.NOT_FOUND);
    }

}
