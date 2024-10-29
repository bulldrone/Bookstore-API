package dxc.assessment.bookstore.error;

import dxc.assessment.bookstore.error.CustomError;
import dxc.assessment.bookstore.error.CustomExceptions;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.time.LocalDateTime;

@ControllerAdvice
public class CustomErrorHandler {

    @ExceptionHandler(CustomExceptions.InternalServerError.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ResponseEntity<CustomError> handleInternalServerError(CustomExceptions.InternalServerError ex) {
        CustomError error = new CustomError(LocalDateTime.now(), ex.getMessage(), "Internal Server Error Occurred");
        return new ResponseEntity<>(error, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(CustomExceptions.MissingJsonContentError.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ResponseEntity<CustomError> handleMissingJsonContentError(CustomExceptions.MissingJsonContentError ex) {
        CustomError error = new CustomError(LocalDateTime.now(), ex.getMessage(), "Missing Json body for Book");
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(CustomExceptions.MissingParamQueryError.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ResponseEntity<CustomError> handleMissingParamQueryError(CustomExceptions.MissingParamQueryError ex) {
        CustomError error = new CustomError(LocalDateTime.now(), ex.getMessage(), "Missing Param For DB Query");
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(CustomExceptions.UnauthorizedError.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public ResponseEntity<CustomError> handleUnauthorizedError(CustomExceptions.UnauthorizedError ex) {
        CustomError error = new CustomError(LocalDateTime.now(), ex.getMessage(), "Unauthorized Access");
        return new ResponseEntity<>(error, HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(CustomExceptions.AuthenticationError.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public ResponseEntity<CustomError> handleAuthenticationError(CustomExceptions.AuthenticationError ex) {
        CustomError error = new CustomError(LocalDateTime.now(), ex.getMessage(), "Authentication Error");
        return new ResponseEntity<>(error, HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler(CustomExceptions.BookNotFoundError.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ResponseEntity<CustomError> handleBookNotFoundError(CustomExceptions.BookNotFoundError ex) {
        CustomError error = new CustomError(LocalDateTime.now(), ex.getMessage(), "Book Not Found");
        return new ResponseEntity<>(error, HttpStatus.NOT_FOUND);
    }
}