package com.jd.accounting.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value= HttpStatus.NOT_FOUND)
public class MovementNotFoundException extends RuntimeException {
    public MovementNotFoundException(String message) { super(message); }
}
