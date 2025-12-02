package com.planazo.controller;

import com.planazo.exception.BadRequestException;
import com.planazo.exception.ResourceNotFoundException;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class HealthController {

    @GetMapping("/health")
    public Map<String, Object> health() {
        Map<String, Object> response = new HashMap<>();
        response.put("status", "OK");
        response.put("message", "PLANAZO Backend is running!");
        response.put("timestamp", LocalDateTime.now());
        response.put("version", "1.0.0");
        return response;
    }

    // Endpoint para probar excepciones
    @GetMapping("/test-error")
    public String testError(@RequestParam(required = false) String type) {
        if ("notfound".equals(type)) {
            throw new ResourceNotFoundException("Recurso no encontrado para prueba");
        } else if ("badrequest".equals(type)) {
            throw new BadRequestException("Solicitud incorrecta para prueba");
        } else {
            throw new RuntimeException("Error genérico para prueba");
        }
    }
}
