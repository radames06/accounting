package com.jd.accounting.services;

import com.jd.accounting.exceptions.AccountNotFoundException;
import com.jd.accounting.exceptions.DuplicateSubcategoryForCategoryException;
import com.jd.accounting.exceptions.SubcategoryNotFoundException;
import com.jd.accounting.model.Category;
import com.jd.accounting.model.Subcategory;
import com.jd.accounting.repositories.ResourceRepository;
import com.jd.accounting.repositories.SubcategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
public class SubcategoryServiceImpl implements SubcategoryService {

    @Autowired
    SubcategoryRepository subcategoryRepository;

    @Override
    public Set<Subcategory> findByCategory(Category category) {
        return subcategoryRepository.findByCategory(category);
    }

    @Override
    public Subcategory create(Category category, String name) {
        if (findByCategoryAndName(category, name)) {
            throw new DuplicateSubcategoryForCategoryException(ResourceRepository.getResource("jd.exception.duplicatesubcategoryforcategory", name, category.getName()));
        } else {
            Subcategory subcategory = new Subcategory();
            subcategory.setName(name);
            subcategory.setCategory(category);
            return subcategoryRepository.save(subcategory);
        }
    }

    @Override
    public void deleteById(Long id) {
        try {
            subcategoryRepository.deleteById(id);
        } catch (EmptyResultDataAccessException ex) {
            throw new SubcategoryNotFoundException(ResourceRepository.getResource("jd.exception.subcategorynotfound", id.toString(), null));
        }
    }

    public boolean findByCategoryAndName(Category category, String name) {
        return findByCategory(category).stream().filter(subcategory -> name.equals(subcategory.getName())).findFirst().orElse(null) != null;
    }

    @Override
    public Subcategory findById(Long id) {
        return subcategoryRepository.findById(id)
                .orElseThrow(() -> new SubcategoryNotFoundException(ResourceRepository.getResource("jd.exception.subcategorynotfound", String.valueOf(id))));
    }
}
