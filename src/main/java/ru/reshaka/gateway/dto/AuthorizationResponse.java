package ru.reshaka.gateway.dto;

import lombok.Getter;

@Getter
public class AuthorizationResponse {

    public final static String BEARER = "Bearer ";

    private final String headerName = "Authorization";

    private final String headerValue;

    public AuthorizationResponse(String headerValue) {
        this.headerValue = BEARER + headerValue;
    }

}
