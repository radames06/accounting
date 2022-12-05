package com.jd.accounting.services;

import com.jd.accounting.exceptions.AccountNotFoundException;
import com.jd.accounting.model.Account;
import com.jd.accounting.model.Movement;
import com.jd.accounting.model.security.UserPrincipal;
import com.jd.accounting.repositories.AccountRepository;
import com.jd.accounting.repositories.MovementRepository;
import org.springframework.security.access.AuthorizationServiceException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;

@Service
public class MovementServiceImpl implements MovementService {

    private final AccountRepository accountRepository;
    private final MovementRepository movementRepository;

    public MovementServiceImpl(MovementRepository movementRepository, AccountRepository accountRepository) {
        this.movementRepository = movementRepository;
        this.accountRepository = accountRepository;
    }

    @Override
    public Set<Movement> findByAccount(Long accountId) {
        Set<Movement> movementSet = new HashSet<>();
        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new AccountNotFoundException(accountId));

        // TODO : Mettre au niveau de la classe ?
        UserPrincipal principal = (UserPrincipal) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (principal.getUsername().equals(account.getUser().getUsername())) {
            account.getMovements().iterator().forEachRemaining(movementSet::add);
            return movementSet;
        } else {
            throw new AuthorizationServiceException("The account " + accountId + " does not belong to user id " + principal.getUsername());
        }

    }

    @Override
    public Movement create(Movement movement) {

        UserPrincipal principal = (UserPrincipal) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (principal.getUsername().equals(movement.getAccount().getUser().getUsername())) {
            return movementRepository.save(movement);
        } else {
            throw new AuthorizationServiceException("The account " + movement.getAccount().getId() + " does not belong to user id " + principal.getUsername());
        }

    }
}
