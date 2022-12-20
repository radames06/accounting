package com.jd.accounting.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value= HttpStatus.CONFLICT)
public class DuplicateSubcategoryForCategoryException extends RuntimeException{
    public DuplicateSubcategoryForCategoryException(String message) { super(message);}
}
