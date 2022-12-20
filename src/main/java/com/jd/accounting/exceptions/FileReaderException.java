package com.jd.accounting.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value= HttpStatus.NOT_FOUND)
public class FileReaderException extends RuntimeException {
    public FileReaderException(String message) { super(message);}
}
