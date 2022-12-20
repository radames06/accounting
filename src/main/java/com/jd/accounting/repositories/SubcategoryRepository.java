package com.jd.accounting.repositories;

import com.jd.accounting.model.Category;
import com.jd.accounting.model.Subcategory;
import org.springframework.data.repository.CrudRepository;

import java.util.Set;

public interface SubcategoryRepository extends CrudRepository<Subcategory, Long> {
    Set<Subcategory> findByCategory(Category category);
}
