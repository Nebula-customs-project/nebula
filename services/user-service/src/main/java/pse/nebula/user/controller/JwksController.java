package pse.nebula.user.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigInteger;
import java.security.KeyFactory;
import java.security.PublicKey;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/users/.well-known")
public class JwksController {

    @Value("${jwt.test-public-key}")
    private String publicKeyPem;

    @GetMapping(value = "/jwks.json", produces = MediaType.APPLICATION_JSON_VALUE)
    public Map<String, Object> getJwks() throws Exception {
        PublicKey publicKey = loadPublicKeyFromPem(publicKeyPem);
        byte[] encoded = publicKey.getEncoded();
        String base64 = Base64.getEncoder().encodeToString(encoded);

        // Parse modulus and exponent from the key
        java.security.interfaces.RSAPublicKey rsaKey = (java.security.interfaces.RSAPublicKey) publicKey;
        String n = Base64.getUrlEncoder().withoutPadding().encodeToString(rsaKey.getModulus().toByteArray());
        String e = Base64.getUrlEncoder().withoutPadding().encodeToString(rsaKey.getPublicExponent().toByteArray());

        Map<String, Object> jwk = new HashMap<>();
        jwk.put("kty", "RSA");
        jwk.put("kid", "key-1");
        jwk.put("use", "sig");
        jwk.put("alg", "RS256");
        jwk.put("n", n);
        jwk.put("e", e);

        Map<String, Object> jwks = new HashMap<>();
        jwks.put("keys", new Object[]{jwk});
        return jwks;
    }

    private PublicKey loadPublicKeyFromPem(String pemKey) throws Exception {
        String publicKeyPEM = pemKey
                .replace("-----BEGIN PUBLIC KEY-----", "")
                .replace("-----END PUBLIC KEY-----", "")
                .replaceAll("\\s", "");
        byte[] encoded = Base64.getDecoder().decode(publicKeyPEM);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        X509EncodedKeySpec keySpec = new X509EncodedKeySpec(encoded);
        return keyFactory.generatePublic(keySpec);
    }
}
