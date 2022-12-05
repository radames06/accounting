package com.jd.accounting.repositories;

import com.jd.accounting.model.Movement;
import org.springframework.data.repository.CrudRepository;

public interface MovementRepository extends CrudRepository<Movement, Long> {
}
