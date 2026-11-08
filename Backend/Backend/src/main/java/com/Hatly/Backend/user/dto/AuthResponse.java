package com.Hatly.Backend.user.dto;

import lombok.Data;

@Data
public class AuthResponse {
    private String message;
    private String accessToken;
    private String refreshToken;
    private UserData user;

    @Data
    public static class UserData {
        private Long id;
        private String email;
        private String phone;
        private String systemRole;
    }
}
