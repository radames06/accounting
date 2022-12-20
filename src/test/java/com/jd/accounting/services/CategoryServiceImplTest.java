package com.jd.accounting.services;

import com.jd.accounting.exceptions.DuplicateCategoryForUserException;
import com.jd.accounting.model.Account;
import com.jd.accounting.model.Category;
import com.jd.accounting.model.security.Role;
import com.jd.accounting.model.security.User;
import com.jd.accounting.repositories.CategoryRepository;
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
class CategoryServiceImplTest {

    @MockBean
    CategoryRepository categoryRepository;

    @Autowired
    CategoryServiceImpl categoryService;

    Account account1 = new Account();
    Category category = new Category();
    User user = new User();

    Set<Category> categories = new HashSet<>();

    @BeforeEach
    void prepareData() {
        user.setUsername("User");
        user.setRoles(new ArrayList<>());
        user.getRoles().add(new Role(1, "ROLE_USER"));

        account1.setId(1L);
        account1.setUser(user);
        account1.setName("CCP");
        account1.setInitial(0);
        account1.setBalance(0);
        account1.setMovements(new ArrayList<>());

        category.setUser(user);
        category.setName("Categorie");
        categories.add(category);
    }

    @Test
    void findByUserTest() {
        // Rien à tester
    }

    @Test
    void createTest() {
        Mockito.when(categoryRepository.save(Mockito.any(Category.class))).thenAnswer(invocation -> invocation.getArgument(0, Category.class));
        Mockito.when(categoryRepository.findByUser(Mockito.any(User.class))).thenReturn(categories);

        // Test nominal
        Category newCategory = categoryService.create(user, "New Category");
        assertEquals(newCategory.getName(), "New Category");
        assertEquals(newCategory.getUser(), user);

        // Categorie déjà existante
        assertThrows(DuplicateCategoryForUserException.class, () -> categoryService.create(user, "Categorie"));
    }

    @Test
    void deleteByIdTest() {
        // Rien à tester
    }

    @Test
    void findByUSerAndNameTest() {
        Mockito.when(categoryRepository.findByUser(Mockito.any(User.class))).thenReturn(categories);

        assertEquals(categoryService.findByUserAndName(user, "Categorie"), true);
        assertEquals(categoryService.findByUserAndName(user, "Non existing"), false);
    }

    @Test
    void findByIdTest() {
        // Rien à tester
    }
}