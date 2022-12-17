package com.jd.accounting.exceptions;

import com.jd.accounting.model.security.User;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value= HttpStatus.CONFLICT) //, reason="Duplicate user")
public class DuplicateAccountForUser extends RuntimeException {
    public DuplicateAccountForUser(String message) {
        super(message);
    }
}
