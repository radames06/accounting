package com.jd.accounting.services;

import com.jd.accounting.exceptions.AccountNotFoundException;
import com.jd.accounting.exceptions.DuplicateAccountForUserException;
import com.jd.accounting.exceptions.FileReaderException;
import com.jd.accounting.model.Account;
import com.jd.accounting.model.Category;
import com.jd.accounting.model.Subcategory;
import com.jd.accounting.model.security.User;
import com.jd.accounting.repositories.AccountRepository;
import com.jd.accounting.repositories.ResourceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Service
public class AccountServiceImpl implements AccountService {

    @Autowired
    private AccountRepository accountRepository;


//    public AccountServiceImpl(AccountRepository accountRepository) {
//        this.accountRepository = accountRepository;
//    }

    @Override
    public Set<Account> findAll() {
        return accountRepository.findAll();
    }

    public Account findByNameAndUser(String accountName, User user) {
        return accountRepository.findByNameAndUser(accountName, user)
                .orElseThrow(() -> new AccountNotFoundException(ResourceRepository.getResource("jd.exception.accountnotfound", accountName)));
    }

    @Override
    public Account create(User user, String name, float initial) {
        try {
            findByNameAndUser(name, user);
            throw new DuplicateAccountForUserException(ResourceRepository.getResource("jd.exception.duplicateaccountforuser", name, user.getUsername()));
        } catch(AccountNotFoundException e){
            Account account = new Account();
            account.setName(name);
            account.setInitial(initial);
            account.setUser(user);
            account.setBalance(initial);
            return accountRepository.save(account);
        }
    }

    @Override
    public Account findById(Long id) {
        return accountRepository.findById(id)
                .orElseThrow(() -> new AccountNotFoundException(ResourceRepository.getResource("jd.exception.accountnotfound", String.valueOf(id))));
    }

    @Override
    public Set<Account> userAccounts(User user) {

        return accountRepository.findByUser(user);
    }

    @Override
    public void deleteById(Long id) {
        try {
            accountRepository.deleteById(id);
        } catch(EmptyResultDataAccessException ex) {
            throw new AccountNotFoundException(ResourceRepository.getResource("jd.exception.accountnotfound", String.valueOf(id)));
        }
    }

}
