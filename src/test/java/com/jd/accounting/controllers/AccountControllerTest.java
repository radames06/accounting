package com.jd.accounting.controllers;

import com.jd.accounting.SpringSecurityWebAuxTestConfig;
import com.jd.accounting.model.Account;
import com.jd.accounting.model.mappers.UserMapper;
import com.jd.accounting.model.security.Role;
import com.jd.accounting.model.security.RoleName;
import com.jd.accounting.model.security.User;
import com.jd.accounting.security.SecurityAdapter;
import com.jd.accounting.services.AccountService;
import com.jd.accounting.services.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.access.SecurityConfig;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.security.Principal;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

//@SpringBootTest
//@ExtendWith(MockitoExtension.class)
@WebMvcTest(controllers = AccountController.class)
@AutoConfigureMockMvc(addFilters = false)
class AccountControllerTest {

    @Autowired
    MockMvc mockMvc;

    @MockBean
    AccountService accountService;

//    @MockBean
//    SecurityContext securityContext;
//
//    @MockBean
//    Authentication authentication;

    @MockBean
    UserService userService;

    @InjectMocks
    AccountController accountController;

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
        userUser.setUsername("Guest");
        userUser.setRoles(new ArrayList<>());
        userUser.getRoles().add(new Role(1, RoleName.USER));

        userAdmin.setUsername("Admin");
        userAdmin.setRoles(new ArrayList<>());
        userAdmin.getRoles().add(new Role(2, RoleName.ADMIN));

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

//        Mockito.when(securityContext.getAuthentication()).thenReturn(authentication);
//        SecurityContextHolder.setContext(securityContext);

        Mockito.when(userService.findByUsername("Guest")).thenReturn(userUser);
        Mockito.when(userService.findByUsername("Admin")).thenReturn(userAdmin);


    }

    @Test
    @WithMockUser(value = "USER") //roles = "USER", username = "Guest")
    void listAccounts() throws Exception {
        Set<Account> accountSet = new HashSet<>(Set.of(account1, account2, account3));
        Mockito.when(accountService.userAccounts(Mockito.any(User.class))).thenReturn(accountSet);
//        Mockito.when(authentication.getPrincipal()).thenReturn(UserMapper.userToPrincipal(userUser));

        System.out.println(accountService.userAccounts(userUser));
        // TODO : Enrichir le test avec le contenu attendu et le formattage standard JSON attendu
        mockMvc.perform(MockMvcRequestBuilders
                .get("/accounts")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(3)));
    }

    @Test
    @WithMockUser(roles = "USER", username = "Admin")
    void createAccount() throws Exception {
        //Set<Account> accountSet = new HashSet<Account>(Set.of(account1, account2));
        Mockito.when(accountService.create(eq(userAdmin), Mockito.any(String.class), Mockito.anyFloat())).thenReturn(account3);

        mockMvc.perform(post("/accounts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "name":"Cpt Admin",
                                    "initial":"1"
                                }""")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("name", is("Cpt Admin")));
    }
}