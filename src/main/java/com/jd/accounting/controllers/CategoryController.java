package com.jd.accounting.controllers;

import com.jd.accounting.model.Account;
import com.jd.accounting.model.Category;
import com.jd.accounting.model.security.User;
import com.jd.accounting.repositories.ResourceRepository;
import com.jd.accounting.services.AccountService;
import com.jd.accounting.services.CategoryService;
import com.jd.accounting.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AuthorizationServiceException;
import org.springframework.web.bind.annotation.*;

import javax.annotation.security.RolesAllowed;
import java.util.Set;

@RestController
public class CategoryController {

    @Autowired
    CategoryService categoryService;
    @Autowired
    AccountService accountService;
    @Autowired
    UserService userService;

    @PostMapping("/categories")
    @RolesAllowed({"ROLE_USER", "ROLE_ADMIN"})
    ResponseEntity<Category> newCategory(@RequestBody Category category) {

        User user = userService.getCurrentUser();
        if (category.getUser() == null || user.getUsername().equals(category.getUser().getUsername())) {
            return ResponseEntity.ok(categoryService.create(user, category.getName()));
        } else if ( user.getStringRoles().contains("ROLE_ADMIN") && category.getUser() != null) {
            return ResponseEntity.ok(categoryService.create(userService.loadUserByUsername(category.getUser().getUsername()), category.getName()));
        } else {
            throw new AuthorizationServiceException(ResourceRepository.getResource("jd.exception.resourceauthorization", user.getUsername()));
        }
    }

    @GetMapping("/categories")
    @RolesAllowed({"ROLE_USER", "ROLE_ADMIN"})
    ResponseEntity<Set> getCategories() {
        return ResponseEntity.ok(categoryService.findByUser(userService.getCurrentUser()));
    }

    @DeleteMapping("/categories/{categoryId}")
    @RolesAllowed({"ROLE_USER", "ROLE_ADMIN"})
    ResponseEntity<Void> deleteCategory(@PathVariable(value = "categoryId") Long categoryId) {

        User user = userService.getCurrentUser();
        Category category = categoryService.findById(categoryId);

        if (user == category.getUser() || user.getStringRoles().contains("ROLE_ADMIN")) {
            categoryService.deleteById(categoryId);
            return ResponseEntity.ok().build();
        } else {
            throw new AuthorizationServiceException(ResourceRepository.getResource("jd.exception.resourceauthorization", user.getUsername()));
        }

    }
}
