package com.jd.accounting.services;

import com.jd.accounting.exceptions.AccountNotFoundException;
import com.jd.accounting.exceptions.DuplicateAccountForUserException;
import com.jd.accounting.model.Account;
import com.jd.accounting.model.security.Role;
import com.jd.accounting.model.security.User;
import com.jd.accounting.repositories.AccountRepository;
import com.jd.accounting.repositories.ResourceRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.test.annotation.DirtiesContext;

import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
@SpringBootTest //*
@DirtiesContext(classMode= DirtiesContext.ClassMode.AFTER_CLASS)
class AccountServiceImplTest {

    @MockBean //*
    AccountRepository accountRepository;

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
        userUser.setRoles(new ArrayList<>());
        userUser.getRoles().add(new Role(1, "ROLE_USER"));

        userAdmin.setUsername("Admin");
        userAdmin.setRoles(new ArrayList<>());
        userAdmin.getRoles().add(new Role(2, "ROLE_ADMIN"));

        account1.setId(1L);
        account1.setUser(userUser);
        account1.setName("CCP");
        account1.setInitial(0);
        account1.setBalance(0);
        account1.setMovements(new ArrayList<>());

        // TODO : Account.builder()...
        account2.setId(2L);
        account2.setUser(userUser);
        account2.setName("Bourso");
        account2.setInitial(100);
        account2.setBalance(100);
        account2.setMovements(new ArrayList<>());

        account3.setId(3L);
        account3.setUser(userAdmin);
        account3.setName("Cpt Admin");
        account3.setInitial(10000);
        account3.setBalance(10000);
        account3.setMovements(new ArrayList<>());

        accountsUser = new HashSet<>(Arrays.asList(account1, account2));
        accountsAdmin = new HashSet<>(Arrays.asList(account3));

    }

    @Test
    void findAllTest() {
        Mockito.when(accountRepository.findAll()).thenReturn(accountsUser);
        assertEquals(accountService.findAll(), accountsUser);
    }

    @Test
    void createAccountTest() {
        Mockito.when(accountRepository.save(Mockito.any(Account.class))).thenAnswer(
                invocation -> invocation.getArgument(0, Account.class)
        );
        Mockito.when(accountRepository.findByNameAndUser("Bourso", userUser)).thenReturn(Optional.of(account2));
        Mockito.when(accountRepository.findByNameAndUser("New Account", userUser)).thenThrow(new AccountNotFoundException("New Account"));

        // Test nominal
        Account testAccount = accountService.create(userUser, "New Account", 1);
        assertEquals(testAccount.getUser(), userUser);
        assertEquals(testAccount.getName(), "New Account");
        assertEquals(testAccount.getInitial(), 1);
        assertEquals(testAccount.getBalance(), 1);

        // Compte déjà existant pour ce USer
        assertThrows(DuplicateAccountForUserException.class, () -> accountService.create(userUser, "Bourso", 1));
    }

    @Test
    void findByIdTest() {
        Mockito.when(accountRepository.findById(Mockito.eq(Long.valueOf(1)))).thenReturn(Optional.of(account1));
        Mockito.when(accountRepository.findById(Mockito.eq(Long.valueOf(4)))).thenThrow(new AccountNotFoundException(ResourceRepository.getResource("jd.exception.accountnotfound", "4")));

        // Test nominal
        assertEquals(accountService.findById(1L), account1);

        // Compte non existant
        assertThrows(AccountNotFoundException.class, () -> accountService.findById(4L));
    }

    @Test
    void currentUserAccountsTest() {
        Mockito.when(accountRepository.findByUser(userUser)).thenReturn(accountsUser);
        Mockito.when(accountRepository.findByUser(userAdmin)).thenReturn(accountsAdmin);

        assertEquals(accountService.userAccounts(userUser), accountsUser);
        assertEquals(accountService.userAccounts(userAdmin), accountsAdmin);
    }

    @Test
    void deleteByIdTest() {
        // Test nominal : rien à tester

        // Account non existant
        Mockito.doThrow(new EmptyResultDataAccessException(1)).when(accountRepository).deleteById(1L);
        assertThrows(AccountNotFoundException.class, () -> accountService.deleteById(1L));
    }
}