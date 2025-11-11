package ru.reshaka.gateway.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.reshaka.gateway.security.util.KeyReader;

import java.io.InputStream;
import java.security.PrivateKey;
import java.security.PublicKey;

@Configuration
public class KeysConfig {


    @Bean
    public PublicKey refreshPublicKey() {
        try (InputStream is = getClass().getClassLoader().getResourceAsStream("keys/refresh_public.pem")) {
            if (is == null) {
                throw new IllegalStateException("Public key not found at the specified path.");
            }
            return KeyReader.getPublicKey(is);
        } catch (Exception e) {
            throw new IllegalStateException("Failed to load public key", e);
        }
    }

    @Bean
    public PrivateKey refreshPrivateKey() {
        try (InputStream is = getClass().getClassLoader().getResourceAsStream("keys/refresh_private.pem")) {
            if (is == null) {
                throw new IllegalStateException("Private key not found at the specified path.");
            }
            return KeyReader.getPrivateKey(is);
        } catch (Exception e) {
            throw new IllegalStateException("Failed to load private key", e);
        }
    }

    @Bean
    public PublicKey accessPublicKey() {
        try (InputStream is = getClass().getClassLoader().getResourceAsStream("keys/access_public.pem")) {
            if (is == null) {
                throw new IllegalStateException("Public key not found at the specified path.");
            }
            return KeyReader.getPublicKey(is);
        } catch (Exception e) {
            throw new IllegalStateException("Failed to load public key", e);
        }
    }

    @Bean
    public PrivateKey accessPrivateKey() {
        try (InputStream is = getClass().getClassLoader().getResourceAsStream("keys/access_private.pem")) {
            if (is == null) {
                throw new IllegalStateException("Private key not found at the specified path.");
            }
            return KeyReader.getPrivateKey(is);
        } catch (Exception e) {
            throw new IllegalStateException("Failed to load private key", e);
        }
    }

}
