package ru.reshaka.gateway.security.service;

import io.jsonwebtoken.Claims;
import lombok.Getter;
import lombok.Setter;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;
import java.util.Set;

@Getter
@Setter
public class JwtAuthentication implements Authentication {

    private boolean authenticated;
    private String username;
    private String firstName;
    private Set<GrantedAuthority> roles;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() { return roles; }

    @Override
    public Object getCredentials() { return null; }

    @Override
    public Object getDetails() { return null; }

    @Override
    public Object getPrincipal() { return username; }

    @Override
    public boolean isAuthenticated() { return authenticated; }

    @Override
    public void setAuthenticated(boolean isAuthenticated) throws IllegalArgumentException {
        this.authenticated = isAuthenticated;
    }

    @Override
    public String getName() { return firstName; }

    @SuppressWarnings("unchecked")
    public static JwtAuthentication fromClaims(Claims claims) {
        JwtAuthentication authentication = new JwtAuthentication();
        authentication.username = claims.getSubject();
        // в клеймах лежит Collection<? extends GrantedAuthority>, JwtTokenGenerator, 28 строка
        authentication.roles = Set.copyOf(claims.get("authorities", Collection.class));
        // JwtTokenGenerator, 27
        authentication.firstName = (String) claims.get("name");

        return authentication;
    }

}