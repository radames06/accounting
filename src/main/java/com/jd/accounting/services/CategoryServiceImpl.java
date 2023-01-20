package com.jd.accounting.services;

import com.jd.accounting.exceptions.CategoryNotFoundException;
import com.jd.accounting.exceptions.DuplicateCategoryForUserException;
import com.jd.accounting.model.Category;
import com.jd.accounting.model.security.User;
import com.jd.accounting.repositories.CategoryRepository;
import com.jd.accounting.repositories.ResourceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
public class CategoryServiceImpl implements CategoryService {

    @Autowired
    CategoryRepository categoryRepository;

    @Override
    public Set<Category> findByUser(User user) {
        return categoryRepository.findByUser(user);
    }

    @Override
    public Category create(User user, String name) {
        if (findByUserAndName(user, name)) {
            throw new DuplicateCategoryForUserException(ResourceRepository.getResource("jd.exception.duplicatecategoryforuser", name, user.getUsername()));
        } else {
            Category category = new Category();
            category.setName(name);
            category.setUser(user);
            return categoryRepository.save(category);
        }
    }

    @Override
    public void deleteById(Long id) {
        try {
            categoryRepository.deleteById(id);
        } catch (EmptyResultDataAccessException ex) {
            throw new CategoryNotFoundException(ResourceRepository.getResource("jd.exception.categorynotfound", id.toString(), null));
        }
    }

    public boolean findByUserAndName(User user, String name) {
        return findByUser(user).stream().filter(category -> name.equals(category.getName())).findFirst().orElse(null) != null;
    }

    @Override
    public Category findById(Long id) {
        return categoryRepository.findById(id)
                .orElseThrow(() -> new CategoryNotFoundException(ResourceRepository.getResource("jd.exception.categorynotfound", String.valueOf(id))));
    }
}
