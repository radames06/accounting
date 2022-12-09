package com.jd.accounting.services;

import com.jd.accounting.model.Account;
import com.jd.accounting.model.Movement;
import com.jd.accounting.repositories.AccountRepository;
import com.jd.accounting.repositories.MovementRepository;
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
    public Set<Movement> findByAccount(Account account) {
        Set<Movement> movementSet = new HashSet<>();
        account.getMovements().iterator().forEachRemaining(movementSet::add);
        return movementSet;
    }

    @Override
    public Movement create(Account account, Movement movement) {

        movement.setAccount(account);
        return movementRepository.save(movement);
    }
}
