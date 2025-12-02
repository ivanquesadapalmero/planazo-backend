package com.planazo;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

@SpringBootTest
@TestPropertySource(locations = "classpath:application-test.properties")
class PlanazoApplicationTests {

    @Test
    void contextLoads() {
        // Test básico para verificar que el contexto de Spring arranca
    }
}
