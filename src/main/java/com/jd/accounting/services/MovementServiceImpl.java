package com.jd.accounting.services;

import com.jd.accounting.exceptions.MovementNotFoundException;
import com.jd.accounting.model.Account;
import com.jd.accounting.model.Movement;
import com.jd.accounting.repositories.MovementRepository;
import com.jd.accounting.repositories.ResourceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;

@Service
public class MovementServiceImpl implements MovementService {

    @Autowired
    private MovementRepository movementRepository;

    @Override
    public Set<Movement> findByAccount(Account account) {
        Set<Movement> movementSet = new HashSet<>();
        account.getMovements().iterator().forEachRemaining(movementSet::add);
        return movementSet;
    }

    @Override
    public Movement create(Account account, Movement movement) {

        movement.setAccount(account);
        account.movement(movement.getAmount());
        return movementRepository.save(movement);
    }

    @Override
    public void delete(Movement movement) {
        movement.getAccount().movement(- movement.getAmount());
        movementRepository.delete(movement);
    }

    @Override
    public Movement findById(Long id) {
        return movementRepository.findById(id)
                .orElseThrow(() -> new MovementNotFoundException(ResourceRepository.getResource("jd.exception.movementnotfound", id.toString())));
    }

    // TODO : JUnit
    @Override
    public Movement update(Movement movement) {
        Movement oldMovement = findById(movement.getId());
        movement.getAccount().movement(movement.getAmount() - oldMovement.getAmount());
        return movementRepository.save(movement);
    }
}
