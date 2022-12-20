package com.jd.accounting.repositories;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
@SpringBootTest
public class ResourceRepositoryStaticTest {

    @Test
    public void resourceTest() {
        assertEquals(ResourceRepository.getResource("spring.messages.test"), "this is a test label");
    }
}
