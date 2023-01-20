package com.jd.accounting.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jd.accounting.model.Account;
import com.jd.accounting.model.Category;
import com.jd.accounting.model.Subcategory;
import com.jd.accounting.model.security.Role;
import com.jd.accounting.model.security.User;
import com.jd.accounting.repositories.ResourceRepository;
import com.jd.accounting.services.AccountService;
import com.jd.accounting.services.CategoryService;
import com.jd.accounting.services.SubcategoryService;
import com.jd.accounting.services.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;

import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.doNothing;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
@DirtiesContext(classMode= DirtiesContext.ClassMode.AFTER_CLASS)
class SubcategoryControllerTest {
    @Autowired
    MockMvc mockMvc;
    @MockBean
    UserService userService;
    @MockBean
    CategoryService categoryService;
    @MockBean
    SubcategoryService subcategoryService;

    private static ObjectMapper mapper = new ObjectMapper();
    User userUser = new User();
    User userAdmin = new User();
    Category category = new Category();
    Category category2 = new Category();
    Subcategory subcategory = new Subcategory();
    Subcategory subcategory2 = new Subcategory();
    Subcategory newSubCat = new Subcategory();

    @BeforeEach
    void prepareData() {
        userUser.setUsername("Guest");
        userUser.setRoles(new ArrayList<>());
        userUser.getRoles().add(new Role(1, "ROLE_USER"));

        userAdmin.setUsername("Admin");
        userAdmin.setRoles(new ArrayList<>());
        userAdmin.getRoles().add(new Role(2, "ROLE_ADMIN"));

        category.setName("New Category");
        category.setUser(userUser);
        category.setId(1L);
        subcategory.setName("New Subcategory");
        subcategory.setId(1L);
        subcategory.setCategory(category);

        category2.setName("Category for account2");
        category2.setUser(userAdmin);
        category2.setId(2L);
        subcategory2.setName("New Subcategory2");
        subcategory2.setId(2L);
        subcategory2.setCategory(category2);

        subcategory.setName("New Subcategory");
    }

    @Test
    @WithMockUser
    void newSubcategoryTest() throws Exception {

        Mockito.when(categoryService.findById(1L)).thenReturn(category);
        Mockito.when(categoryService.findById(2L)).thenReturn(category2);
        Mockito.when(subcategoryService.create(Mockito.any(), Mockito.any())).thenReturn(newSubCat);

        // Test nominal
        Mockito.when(userService.getCurrentUser()).thenReturn(userUser);
        mockMvc.perform(post("/categories/1/subcategories")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(newSubCat))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("name", is(newSubCat.getName())));

        // categorie n'appartient pas au user
        mockMvc.perform(post("/categories/2/subcategories")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(newSubCat))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("message", is(ResourceRepository.getResource("jd.exception.resourceauthorization", userUser.getUsername()))));

        // categorie créée par un admin
        Mockito.when(userService.getCurrentUser()).thenReturn(userAdmin);
        mockMvc.perform(post("/categories/1/subcategories")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(newSubCat))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("name", is(newSubCat.getName())));

    }

    @Test
    @WithMockUser
    void deleteSubcategoryTest() throws Exception {
        Mockito.when(categoryService.findById(1L)).thenReturn(category);
        Mockito.when(categoryService.findById(2L)).thenReturn(category2);
        Mockito.when(subcategoryService.findById(1L)).thenReturn(subcategory);
        Mockito.when(subcategoryService.findById(2L)).thenReturn(subcategory2);
        doNothing().when(subcategoryService).deleteById(Mockito.any());

        // Test nominal
        Mockito.when(userService.getCurrentUser()).thenReturn(userUser);
        mockMvc.perform(delete("/categories/1/subcategories/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        // categorie n'appartient pas au user
        mockMvc.perform(delete("/categories/2/subcategories/2")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("message", is(ResourceRepository.getResource("jd.exception.resourceauthorization", userUser.getUsername()))));

        // subcategory n'appartient pas à la catégorie
        mockMvc.perform(delete("/categories/1/subcategories/2")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("message", is(ResourceRepository.getResource("jd.exception.subcategorynotfound", subcategory2.getId().toString(), category.getId().toString()))));

        // categorie créée par Admin
        Mockito.when(userService.getCurrentUser()).thenReturn(userAdmin);
        mockMvc.perform(delete("/categories/1/subcategories/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }
}