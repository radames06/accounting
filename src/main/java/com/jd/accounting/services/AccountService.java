package com.jd.accounting.services;

import com.jd.accounting.model.Account;
import com.jd.accounting.model.security.User;

import java.util.Optional;
import java.util.Set;

public interface AccountService {
    Set<Account> findAll();
    Account create(User user, String name, float initial);
    Account findById(Long id);
    Set<Account> userAccounts(User user);

    Account findByNameAndUser(String accountName, User user);

    void deleteById(Long id);
}
