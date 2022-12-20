package com.jd.accounting.services;

import com.jd.accounting.exceptions.DuplicateSubcategoryForCategoryException;
import com.jd.accounting.model.Account;
import com.jd.accounting.model.Category;
import com.jd.accounting.model.Subcategory;
import com.jd.accounting.model.security.User;
import com.jd.accounting.repositories.SubcategoryRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.annotation.DirtiesContext;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ExtendWith(MockitoExtension.class)
@DirtiesContext(classMode= DirtiesContext.ClassMode.AFTER_CLASS)
class SubcategoryServiceImplTest {

    @MockBean
    SubcategoryRepository subcategoryRepository;
    @Autowired
    SubcategoryServiceImpl subcategoryService;

    User user = new User();
    Category category = new Category();
    Subcategory subcategory = new Subcategory();
    Set<Subcategory> subcategories = new HashSet<>();

    @BeforeEach
    void prepareData() {

        user.setUsername("Guest");

        category.setUser(user);
        category.setName("Categorie");

        subcategory.setCategory(category);
        subcategory.setName("SubCat");

        subcategories.add(subcategory);
    }

    @Test
    void findByCategory() {
        // Rien à tester
    }

    @Test
    void create() {
        Mockito.when(subcategoryRepository.save(Mockito.any(Subcategory.class))).thenAnswer(invocation -> invocation.getArgument(0, Subcategory.class));
        Mockito.when(subcategoryRepository.findByCategory(Mockito.any(Category.class))).thenReturn(subcategories);

        // Test nominal
        Subcategory newSubcategory = subcategoryService.create(category, "New Subcategory");
        assertEquals(newSubcategory.getName(), "New Subcategory");
        assertEquals(newSubcategory.getCategory(), category);

        // Subcategory déjà existante
        assertThrows(DuplicateSubcategoryForCategoryException.class, () -> subcategoryService.create(category, "SubCat"));
    }

    @Test
    void deleteById() {
        // Rien à tester
    }

    @Test
    void findByCategoryAndName() {
        Mockito.when(subcategoryService.findByCategory(Mockito.any(Category.class))).thenReturn(subcategories);

        assertEquals(subcategoryService.findByCategoryAndName(category, "SubCat"), true);
        assertEquals(subcategoryService.findByCategoryAndName(category, "Non existing"), false);
    }
}