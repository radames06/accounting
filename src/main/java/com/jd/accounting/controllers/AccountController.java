package com.jd.accounting.controllers;

import com.jd.accounting.model.Account;
import com.jd.accounting.services.AccountService;
import com.jd.accounting.services.UserService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;
import java.util.Set;

// TODO : Utiliser les ResponseEntity et gérer les erreurs
@RestController
public class AccountController {

    private final AccountService accountService;
    private final UserService userService;

    public AccountController(AccountService accountService, UserService userService) {
        this.accountService = accountService;
        this.userService = userService;
    }

    @PreAuthorize("hasRole('USER')")
    @GetMapping("/accounts")
    Set<Account> listAccounts(Principal principal) {
        Set<Account> accountSet = accountService.userAccounts(userService.getCurrentUser());
        // TODO : Traiter le cas du rôle ADMIN qui doit tout voir
        return accountService.userAccounts(userService.getCurrentUser());
    }

    @PreAuthorize("hasRole('USER')")
    @PostMapping("/accounts")
    Account newAccount(@RequestBody Account account) {
        return accountService.create(userService.getCurrentUser(), account.getName(), account.getInitial());
    }
}
