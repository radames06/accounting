package com.jd.accounting.services;

import com.jd.accounting.model.Account;
import com.jd.accounting.model.Movement;
import com.jd.accounting.model.mappers.UserMapper;
import com.jd.accounting.model.security.Role;
import com.jd.accounting.model.security.RoleName;
import com.jd.accounting.model.security.User;
import com.jd.accounting.repositories.AccountRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import java.util.*;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ExtendWith(MockitoExtension.class)
class AccountServiceImplTest {

    @MockBean
    AccountRepository accountRepository;

//    @MockBean
//    UserService userService;
//
//    @MockBean
//    Authentication authentication;

//    @MockBean
//    SecurityContext securityContext;

    @Autowired
    AccountServiceImpl accountService;

    Account account1 = new Account();
    Account account2 = new Account();
    Account account3 = new Account();

    User userUser = new User();
    User userAdmin = new User();

    Set<Account> accountsUser;
    Set<Account> accountsAdmin;

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

        // TODO : Account.builder()...
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

//        Mockito.when(securityContext.getAuthentication()).thenReturn(authentication);
//        SecurityContextHolder.setContext(securityContext);
//        Mockito.when(userService.findByUsername("User")).thenReturn(userUser);
//        Mockito.when(userService.findByUsername("Admin")).thenReturn(userAdmin);

        accountsUser = new HashSet<>(Arrays.asList(account1, account2));
        accountsAdmin = new HashSet<>(Arrays.asList(account3));
        Mockito.when(accountRepository.findByUser(userUser)).thenReturn(accountsUser);
        Mockito.when(accountRepository.findByUser(userAdmin)).thenReturn(accountsAdmin);

    }

    @Test
    void currentUserAccounts() {

        assertEquals(accountService.userAccounts(userUser), accountsUser);
        assertEquals(accountService.userAccounts(userAdmin), accountsAdmin);

    }
}