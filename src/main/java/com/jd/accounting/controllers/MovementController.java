package com.jd.accounting.controllers;

import com.jd.accounting.exceptions.MovementNotFoundException;
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

    public MovementController(MovementService movementService, AccountService accountService, UserService userService) {
        this.movementService = movementService;
        this.accountService = accountService;
        this.userService = userService;
    }

    @RolesAllowed({"ROLE_USER", "ROLE_ADMIN"})
    @GetMapping("/accounts/{id}/movements")
    ResponseEntity<Set> listMovementsByAccount(@PathVariable(value="id") Long accountId) {

        Account account = accountService.findById(accountId);
        User user = userService.getCurrentUser();
        if (user.equals(account.getUser()) || user.getStringRoles().contains("ROLE_ADMIN")) {
            return ResponseEntity.ok(movementService.findByAccount(account));
        } else {
            throw new AuthorizationServiceException(ResourceRepository.getResource("jd.exception.accountbelongs", accountId.toString(), user.getUsername()));
        }
    }

    @RolesAllowed({"ROLE_USER", "ROLE_ADMIN"})
    @PostMapping("/accounts/{id}/movements")
    ResponseEntity<Movement> newMovement(@PathVariable(value="id") Long accountId, @RequestBody Movement newMovement) {

        Account account = accountService.findById(accountId);
        User user = userService.getCurrentUser();

        if (user.equals(account.getUser()) || user.getStringRoles().contains("ROLE_ADMIN")) {
            return ResponseEntity.ok(movementService.create(account, newMovement));
        } else {
            throw new AuthorizationServiceException(ResourceRepository.getResource("jd.exception.accountbelongs", accountId.toString(), user.getUsername()));
        }
    }

    @RolesAllowed({"ROLE_USER", "ROLE_ADMIN"})
    @PutMapping("/accounts/{accountId}/movements/{movementId}")
    ResponseEntity<Movement> editMovement(@PathVariable(value="accountId") Long accountId, @PathVariable(value="movementId") Long movementId, @RequestBody Movement movement) {

        Account account = accountService.findById(accountId);
        User user = userService.getCurrentUser();

        Movement oldMovement;
        try {
            oldMovement = movementService.findById(movementId);
        } catch (MovementNotFoundException ex) {
            throw new MovementNotFoundException(ResourceRepository.getResource("jd.exception.movementnotfound", movementId.toString(), accountId.toString()));
        }
        if (oldMovement.getAccount().getId() != accountId) {
            throw new MovementNotFoundException(ResourceRepository.getResource("jd.exception.movementnotfound", movementId.toString(), accountId.toString()));
        } else if (user == account.getUser() || user.getStringRoles().contains("ROLE_ADMIN")) {
            oldMovement.setMovementDate(movement.getMovementDate());
            oldMovement.setTiers(movement.getTiers());
            oldMovement.setAmount(movement.getAmount());
            oldMovement.setAccount(account);
            return ResponseEntity.ok(movementService.update(oldMovement));
        } else {
            throw new AuthorizationServiceException(ResourceRepository.getResource("jd.exception.accountbelongs", accountId.toString(), user.getUsername()));
        }

    }

    @RolesAllowed({"ROLE_USER", "ROLE_ADMIN"})
    @DeleteMapping("/accounts/{accountId}/movements/{movementId}")
    ResponseEntity<Void> deleteMovement(@PathVariable(value="accountId") Long accountId, @PathVariable(value="movementId") Long movementId ) {
        User user = userService.getCurrentUser();
        Account account = accountService.findById(accountId);
        Movement movement;
        try {
            movement = movementService.findById(movementId);
        } catch (MovementNotFoundException ex) {
            throw new MovementNotFoundException(ResourceRepository.getResource("jd.exception.movementnotfound", movementId.toString(), accountId.toString()));
        }
        if (movement.getAccount().getId() != accountId) {
            throw new MovementNotFoundException(ResourceRepository.getResource("jd.exception.movementnotfound", movementId.toString(), accountId.toString()));
        } else if (user == account.getUser() || user.getStringRoles().contains("ROLE_ADMIN")) {
            movementService.delete(movement);
            return ResponseEntity.ok().build();
        } else {
            throw new AuthorizationServiceException(ResourceRepository.getResource("jd.exception.accountbelongs", accountId.toString(), user.getUsername()));
        }
    }
}
