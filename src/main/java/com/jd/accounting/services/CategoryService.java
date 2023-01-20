package com.jd.accounting.services;

import com.jd.accounting.model.Account;
import com.jd.accounting.model.Category;
import com.jd.accounting.model.security.User;

import java.util.Set;

public interface CategoryService {
    Set<Category> findByUser(User user);
    Category create(User user, String name);
    void deleteById(Long id);
    Category findById(Long id);
}
