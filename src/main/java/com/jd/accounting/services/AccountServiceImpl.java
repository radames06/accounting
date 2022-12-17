package com.jd.accounting.services;

import com.jd.accounting.exceptions.AccountNotFoundException;
import com.jd.accounting.exceptions.DuplicateAccountForUser;
import com.jd.accounting.model.Account;
import com.jd.accounting.model.security.User;
import com.jd.accounting.repositories.AccountRepository;
import com.jd.accounting.repositories.ResourceRepository;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
public class AccountServiceImpl implements AccountService {

    private final AccountRepository accountRepository;
    private final ResourceRepository resourceRepository;

    public AccountServiceImpl(AccountRepository accountRepository, ResourceRepository resourceRepository) {

        this.accountRepository = accountRepository;
        this.resourceRepository = resourceRepository;
    }

    @Override
    public Set<Account> findAll() {
        return accountRepository.findAll();
    }

    public Account findByNameAndUser(String accountName, User user) {
        return accountRepository.findByNameAndUser(accountName, user)
                .orElseThrow(() -> new AccountNotFoundException(resourceRepository.getResource("jd.exception.accountnotfound", accountName)));
    }

    @Override
    public Account create(User user, String name, float initial) {
        try {
            findByNameAndUser(name, user);
            throw new DuplicateAccountForUser(resourceRepository.getResource("jd.exception.duplicateaccountforuser", name, user.getUsername()));
        } catch(AccountNotFoundException e){
            Account account = new Account();
            account.setName(name);
            account.setInitial(initial);
            account.setUser(user);
            return accountRepository.save(account);
        }
    }

    @Override
    public Account findById(Long id) {
        return accountRepository.findById(id)
                .orElseThrow(() -> new AccountNotFoundException(resourceRepository.getResource("jd.exception.accountnotfound", String.valueOf(id))));
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
            throw new AccountNotFoundException(resourceRepository.getResource("jd.exception.accountnotfound", String.valueOf(id)));
        }
    }
}
