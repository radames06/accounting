package com.jd.accounting.services;

import com.jd.accounting.model.Account;
import com.jd.accounting.model.Movement;

import java.util.Optional;
import java.util.Set;

public interface MovementService {

    Set<Movement> findByAccount(Account account);
    Movement findById(Long id);
    Movement create(Account account, Movement movement);
    void delete(Movement movement);

    Movement update(Movement movement);
}
