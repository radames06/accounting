package com.jd.accounting.repositories;

import com.jd.accounting.model.Account;
import com.jd.accounting.model.security.User;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;
import java.util.Set;

public interface AccountRepository extends CrudRepository<Account, Long> {
    Set<Account> findByUser(User user);
    Set<Account> findAll();

    Optional<Account> findByNameAndUser(String accountName, User user);

    void deleteById(Long id);
}
