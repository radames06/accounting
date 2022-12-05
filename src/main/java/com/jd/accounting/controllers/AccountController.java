package com.jd.accounting.controllers;

import com.jd.accounting.model.Account;
import com.jd.accounting.services.AccountService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;
import java.util.Set;

// TODO : Utiliser les ResponseEntity et gérer les erreurs
// TODO : JUNITs
@RestController
public class AccountController {

    private final AccountService accountService;

    public AccountController(AccountService accountService) {

        this.accountService = accountService;
    }

    @PreAuthorize("hasAnyRole('ADMIN','MANAGER')")
    @GetMapping("/accounts")
    Set<Account> listAccounts(Principal principal) {
        // TODO : Traiter le cas du rôle ADMIN qui doit tout voir
        // TODO : Gérer le cas d'un utilisateur non trouvé
        return accountService.currentUserAccounts();
    }

    @PreAuthorize("hasAnyRole('ADMIN','MANAGER')")
    @PostMapping("/accounts")
    Account newAccount(@RequestBody Account account) {
        return accountService.create(account.getName(), account.getInitial());
    }
}
