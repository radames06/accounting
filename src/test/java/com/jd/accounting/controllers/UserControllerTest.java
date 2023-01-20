package com.jd.accounting.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jd.accounting.model.security.Role;
import com.jd.accounting.model.security.User;
import com.jd.accounting.repositories.ResourceRepository;
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
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
@DirtiesContext(classMode= DirtiesContext.ClassMode.AFTER_CLASS)
class UserControllerTest {

    @Autowired
    MockMvc mockMvc;

    @MockBean
    UserService userService;

//    @Autowired
//    UserController userController;

    private static ObjectMapper mapper = new ObjectMapper();

    User user1 = new User();
    User user2 = new User();
    User user3 = new User();
    Set<User> userSet;

    @BeforeEach
    void prepareData() {
        user1.setUsername("Guest1");
        user1.setRoles(new ArrayList<>());
        user1.getRoles().add(new Role(1, "ROLE_USER"));

        user2.setUsername("Guest2");
        user2.setRoles(new ArrayList<>());
        user2.getRoles().add(new Role(1, "ROLE_USER"));

        user3.setUsername("Admin");
        user3.setRoles(new ArrayList<>());
        user3.getRoles().add(new Role(1, "ROLE_ADMIN"));

        userSet = new HashSet<>(Set.of(user1, user2, user3));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void listUsersTest() throws Exception {

        Mockito.when(userService.findAll()).thenReturn(userSet);

        mockMvc.perform(MockMvcRequestBuilders
                        .get("/users")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(3)));
    }

    @Test
    void createUserTest() throws Exception {
        Mockito.when(userService.create(Mockito.any(User.class))).thenReturn(user1);

        mockMvc.perform((MockMvcRequestBuilders
                .post("/auth/register")
                .contentType(MediaType.APPLICATION_JSON))
                        .content(mapper.writeValueAsString(user1))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("username", is(user1.getUsername())));
    }

    @Test
    @WithMockUser
    void updateUserTest() throws Exception {
        Mockito.when(userService.getCurrentUser()).thenReturn(user1);
        Mockito.when(userService.updateUser(Mockito.any(), Mockito.any())).thenReturn(user1);

        // Test nominal
        mockMvc.perform(MockMvcRequestBuilders
                        .put("/users/update")
                        .content(mapper.writeValueAsString(user1))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("username", is(user1.getUsername())));

        // User différent du user courant
        mockMvc.perform(MockMvcRequestBuilders
                        .put("/users/update")
                        .content(mapper.writeValueAsString(user2))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("message", is(ResourceRepository.getResource("jd.exception.userupdateauthorization", user2.getUsername()))));

        // Update par un Admin
        Mockito.when(userService.getCurrentUser()).thenReturn(user3);
        mockMvc.perform(MockMvcRequestBuilders
                        .put("/users/update")
                        .content(mapper.writeValueAsString(user1))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("username", is(user1.getUsername())));
    }
}