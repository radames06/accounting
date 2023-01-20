package com.jd.accounting.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AuthorizationServiceException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice
public class ExceptionHandlingController extends ResponseEntityExceptionHandler {

    @ExceptionHandler(AuthorizationServiceException.class)
    protected ResponseEntity<Object> handleAuthorizationServiceException(AuthorizationServiceException ex) {
        ApiError apiError = new ApiError(HttpStatus.FORBIDDEN);
        apiError.setMessage(ex.getMessage());

        return buildResponseEntity(apiError);
    }

    @ExceptionHandler({AccountNotFoundException.class, UserNotFoundException.class, MovementNotFoundException.class,
        CategoryNotFoundException.class, SubcategoryNotFoundException.class, FileReaderException.class})
    protected ResponseEntity<Object> handleAccountNotFoundException(RuntimeException ex) {
        ApiError apiError = new ApiError(HttpStatus.NOT_FOUND);
        apiError.setMessage(ex.getMessage());

        return buildResponseEntity(apiError);
    }

    @ExceptionHandler({DuplicateAccountForUserException.class, DuplicateCategoryForUserException.class, DuplicateSubcategoryForCategoryException.class})
    protected ResponseEntity<Object> handleDuplicateAccountForUserException(RuntimeException ex) {
        ApiError apiError = new ApiError(HttpStatus.CONFLICT);
        apiError.setMessage(ex.getMessage());

        return buildResponseEntity(apiError);
    }

    private ResponseEntity<Object> buildResponseEntity(ApiError apiError) {
        return new ResponseEntity<>(apiError, apiError.getStatus());
    }
}
