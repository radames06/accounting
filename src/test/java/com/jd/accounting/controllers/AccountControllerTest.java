package com.jd.accounting.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jd.accounting.exceptions.AccountNotFoundException;
import com.jd.accounting.exceptions.DuplicateAccountForUserException;
import com.jd.accounting.model.Account;
import com.jd.accounting.model.security.Role;
import com.jd.accounting.model.security.User;
import com.jd.accounting.repositories.ResourceRepository;
import com.jd.accounting.services.AccountService;
import com.jd.accounting.services.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
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
import static org.mockito.Mockito.doNothing;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
@DirtiesContext(classMode= DirtiesContext.ClassMode.AFTER_CLASS)
class AccountControllerTest {

    @Autowired
    MockMvc mockMvc;

    @MockBean
    AccountService accountService;

    @MockBean
    UserService userService;

    private static ObjectMapper mapper = new ObjectMapper();

    Account account1 = new Account();
    Account account2 = new Account();
    Account account3 = new Account();
    User userUser = new User();
    User userAdmin = new User();

    @BeforeEach
    void prepareData() {
        userUser.setUsername("Guest");
        userUser.setRoles(new ArrayList<>());
        userUser.getRoles().add(new Role(1, "ROLE_USER"));

        userAdmin.setUsername("Admin");
        userAdmin.setRoles(new ArrayList<>());
        userAdmin.getRoles().add(new Role(2, "ROLE_ADMIN"));

        account1.setId(1L);
        account1.setUser(userUser);
        account1.setName("CCP");
        account1.setInitial(0);
        account1.setMovements(new ArrayList<>());

        account2.setId(2L);
        account2.setUser(userUser);
        account2.setName("Bourso");
        account2.setInitial(100);
        account2.setMovements(new ArrayList<>());

        account3.setId(3L);
        account3.setUser(userAdmin);
        account3.setName("Cpt Admin");
        account3.setInitial(10000);
        account3.setMovements(new ArrayList<>());

        Mockito.when(userService.loadUserByUsername("Guest")).thenReturn(userUser);
        Mockito.when(userService.loadUserByUsername("Admin")).thenReturn(userAdmin);
        Mockito.when(userService.getCurrentUser()).thenReturn(userUser);

    }

    @Test
    @WithMockUser
    void listAccountsByUserTest() throws Exception {
        Set<Account> accountSet = new HashSet<>(Set.of(account1, account2, account3));
        Mockito.when(accountService.userAccounts(Mockito.any(User.class))).thenReturn(accountSet);

        mockMvc.perform(MockMvcRequestBuilders
                .get("/accounts")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(3)));
    }

    @Test
    @WithMockUser
    void getAccountById() throws Exception {
        Mockito.when(accountService.findById(1L)).thenReturn(account1);
        Mockito.when(accountService.findById(3L)).thenReturn(account3);
        Mockito.when(accountService.findById(4L)).thenThrow(new AccountNotFoundException(ResourceRepository.getResource("jd.exception.accountnotfound", "4")));

        Mockito.when(userService.getCurrentUser()).thenReturn(userUser);
        mockMvc.perform(MockMvcRequestBuilders
                        .get("/accounts/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
        mockMvc.perform(MockMvcRequestBuilders
                        .get("/accounts/3")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("message", is(ResourceRepository.getResource("jd.exception.accountbelongs", "3", userUser.getUsername()))));
        mockMvc.perform(MockMvcRequestBuilders
                        .get("/accounts/4")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("message", is(ResourceRepository.getResource("jd.exception.accountnotfound", "4"))));

        Mockito.when(userService.getCurrentUser()).thenReturn(userAdmin);
        mockMvc.perform(MockMvcRequestBuilders
                        .get("/accounts/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void listAllAccounts() throws Exception {
        Set<Account> accountSet = new HashSet<>(Set.of(account1, account2, account3));
        Mockito.when(accountService.findAll()).thenReturn(accountSet);

        mockMvc.perform(MockMvcRequestBuilders
                        .get("/admin/accounts/")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(3)));
    }

    @Test
    @WithMockUser
    void createAccountTest() throws Exception {
        Mockito.when(accountService.create(Mockito.any(User.class), eq(account3.getName()), Mockito.anyFloat())).thenReturn(account3);
        Mockito.when(accountService.create(Mockito.any(User.class), eq(account2.getName()), Mockito.anyFloat()))
                .thenThrow(new DuplicateAccountForUserException(ResourceRepository.getResource("jd.exception.duplicateaccountforuser", account2.getName(), userUser.getUsername())));

        mockMvc.perform(post("/accounts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(account3))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("name", is(account3.getName())));

        mockMvc.perform(post("/accounts")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(account2))
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("message", is(ResourceRepository.getResource("jd.exception.duplicateaccountforuser", account2.getName(), userUser.getUsername()))));
    }

    @Test
    @WithMockUser
    void deleteAccountTest() throws Exception {
        Mockito.doThrow(new AccountNotFoundException(ResourceRepository.getResource("jd.exception.accountnotfound", "1"))).when(accountService).deleteById(1L);
        Mockito.when(accountService.findById(1L)).thenThrow(new AccountNotFoundException(ResourceRepository.getResource("jd.exception.accountnotfound", "1")));
        Mockito.when(accountService.findById(3L)).thenReturn(account3);
        Mockito.when(userService.getCurrentUser()).thenReturn(userUser);
        doNothing().when(accountService).deleteById(Mockito.any());

        mockMvc.perform(delete("/accounts/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("message", is(ResourceRepository.getResource("jd.exception.accountnotfound", "1"))));

        mockMvc.perform(delete("/accounts/3")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("message", is(ResourceRepository.getResource("jd.exception.accountbelongs", "3", userUser.getUsername()))));
    }
}