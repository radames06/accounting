package com.jd.accounting.controllers;

import com.jd.accounting.model.Account;
import com.jd.accounting.model.Movement;
import com.jd.accounting.model.security.User;
import com.jd.accounting.repositories.ResourceRepository;
import com.jd.accounting.services.AccountService;
import com.jd.accounting.services.MovementService;
import com.jd.accounting.services.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AuthorizationServiceException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.annotation.security.RolesAllowed;
import java.util.List;
import java.util.Set;

@RestController
public class MovementController {

    private final MovementService movementService;
    private final AccountService accountService;
    private final UserService userService;
    private final ResourceRepository resourceRepository;

    public MovementController(MovementService movementService, AccountService accountService, UserService userService, ResourceRepository resourceRepository) {
        this.movementService = movementService;
        this.accountService = accountService;
        this.userService = userService;
        this.resourceRepository = resourceRepository;
    }

    @RolesAllowed({"ROLE_USER", "ROLE_ADMIN"})
    @GetMapping("/accounts/{id}/movements")
    ResponseEntity<Set> listMovementsByAccount(@PathVariable(value="id") Long accountId) {

        Account account = accountService.findById(accountId);
        User user = userService.getCurrentUser();
        if (user.equals(account.getUser())) {
            return ResponseEntity.ok(movementService.findByAccount(account));
        } else {
            throw new AuthorizationServiceException(resourceRepository.getResource("jd.exception.accountbelongs", accountId.toString(), user.getUsername()));
        }
    }

    @RolesAllowed({"ROLE_USER", "ROLE_ADMIN"})
    @PostMapping("/accounts/{id}/movements")
    ResponseEntity<Movement> newMovement(@PathVariable(value="id") Long accountId, @RequestBody Movement newMovement) {

        Account account = accountService.findById(accountId);
        User user = userService.getCurrentUser();

        if (user.equals(account.getUser())) {
            return ResponseEntity.ok(movementService.create(account, newMovement));
        } else {
            throw new AuthorizationServiceException(resourceRepository.getResource("jd.exception.accountbelongs", accountId.toString(), user.getUsername()));
        }
    }
}
