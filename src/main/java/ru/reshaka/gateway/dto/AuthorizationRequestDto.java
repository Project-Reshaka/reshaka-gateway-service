package ru.reshaka.gateway.dto;

import lombok.Value;

@Value
public class AuthorizationRequestDto {

    String username;

    String password;

}
