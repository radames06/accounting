package com.jd.accounting.controllers;

import com.jd.accounting.model.Account;
import com.jd.accounting.model.Movement;
import com.jd.accounting.services.AccountService;
import com.jd.accounting.services.MovementService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Set;

@RestController
public class MovementController {

    private final MovementService movementService;
    private final AccountService accountService;

    public MovementController(MovementService movementService, AccountService accountService) {
        this.movementService = movementService;
        this.accountService = accountService;
    }

    @PreAuthorize("hasAnyRole('ADMIN','MANAGER')")
    @GetMapping("/accounts/{id}/movements")
    Set<Movement> listMovementsByAccount(@PathVariable(value="id") Long accountId) {
        return movementService.findByAccount(accountId);
    }

    @PreAuthorize("hasAnyRole('ADMIN','MANAGER')")
    @PostMapping("/accounts/{id}/movements")
    Movement newMovement(@PathVariable(value="id") Long accountId, @RequestBody Movement newMovement) {
        Account account = accountService.findById(accountId);
        newMovement.setAccount(account);
        return movementService.create(newMovement);
    }
}
