package ru.reshaka.gateway.security.util;

import java.io.InputStream;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

public class KeyReader {


    public static PrivateKey getPrivateKey(InputStream inputStream) throws Exception {
        byte[] keyBytes = inputStream.readAllBytes();
        return generatePrivateKey(keyBytes);
    }

    public static PublicKey getPublicKey(InputStream inputStream) throws Exception {
        byte[] keyBytes = inputStream.readAllBytes();
        return generatePublicKey(keyBytes);
    }

    private static PrivateKey generatePrivateKey(byte[] keyBytes) throws Exception {
        keyBytes = Base64.getDecoder().decode(new String(keyBytes)
                .replaceAll("-----\\w+ PRIVATE KEY-----", "")
                .replaceAll("\\s", ""));
        PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(keyBytes);
        KeyFactory kf = KeyFactory.getInstance("RSA");
        return kf.generatePrivate(spec);
    }

    private static PublicKey generatePublicKey(byte[] keyBytes) throws Exception {
        keyBytes = Base64.getDecoder().decode(new String(keyBytes)
                .replaceAll("-----\\w+ PUBLIC KEY-----", "")
                .replaceAll("\\s", ""));
        X509EncodedKeySpec spec = new X509EncodedKeySpec(keyBytes);
        KeyFactory kf = KeyFactory.getInstance("RSA");
        return kf.generatePublic(spec);
    }
}
