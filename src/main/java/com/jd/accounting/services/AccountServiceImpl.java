package com.jd.accounting.services;

import com.jd.accounting.exceptions.AccountNotFoundException;
import com.jd.accounting.model.Account;
import com.jd.accounting.model.security.User;
import com.jd.accounting.model.security.UserPrincipal;
import com.jd.accounting.repositories.AccountRepository;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;

@Service
public class AccountServiceImpl implements AccountService {

    private final AccountRepository accountRepository;
    //private final UserService userService;

    public AccountServiceImpl(AccountRepository accountRepository) {

        this.accountRepository = accountRepository;
        //this.userService = userService;
    }



    @Override
    public Set<Account> findAll() {
        Set<Account> accountSet = new HashSet<>();
        accountRepository.findAll().iterator().forEachRemaining(accountSet::add);

        return accountSet;
    }

    @Override
    public Account create(User user, String name, float initial) {
        Account account = new Account();
        account.setName(name);
        account.setInitial(initial);
        account.setUser(user);
        return accountRepository.save(account);
    }

    @Override
    public Account findById(Long id) {
        return accountRepository.findById(id)
                .orElseThrow(() -> new AccountNotFoundException(id));
    }

    @Override
    public Set<Account> userAccounts(User user) {
        return accountRepository.findByUser(user);
    }
}
