package com.jd.accounting.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jd.accounting.model.security.Role;
import com.jd.accounting.model.security.User;
import com.jd.accounting.security.JwtProvider;
import com.jd.accounting.security.SecurityConfiguration;
import com.jd.accounting.services.AccountService;
import com.jd.accounting.services.MovementService;
import com.jd.accounting.services.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserController.class)
@AutoConfigureMockMvc(addFilters = false)
class UserControllerTest {

    @Autowired
    MockMvc mockMvc;

    @MockBean
    UserService userService;

    @InjectMocks
    UserController userController;

    @MockBean
    private JwtProvider jwtProvider;
    @MockBean
    private PasswordEncoder passwordEncoder;

    private static ObjectMapper mapper = new ObjectMapper();

    User user1 = new User();
    User user2 = new User();
    Set<User> userSet;

    @BeforeEach
    void prepareData() {
        user1.setUsername("Guest1");
        user1.setRoles(new ArrayList<>());
        user1.getRoles().add(new Role(1, "ROLE_USER"));

        user2.setUsername("Guest2");
        user2.setRoles(new ArrayList<>());
        user2.getRoles().add(new Role(1, "ROLE_USER"));

        userSet = new HashSet<>(Set.of(user1, user2));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void listUsersTest() throws Exception {

        Mockito.when(userService.findAll()).thenReturn(userSet);

        mockMvc.perform(MockMvcRequestBuilders
                        .get("/users")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)));
    }

    @Test
    void createUserTest() throws Exception {
        Mockito.when(userService.create(Mockito.any(User.class))).thenReturn(user1);

        mockMvc.perform((MockMvcRequestBuilders
                .post("/auth/register")
                .contentType(MediaType.APPLICATION_JSON)).content(mapper.writeValueAsString(user1))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("username", is(user1.getUsername())));
    }
}