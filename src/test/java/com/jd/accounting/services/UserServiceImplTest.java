package com.jd.accounting.services;

import com.jd.accounting.exceptions.UserAlreadyExistsException;
import com.jd.accounting.exceptions.UserNotFoundException;
import com.jd.accounting.model.security.Provider;
import com.jd.accounting.model.security.User;
import com.jd.accounting.repositories.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.core.parameters.P;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.WithMockUser;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock
    UserRepository userRepository;

    @Mock
    PasswordEncoder passwordEncoder;

    @InjectMocks
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
    void getCurrentUser() {
    }

    @Test
    void loadUserByUsername() {
        Mockito.when(userRepository.findByUsername("Guest")).thenReturn(Optional.of(userGuest));
        Mockito.when(userRepository.findByUsername("Other")).thenThrow(new UserNotFoundException());

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
        Mockito.when(userRepository.findByUsername(userGuest.getUsername())).thenThrow(new UserNotFoundException(userGuest.getUsername()));

        User createdUser = userService.create(userGuest);
        assertEquals(createdUser.getUsername(), userGuest.getUsername());
        assertEquals(createdUser.getPassword(), "encoded password");
        assertThrows(UserAlreadyExistsException.class, () -> userService.create(userAdmin));

    }
}