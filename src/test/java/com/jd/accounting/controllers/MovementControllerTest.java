package com.jd.accounting.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jd.accounting.exceptions.AccountNotFoundException;
import com.jd.accounting.model.Account;
import com.jd.accounting.model.Movement;
import com.jd.accounting.model.security.Role;
import com.jd.accounting.model.security.User;
import com.jd.accounting.security.JwtProvider;
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
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(MovementController.class)
@AutoConfigureMockMvc(addFilters = false)
class MovementControllerTest {

    @Autowired
    MockMvc mockMvc;

    @MockBean
    AccountService accountService;

    @MockBean
    UserService userService;

    @MockBean
    MovementService movementService;

    @InjectMocks
    MovementController movementController;

    @MockBean
    private JwtProvider jwtProvider;
    @MockBean
    private PasswordEncoder passwordEncoder;

    private static ObjectMapper mapper = new ObjectMapper();

    Account account1 = new Account();
    Account account2 = new Account();
    Movement movement1 = new Movement();
    Movement movement2 = new Movement();
    Movement newMovement = new Movement();
    Set<Movement> movements;
    User user1 = new User();
    User user2 = new User();

    @BeforeEach
    void prepareData() {
        user1.setUsername("Guest1");
        user1.setRoles(new ArrayList<>());
        user1.getRoles().add(new Role(1, "ROLE_USER"));

        user2.setUsername("Guest2");
        user2.setRoles(new ArrayList<>());
        user2.getRoles().add(new Role(1, "ROLE_USER"));

        account1.setId(1L);
        account1.setUser(user1);
        account1.setName("CCP1");
        account1.setInitial(0);
        account1.setMovements(new ArrayList<>());

        account2.setId(2L);
        account2.setUser(user2);
        account2.setName("CCP2");
        account2.setInitial(1);
        account2.setMovements(new ArrayList<>());

        movement1.setAccount(account1);
        movement1.setAmount(12);
        movement1.setId(1L);
        movement2.setAccount(account1);
        movement2.setAmount(10);
        movement2.setId(2L);

        newMovement.setAmount(1);

        movements = new HashSet<>();
        movements.add(movement1);
        movements.add(movement2);
    }

    @Test
    @WithMockUser
    void listMovementsByAccount() throws Exception {
        Mockito.when(accountService.findById(1L)).thenReturn(account1);
        Mockito.when(accountService.findById(2L)).thenReturn(account2);
        Mockito.when(accountService.findById(4L)).thenThrow(new AccountNotFoundException(4L));
        Mockito.when(movementService.findByAccount(Mockito.any(Account.class))).thenReturn(movements);

        Mockito.when(userService.getCurrentUser()).thenReturn(user1);
        mockMvc.perform(MockMvcRequestBuilders
                        .get("/accounts/1/movements")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)));

        mockMvc.perform(MockMvcRequestBuilders
                        .get("/accounts/2/movements")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("message", is("The account 2 does not belong to user "+user1.getUsername())));

        mockMvc.perform(MockMvcRequestBuilders
                        .get("/accounts/4/movements")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("message", is("Account not found : 4")));
    }

    @Test
    @WithMockUser
    void newMovement() throws Exception {

        Mockito.when(accountService.findById(1L)).thenReturn(account1);
        Mockito.when(accountService.findById(2L)).thenReturn(account2);
        Mockito.when(accountService.findById(4L)).thenThrow(new AccountNotFoundException(4L));
        Mockito.when(movementService.create(Mockito.any(Account.class), Mockito.any(Movement.class))).thenReturn(movement1);
        Mockito.when(userService.getCurrentUser()).thenReturn(user1);

        mockMvc.perform(post("/accounts/1/movements")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(newMovement))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.account.id", is(1)));

        mockMvc.perform(post("/accounts/2/movements")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(newMovement))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("message", is("The account 2 does not belong to user "+user1.getUsername())));

        mockMvc.perform(post("/accounts/4/movements")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(newMovement))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("message", is("Account not found : 4")));

    }
}