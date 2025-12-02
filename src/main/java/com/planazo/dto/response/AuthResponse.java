package com.planazo.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuthResponse {

    private String token;
    private String type = "Bearer";
    private UserResponse user;

    // Constructor que auto-asigna type = "Bearer"
    public AuthResponse(String token, UserResponse user) {
        this.token = token;
        this.type = "Bearer";
        this.user = user;
    }
}

