package com.example.backend;

import com.example.backend.controller.StockController;
import com.example.backend.service.TwseService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * Integration test for the entire application
 * Tests that all components are properly wired and the application starts correctly
 */
@SpringBootTest
@TestPropertySource(properties = {
        "spring.profiles.active=test"
})
public class BackendApplicationTest {

    @Autowired
    private StockController stockController;

    @Autowired
    private TwseService twseService;

    @Test
    void contextLoads() {
        // Verify that all main components are loaded
        assertNotNull(stockController, "StockController should be loaded");
        assertNotNull(twseService, "TwseService should be loaded");
    }

    @Test
    void applicationStartsSuccessfully() {
        // This test verifies that the Spring Boot application can start successfully
        // If the application context fails to load, this test will fail
        // This is important for catching configuration issues
        assert true; // If we reach this point, the context loaded successfully
    }
}