package com.jd.accounting.services;


import com.jd.accounting.model.Category;
import com.jd.accounting.model.Subcategory;

import java.util.Set;

public interface SubcategoryService {
    public Set<Subcategory> findByCategory(Category category);
    public Subcategory create(Category category, String name);
    public void deleteById(Long id);
    public Subcategory findById(Long id);
}
