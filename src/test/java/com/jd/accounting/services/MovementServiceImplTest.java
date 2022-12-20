package com.jd.accounting.services;

import com.jd.accounting.exceptions.AccountNotFoundException;
import com.jd.accounting.model.Account;
import com.jd.accounting.model.Movement;
import com.jd.accounting.model.security.Role;
import com.jd.accounting.model.security.User;
import com.jd.accounting.repositories.AccountRepository;
import com.jd.accounting.repositories.MovementRepository;
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
import org.springframework.test.annotation.DirtiesContext;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.doNothing;

@ExtendWith(MockitoExtension.class)
@SpringBootTest
@DirtiesContext(classMode= DirtiesContext.ClassMode.AFTER_CLASS)
class MovementServiceImplTest {

    @MockBean
    MovementRepository movementRepository;
    @MockBean
    Account account2;
    @Autowired
    MovementServiceImpl movementService;

    Account account1 = new Account();
    Movement movement1 = new Movement();
    Movement movement2 = new Movement();
    User userUser = new User();

    List<Movement> movements = new ArrayList<>();
    Set<Movement> movementsSet = new HashSet<>();

    @BeforeEach
    void prepareData() {

        account1.setId(1L);
        account1.setUser(userUser);
        account1.setName("CCP");
        account1.setInitial(0);
        account1.setBalance(0);
        account1.setMovements(new ArrayList<>());

        movement1.setAmount(12);
        movement1.setId(1L);
        movement2.setAmount(10);
        movement2.setId(2L);

        movements.add(movement1);
        movements.add(movement2);
        movementsSet.add(movement1);
        movementsSet.add(movement2);
    }

    @Test
    void findByAccountTest() {
        Mockito.when(account2.getMovements()).thenReturn(movements);
        assertEquals(movementService.findByAccount(account2), movementsSet);
    }

    @Test
    void createTest() {
        Mockito.when(movementRepository.save(Mockito.any(Movement.class))).thenAnswer(
                invocation -> invocation.getArgument(0, Movement.class)
        );

        Movement movement = movementService.create(account1, movement1);
        assertEquals(movement.getAccount(), account1);
        assertEquals(movement.getAmount(), 12);
        assertEquals(movement.getAccount().getBalance(), 12);
    }

    @Test
    void deleteMovementTest() {
        doNothing().when(movementRepository).deleteById(Mockito.any());

        // Test nominal
        movement1.setAccount(account1);
        movementService.delete(movement1);
        assertEquals(account1.getBalance(), -12);

    }
}