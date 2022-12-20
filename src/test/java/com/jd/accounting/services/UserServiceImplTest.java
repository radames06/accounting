package com.jd.accounting.services;

import com.jd.accounting.exceptions.UserAlreadyExistsException;
import com.jd.accounting.exceptions.UserNotFoundException;
import com.jd.accounting.model.Account;
import com.jd.accounting.model.security.Provider;
import com.jd.accounting.model.security.User;
import com.jd.accounting.repositories.ResourceRepository;
import com.jd.accounting.repositories.UserRepository;
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
import org.springframework.security.core.parameters.P;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.annotation.DirtiesContext;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
@SpringBootTest
@DirtiesContext(classMode= DirtiesContext.ClassMode.AFTER_CLASS)
class UserServiceImplTest {

    @MockBean
    UserRepository userRepository;
    @MockBean
    PasswordEncoder passwordEncoder;
    @Autowired
    UserServiceImpl userService;

    User userGuest;
    User userAdmin;
    List<User> users = new ArrayList<>();

    @BeforeEach
    void prepareData() {
        userGuest = new User();
        userGuest.setUsername("Guest");
        userGuest.setProvider(Provider.LOCAL);

        userAdmin = new User();
        userAdmin.setUsername("Admin");
        userAdmin.setProvider(Provider.LOCAL);

        users.add(userGuest);
        users.add(userAdmin);
    }

    @Test
    void getCurrentUserTest() {
        // Rien à tester
    }

    @Test
    void loadUserByUsername() {
        Mockito.when(userRepository.findByUsername("Guest")).thenReturn(Optional.of(userGuest));
        Mockito.when(userRepository.findByUsername("Other")).thenThrow(new UserNotFoundException(ResourceRepository.getResource("jd.exception.usernotfound", "Other"))); //ResourceRepository.getResource("jd.exception.usernotfound", "Other")));

        assertEquals(userService.loadUserByUsername("Guest"), userGuest);
        assertThrows(UserNotFoundException.class, () -> userService.loadUserByUsername("Other"));
    }

    @Test
    void findAll() {
        Mockito.when(userRepository.findAll()).thenReturn(users);

        assertEquals(userService.findAll().size(), 2);
    }

    @Test
    void createUserTest() {
        Mockito.when(userRepository.save(Mockito.any(User.class))).thenReturn(userGuest);
        Mockito.when(passwordEncoder.encode(Mockito.any())).thenReturn("encoded password");
        Mockito.when(userRepository.findByUsername(userAdmin.getUsername())).thenReturn(Optional.of(userAdmin));
        Mockito.when(userRepository.findByUsername(userGuest.getUsername())).thenReturn(Optional.empty());

        // Test nominal
        User createdUser = userService.create(userGuest);
        assertEquals(createdUser.getUsername(), userGuest.getUsername());
        assertEquals(createdUser.getPassword(), "encoded password");
        assertEquals(createdUser.getStringRoles().contains("ROLE_USER"), true);
        assertEquals(createdUser.getCategories().size(), 3);

        // User déjà existiant
        assertThrows(UserAlreadyExistsException.class, () -> userService.create(userAdmin));
    }

    @Test
    void updatePasswordTest() {
        Mockito.when(passwordEncoder.encode(Mockito.any())).thenReturn("encoded password");
        Mockito.when(userRepository.save(Mockito.any(User.class))).thenAnswer(
                invocation -> invocation.getArgument(0, User.class)
        );

        assertEquals(userService.updatePassword(userGuest, "encoded password").getPassword(), "encoded password");
    }
}