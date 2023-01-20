package com.jd.accounting.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jd.accounting.model.Account;
import com.jd.accounting.model.Category;
import com.jd.accounting.model.security.Role;
import com.jd.accounting.model.security.User;
import com.jd.accounting.repositories.ResourceRepository;
import com.jd.accounting.services.AccountService;
import com.jd.accounting.services.CategoryService;
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
import java.util.HashSet;
import java.util.Set;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
@DirtiesContext(classMode= DirtiesContext.ClassMode.AFTER_CLASS)
class CategoryControllerTest {
    @Autowired
    MockMvc mockMvc;
    @MockBean
    UserService userService;
    @MockBean
    CategoryService categoryService;

    private static ObjectMapper mapper = new ObjectMapper();

    User userUser = new User();
    User userAdmin = new User();
    Category category1 = new Category();
    Category category2 = new Category();

    @BeforeEach
    void prepareData() {
        userUser.setUsername("Guest");
        userUser.setRoles(new ArrayList<>());
        userUser.getRoles().add(new Role(1, "ROLE_USER"));

        userAdmin.setUsername("Admin");
        userAdmin.setRoles(new ArrayList<>());
        userAdmin.getRoles().add(new Role(2, "ROLE_ADMIN"));

        category1.setName("New Category");
        category1.setUser(userUser);

        category2.setName("Category2");
        category2.setUser(userAdmin);
    }

    @Test
    @WithMockUser
    void newCategory() throws Exception {
        Mockito.when(userService.getCurrentUser()).thenReturn(userUser);
        Mockito.when(categoryService.create(Mockito.any(), Mockito.any())).thenReturn(category1);

        // test nominal
        mockMvc.perform(post("/categories")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(category1))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("name", is(category1.getName())));

        // category créée par Admin
        Mockito.when(userService.getCurrentUser()).thenReturn(userAdmin);
        Mockito.when(userService.loadUserByUsername(eq("Guest"))).thenReturn(userUser);
        mockMvc.perform(post("/categories")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(category1))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("name", is(category1.getName())));
    }

    @Test
    @WithMockUser
    void deleteCategory() throws Exception {
        doNothing().when(categoryService).deleteById(Mockito.any());

        // Test nominal
        Mockito.when(userService.getCurrentUser()).thenReturn(userUser);
        Mockito.when(categoryService.findById(1L)).thenReturn(category1);
        mockMvc.perform(delete("/categories/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        // Delete interdit par ROLE_USER
        Mockito.when(userService.getCurrentUser()).thenReturn(userUser);
        Mockito.when(categoryService.findById(2L)).thenReturn(category2);
        mockMvc.perform(delete("/categories/2")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("message", is(ResourceRepository.getResource("jd.exception.resourceauthorization", userUser.getUsername()))));

        // Delete par un admin
        Mockito.when(userService.getCurrentUser()).thenReturn(userAdmin);
        Mockito.when(userService.getCurrentUser()).thenReturn(userUser);
        Mockito.when(categoryService.findById(1L)).thenReturn(category1);
        mockMvc.perform(delete("/categories/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

    }

    @Test
    @WithMockUser
    void getCategoriesTest() throws Exception {
        Mockito.when(userService.getCurrentUser()).thenReturn(userUser);
        Set categories = new HashSet<Category>();
        categories.add(category1);
        categories.add(category2);
        Mockito.when(categoryService.findByUser(Mockito.any())).thenReturn(categories);

        // test nominal
        mockMvc.perform(get("/categories")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)));
    }
}