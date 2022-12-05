package com.jd.accounting.exceptions;

public class AccountNotFoundException extends RuntimeException {
    public AccountNotFoundException(Long id) {
        super("Account not found : " + id.toString());
    }
}
