package com.jd.accounting.controllers;

import com.jd.accounting.model.Account;
import com.jd.accounting.model.Movement;
import com.jd.accounting.model.security.Role;
import com.jd.accounting.model.security.RoleName;
import com.jd.accounting.model.security.User;
import com.jd.accounting.security.SecurityAdapter;
import com.jd.accounting.services.AccountService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.hamcrest.Matchers.*;
import java.util.*;


@WebMvcTest(controllers = AccountController.class)
@AutoConfigureMockMvc(addFilters = false)
class AccountControllerTest {

    @Autowired
    MockMvc mockMvc;

    @MockBean
    AccountService accountService;

    // TODO : Semble obligatoire ? pourquoi ?
    @MockBean
    SecurityAdapter securityAdapter;

    Account account1 = new Account();
    Account account2 = new Account();
    Account account3 = new Account();

    User userUser = new User();
    User userAdmin = new User();

    @BeforeEach
    void prepareData() {
        userUser.setUsername("User");
        userUser.setRoles(new ArrayList<Role>());
        userUser.getRoles().add(new Role(1, RoleName.USER));

        userAdmin.setUsername("Admin");
        userAdmin.setRoles(new ArrayList<Role>());
        userAdmin.getRoles().add(new Role(2, RoleName.ADMIN));

        account1.setId(1L);
        account1.setUser(userUser);
        account1.setName("CCP");
        account1.setInitial(0);
        account1.setMovements(new ArrayList<Movement>());

        account2.setId(2L);
        account2.setUser(userUser);
        account2.setName("Bourso");
        account2.setInitial(100);
        account2.setMovements(new ArrayList<Movement>());

        account3.setId(3L);
        account3.setUser(userAdmin);
        account3.setName("Cpt Admin");
        account3.setInitial(10000);
        account3.setMovements(new ArrayList<Movement>());

    }

    @Test
    @WithMockUser(roles = "ADMIN", username = "Guest")
    void listAccounts() throws Exception {
        //List<Account> accounts = new ArrayList<>(Arrays.asList(account1, account2, account3));
        Set<Account> accountSet = new HashSet<Account>(Set.of(account1, account2, account3));

        //Mockito.when(accountRepository.findAll()).thenReturn(accounts);
        Mockito.when(accountService.currentUserAccounts()).thenReturn(accountSet);

        // TODO : Enrichir le test avec le contenu attendu et le formattage standard JSON attendu
        mockMvc.perform(MockMvcRequestBuilders
                .get("/accounts")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(3)));
    }
}