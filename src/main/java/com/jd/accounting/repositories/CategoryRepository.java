package com.jd.accounting.repositories;

import com.jd.accounting.model.Account;
import com.jd.accounting.model.Category;
import com.jd.accounting.model.security.User;
import org.springframework.data.repository.CrudRepository;

import java.util.Set;

public interface CategoryRepository extends CrudRepository<Category, Long> {
    Set<Category> findByUser(User user);

}
