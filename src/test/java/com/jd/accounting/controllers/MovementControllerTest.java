package com.jd.accounting.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jd.accounting.exceptions.AccountNotFoundException;
import com.jd.accounting.exceptions.MovementNotFoundException;
import com.jd.accounting.model.Account;
import com.jd.accounting.model.Movement;
import com.jd.accounting.model.security.Role;
import com.jd.accounting.model.security.User;
import com.jd.accounting.repositories.ResourceRepository;
import com.jd.accounting.security.JwtProvider;
import com.jd.accounting.services.AccountService;
import com.jd.accounting.services.MovementService;
import com.jd.accounting.services.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.web.servlet.resource.ResourceResolver;

import java.util.*;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.doNothing;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
@DirtiesContext(classMode= DirtiesContext.ClassMode.AFTER_CLASS)
class MovementControllerTest {

    @Autowired
    MockMvc mockMvc;
    @MockBean
    AccountService accountService;
    @MockBean
    UserService userService;
    @MockBean
    MovementService movementService;
    private static ObjectMapper mapper = new ObjectMapper();

    Account account1 = new Account();
    Account account2 = new Account();
    Account account3 = new Account();
    Movement movement1 = new Movement();
    Movement movement2 = new Movement();
    Movement movement3 = new Movement();
    Movement newMovement = new Movement();
    Set<Movement> movements;
    User user1 = new User();
    User user2 = new User();
    User user3 = new User();

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

        account3.setId(3L);
        account3.setUser(user1);
        account3.setName("CCP2");
        account3.setInitial(0);
        account3.setMovements(new ArrayList<>());

        movement1.setAccount(account1);
        movement1.setAmount(12);
        movement1.setTiers("Tiers1");
        movement1.setMovementDate(new Date());
        movement1.setId(1L);
        movement2.setAccount(account1);
        movement2.setAmount(10);
        movement2.setId(2L);
        movement2.setTiers("Tiers1");
        movement2.setMovementDate(new Date());
        movement3.setAccount(account2);
        movement3.setAmount(10);
        movement3.setId(3L);
        movement3.setTiers("Tiers1");
        movement3.setMovementDate(new Date());

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
        Mockito.when(accountService.findById(4L)).thenThrow(new AccountNotFoundException(ResourceRepository.getResource("jd.exception.accountnotfound", "4")));
        Mockito.when(movementService.findByAccount(Mockito.any(Account.class))).thenReturn(movements);

