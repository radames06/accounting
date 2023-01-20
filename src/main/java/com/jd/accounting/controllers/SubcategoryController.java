package com.jd.accounting.controllers;

import com.jd.accounting.exceptions.CategoryNotFoundException;
import com.jd.accounting.exceptions.SubcategoryNotFoundException;
import com.jd.accounting.model.Category;
import com.jd.accounting.model.Subcategory;
import com.jd.accounting.model.security.User;
import com.jd.accounting.repositories.ResourceRepository;
import com.jd.accounting.services.AccountService;
import com.jd.accounting.services.CategoryService;
import com.jd.accounting.services.SubcategoryService;
import com.jd.accounting.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AuthorizationServiceException;
import org.springframework.web.bind.annotation.*;

import javax.annotation.security.RolesAllowed;

@RestController
public class SubcategoryController {

    @Autowired
    CategoryService categoryService;
    @Autowired
    UserService userService;
    @Autowired
    SubcategoryService subcategoryService;

    @PostMapping("/categories/{categoryId}/subcategories")
    @RolesAllowed({"ROLE_USER", "ROLE_ADMIN"})
    ResponseEntity<Subcategory> newSubcategory(@PathVariable(value = "categoryId") Long categoryId, @RequestBody Subcategory subcategory) {

        Category category = categoryService.findById(categoryId);
        User user = userService.getCurrentUser();

        if (user != category.getUser() && !user.getStringRoles().contains("ROLE_ADMIN")) {
            throw new AuthorizationServiceException(ResourceRepository.getResource("jd.exception.resourceauthorization", user.getUsername()));
        } else {
            Subcategory subC = subcategoryService.create(category, subcategory.getName());
            ResponseEntity<Subcategory> response = ResponseEntity.ok(subC);
            return response;
        }
    }

    @DeleteMapping("/categories/{categoryId}/subcategories/{subcategoryId}")
    @RolesAllowed({"ROLE_USER", "ROLE_ADMIN"})
    ResponseEntity<Void> deleteSubcategory(@PathVariable(value = "categoryId") Long categoryId, @PathVariable(value = "subcategoryId") Long subcategoryId) {

        Category category;
        User user = userService.getCurrentUser();
        try {
            category = categoryService.findById(categoryId);
        } catch (CategoryNotFoundException ex) {
            throw new CategoryNotFoundException(ResourceRepository.getResource("jd.exception.categorynotfound", categoryId.toString(), user.getUsername()));
        }
        Subcategory subcategory = subcategoryService.findById(subcategoryId);
        if (!user.equals(category.getUser()) && !user.getStringRoles().contains("ROLE_ADMIN")) {
            throw new AuthorizationServiceException(ResourceRepository.getResource("jd.exception.resourceauthorization", user.getUsername()));
        } else if (subcategory.getCategory() != category) {
            throw new SubcategoryNotFoundException(ResourceRepository.getResource("jd.exception.subcategorynotfound", subcategoryId.toString(), categoryId.toString()));
        } else {
            subcategoryService.deleteById(subcategoryId);
            return ResponseEntity.ok().build();
        }
    }
}
