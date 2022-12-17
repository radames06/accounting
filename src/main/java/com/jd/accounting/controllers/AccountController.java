package com.jd.accounting.controllers;

import com.jd.accounting.model.Account;
import com.jd.accounting.model.security.User;
import com.jd.accounting.repositories.ResourceRepository;
import com.jd.accounting.services.AccountService;
import com.jd.accounting.services.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AuthorizationServiceException;
import org.springframework.web.bind.annotation.*;

import javax.annotation.security.RolesAllowed;
import java.util.Set;

// TODO : Utiliser les ResponseEntity et gérer les erreurs
@RestController
public class AccountController {

    private final AccountService accountService;
    private final UserService userService;
    private final ResourceRepository resourceRepository;

    public AccountController(AccountService accountService, UserService userService, ResourceRepository resourceRepository) {
        this.accountService = accountService;
        this.userService = userService;
        this.resourceRepository = resourceRepository;
    }

    @GetMapping("/accounts")
    @RolesAllowed({"ROLE_USER", "ROLE_ADMIN"})
    ResponseEntity<Set> listAccounts() {
        return ResponseEntity.ok(accountService.userAccounts(userService.getCurrentUser()));
    }

    @PostMapping("/accounts")
    @RolesAllowed({"ROLE_USER", "ROLE_ADMIN"})
    ResponseEntity<Account> newAccount(@RequestBody Account account) {
        return ResponseEntity.ok(accountService.create(userService.getCurrentUser(), account.getName(), account.getInitial()));
    }

    @GetMapping("/admin/accounts")
    @RolesAllowed("ROLE_ADMIN")
    ResponseEntity<Set> listAllAccounts() {

        return ResponseEntity.ok(accountService.findAll());
    }

    @DeleteMapping("/accounts/{id}")
    ResponseEntity<Void> deleteAccount(@PathVariable(value="id") Long accountId) {

        Account account = accountService.findById(accountId);
        User user = userService.getCurrentUser();

        if (user.equals(account.getUser())) {
            accountService.deleteById(accountId);
            return ResponseEntity.ok().build();
        } else {
            throw new AuthorizationServiceException(resourceRepository.getResource("jd.exception.accountbelongs", account.getId().toString(), user.getUsername()));
        }

    }
}
