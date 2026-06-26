package br.com.ecommerce.application.dto;

public record LoginResponse(String accessToken, String refreshToken, String tokenType) {
    public LoginResponse(String accessToken, String refreshToken) {
        this(accessToken, refreshToken, "Bearer");
    }
}
