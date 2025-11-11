package ru.reshaka.gateway.dto;

import lombok.Value;

public record AuthenticationResponse(String accessToken, long expiresInSeconds) {}
