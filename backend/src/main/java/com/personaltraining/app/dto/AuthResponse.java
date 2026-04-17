package com.personaltraining.app.dto;

public record AuthResponse(
        String token,
        UserResponse user
) {
}