        // Test nominal
        Mockito.when(userService.getCurrentUser()).thenReturn(user1);
        mockMvc.perform(MockMvcRequestBuilders
                        .get("/accounts/1/movements")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)));

        // User non autorisé
        mockMvc.perform(MockMvcRequestBuilders
                        .get("/accounts/2/movements")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("message", is(ResourceRepository.getResource("jd.exception.accountbelongs", "2", user1.getUsername()))));

        // Compte non trouvé
        mockMvc.perform(MockMvcRequestBuilders
                        .get("/accounts/4/movements")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("message", is(ResourceRepository.getResource("jd.exception.accountnotfound", "4"))));
    }

    @Test
    @WithMockUser
    void newMovement() throws Exception {

        Mockito.when(accountService.findById(1L)).thenReturn(account1);
        Mockito.when(accountService.findById(2L)).thenReturn(account2);
        Mockito.when(accountService.findById(4L)).thenThrow(new AccountNotFoundException(ResourceRepository.getResource("jd.exception.accountnotfound", "4")));
        Mockito.when(movementService.create(Mockito.any(Account.class), Mockito.any(Movement.class))).thenReturn(movement1);
        Mockito.when(userService.getCurrentUser()).thenReturn(user1);

        // Test nominal
        mockMvc.perform(post("/accounts/1/movements")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(newMovement))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.account.id", is(1)));

        // Compte n'appartenant pas au user
        mockMvc.perform(post("/accounts/2/movements")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(newMovement))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("message", is(ResourceRepository.getResource("jd.exception.accountbelongs", "2", user1.getUsername()))));

        // Compte inexistant
        mockMvc.perform(post("/accounts/4/movements")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(newMovement))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("message", is(ResourceRepository.getResource("jd.exception.accountnotfound", "4"))));

        // Mouvement créé par Admin
        Mockito.when(userService.getCurrentUser()).thenReturn(user3);
        mockMvc.perform(post("/accounts/1/movements")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(newMovement))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.account.id", is(1)));

    }

    @Test
    @WithMockUser
    void deleteMovementTest() throws Exception {
        Mockito.when(userService.getCurrentUser()).thenReturn(user1);
        Mockito.when(accountService.findById(1L)).thenReturn(account1);
        Mockito.when(accountService.findById(2L)).thenReturn(account2);
        Mockito.when(accountService.findById(3L)).thenReturn(account3);
        Mockito.when(movementService.findById(1L)).thenReturn(movement1);
        Mockito.when(movementService.findById(3L)).thenReturn(movement3);
        Mockito.doThrow(new MovementNotFoundException(ResourceRepository.getResource("jd.exception.movementnotfound", "4"))).when(movementService).findById(4L);
        doNothing().when(movementService).delete(Mockito.any());

        // Test nominal
        mockMvc.perform(MockMvcRequestBuilders.delete("/accounts/1/movements/1"))
                .andExpect(status().isOk());

        // Mouvement d'un autre compte
        mockMvc.perform(MockMvcRequestBuilders.delete("/accounts/3/movements/1"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("message", is(ResourceRepository.getResource("jd.exception.movementnotfound", "1", "3"))));

        // Compte n'appartenant pas au user
        mockMvc.perform(MockMvcRequestBuilders.delete("/accounts/2/movements/3"))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("message", is(ResourceRepository.getResource("jd.exception.accountbelongs", "2", user1.getUsername()))));

        // Mouvement inexistant
        mockMvc.perform(MockMvcRequestBuilders.delete("/accounts/1/movements/4"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("message", is(ResourceRepository.getResource("jd.exception.movementnotfound", "4", "1"))));

        // Movement supprimé par Admin
        Mockito.when(userService.getCurrentUser()).thenReturn(user3);
        mockMvc.perform(MockMvcRequestBuilders.delete("/accounts/1/movements/1"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser
    void updateMovementTest() throws Exception {
        Mockito.when(userService.getCurrentUser()).thenReturn(user1);
        Mockito.when(accountService.findById(1L)).thenReturn(account1);
        Mockito.when(accountService.findById(2L)).thenReturn(account2);
        Mockito.when(accountService.findById(3L)).thenReturn(account3);
        Mockito.when(movementService.findById(1L)).thenReturn(movement1);
        Mockito.when(movementService.findById(3L)).thenReturn(movement3);
        Mockito.doThrow(new MovementNotFoundException(ResourceRepository.getResource("jd.exception.movementnotfound", "4"))).when(movementService).findById(4L);
        Mockito.when(movementService.update(Mockito.any())).thenReturn(movement1);

        // Test nominal
        mockMvc.perform(MockMvcRequestBuilders.put("/accounts/1/movements/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(movement1))
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        // Mouvement d'un autre compte
        mockMvc.perform(MockMvcRequestBuilders.put("/accounts/3/movements/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(movement1))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("message", is(ResourceRepository.getResource("jd.exception.movementnotfound", "1", "3"))));

        // Compte n'appartenant pas au user
        mockMvc.perform(MockMvcRequestBuilders.put("/accounts/2/movements/3")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(movement1))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("message", is(ResourceRepository.getResource("jd.exception.accountbelongs", "2", user1.getUsername()))));

        // Movement supprimé par Admin
        Mockito.when(userService.getCurrentUser()).thenReturn(user3);
        mockMvc.perform(MockMvcRequestBuilders.put("/accounts/1/movements/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(movement1))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

}