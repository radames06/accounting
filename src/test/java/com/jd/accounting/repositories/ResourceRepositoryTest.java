package com.jd.accounting.repositories;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
@SpringBootTest
public class ResourceRepositoryTest {

    @Autowired
    private ResourceRepository resourceRepository;

    @Test
    public void resourceTest() {
        assertEquals(resourceRepository.getResource("spring.messages.test"), "this is a test label");
    }

}
