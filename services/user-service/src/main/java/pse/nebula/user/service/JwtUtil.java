package pse.nebula.user.service;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Component
public class JwtUtil {

    @Value("${jwt.test-private-key}")
    private String testPrivateKeyPem;

    private volatile PrivateKey privateKey;

    public String generateToken(String userId, String email, String role) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("email", email);
        claims.put("role", role);

        return Jwts.builder()
                .claims(claims)
                .subject(userId)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60)) // 1 hour (3600 seconds)
                .signWith(getPrivateKey(), SignatureAlgorithm.RS256)
                .compact();
    }

    private PrivateKey getPrivateKey() {
        if (privateKey != null) {
            return privateKey;
        }

        synchronized (this) {
            if (privateKey != null) {
                return privateKey;
            }

            try {
                String privateKeyPEM = testPrivateKeyPem
                        .replace("-----BEGIN PRIVATE KEY-----", "")
                        .replace("-----END PRIVATE KEY-----", "")
                        .replaceAll("\\s", "");

                byte[] encoded = Base64.getDecoder().decode(privateKeyPEM);
                KeyFactory keyFactory = KeyFactory.getInstance("RSA");
                PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(encoded);
                privateKey = keyFactory.generatePrivate(keySpec);
                return privateKey;
            } catch (Exception e) {
                throw new RuntimeException("Failed to load private key", e);
            }
        }
    }
}