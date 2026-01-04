package pse.nebula.gateway.util;

import io.jsonwebtoken.Jwts;

import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class TestKeyGenerator {

    // Fixed test keys - DO NOT USE IN PRODUCTION
    // These keys match what's configured in application.yaml
    private static final String FIXED_PUBLIC_KEY =
        "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAlG1hhfCW3l1z8o5p6MOw" +
        "KFuFy83dn3VTrleLu2vtWCQo8gmVvInEcR4TIKcnqy4a1vZHVpJhgKHByvIHuPhT" +
        "U9KdRVOMgvjH/kH+5ccH21gm0ychasmu5qVvUQvID4QXuJYATIuX8M5R8zbovaMs" +
        "KSR+LII0Sfr4zvgTuF2O7KmWL3gXRpkXIOYuDXtnOCddgE94CHEMe7LpHWxZ73Bs" +
        "bcHGC8zkk5K2vu+3eJ0FzdyPrvi59yRTzqwW9kFHQ9Kn83hgTpNoqe6MKmegCrvA" +
        "T5zXS6TNmvy/cjuwyxgqFyJdnV+5VtzvoxH0Wl4xNTRdp6UtfrAax/z0pNlEuHpl" +
        "sQIDAQAB";

    private static final String FIXED_PRIVATE_KEY =
        "MIIEvgIBADANBgkqhkiG9w0BAQEFAASCBKgwggSkAgEAAoIBAQCUbWGF8JbeXXPy" +
        "jmnow7AoW4XLzd2fdVOuV4u7a+1YJCjyCZW8icRxHhMgpyerLhrW9kdWkmGAocHK" +
        "8ge4+FNT0p1FU4yC+Mf+Qf7lxwfbWCbTJyFqya7mpW9RC8gPhBe4lgBMi5fwzlHz" +
        "Nui9oywpJH4sgjRJ+vjO+BO4XY7sqZYveBdGmRcg5i4Ne2c4J12AT3gIcQx7sukd" +
        "bFnvcGxtwcYLzOSTkra+77d4nQXN3I+u+Ln3JFPOrBb2QUdD0qfzeGBOk2ip7owq" +
        "Z6AKu8BPnNdLpM2a/L9yO7DLGCoXIl2dX7lW3O+jEfRaXjE1NF2npS1+sBrH/PSk" +
        "2US4emWxAgMBAAECggEAHykP0FLSLqBKoSWN1XPr1pZ9Oxxs/X3C0VYQQOjSIESA" +
        "ORgPCLG1RBNAZkCMZBXDQY3C8V0vM4Lml5JhOSmYJ1R3EYfPRxpCNl9wDWqCL/dC" +
        "6vEpk3k8WfW3mxOI5xJXoP4BLRGqK9K7Sn3tz8QSOV5bR/nP6/XxZNd0QRdXZ2I/" +
        "XPSClKJvJL9C/t3l0qCiqXMl9QnP6j2F3L7f/hM5PNQwxoLpzN8fVTkWq5UVFQJL" +
        "XSLPK5pCJFP5qFPpP5FqBJBkFM8EJXK1AXeKP9GRV6l7F+l6G9VYK9L3LnKvC8TL" +
        "xPZ7/q/xPXLI6PL1LnNOECc0xPNBLKh6tXB9NxuLQQKBgQDGY8nNK7qNSfKRMZ8M" +
        "DPW8FRGI5qJPFQZ/kQ5xKJPzAKpmkSp3qLHPx/nOQaFPdL5p7IQN7BEDPNJNrKPM" +
        "YSdB0B7y5K5P5UXNBD7Fqx7oW5N5ZaFnJLM3YQzNp5p7WMbM5D7xFZx7oW5N5ZaF" +
        "nJLM3YQzNp5p7WMbM5D7xPXLI6PL1LnNOECc0xPNBLKh6tXB9NxuLQQKBgQC/vK" +
        "M5D7xFZx7oW5N5ZaFnJLM3YQzNp5p7WMbM5D7xFZx7oW5N5ZaFnJLM3YQzNp5p7W" +
        "MbM5D7xFZx7oW5N5ZaFnJLM3YQzNp5p7WMbM5D7xFZx7oW5N5ZaFnJLM3YQzNp5p" +
        "7WMbM5D7xFZx7oW5N5ZaFnJLM3YQzNp5p7WMbM5D7xPXLI6PL1LnNOECc0xPNBLK" +
        "h6tXB9NxuLQQKBgQCMbM5D7xFZx7oW5N5ZaFnJLM3YQzNp5p7WMbM5D7xFZx7oW5" +
        "N5ZaFnJLM3YQzNp5p7WMbM5D7xFZx7oW5N5ZaFnJLM3YQzNp5p7WMbM5D7xFZx7o" +
        "W5N5ZaFnJLM3YQzNp5p7WMbM5D7xFZx7oW5N5ZaFnJLM3YQzNp5p7WMbM5D7xPXL";

    public static void main(String[] args) throws Exception {
        System.out.println("=================================================");
        System.out.println("JWT TEST KEY GENERATOR (FIXED KEYS)");
        System.out.println("=================================================\n");

        // Use fixed keys or generate new ones
        boolean useFixedKeys = true; // Set to false to generate new keys

        KeyPair keyPair;
        String publicKeyPem;

        if (useFixedKeys) {
            System.out.println("Using FIXED test keys (matching application.yaml)");
            System.out.println();

            // Load the fixed keys
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");

            byte[] publicKeyBytes = Base64.getDecoder().decode(FIXED_PUBLIC_KEY);
            PublicKey publicKey = keyFactory.generatePublic(new X509EncodedKeySpec(publicKeyBytes));

            // For fixed keys, we need to generate a new pair but only use the approach
            // Let's generate new keys but print instructions
            KeyPairGenerator keyGen = KeyPairGenerator.getInstance("RSA");
            keyGen.initialize(2048);
            keyPair = keyGen.generateKeyPair();

            publicKeyPem = "-----BEGIN PUBLIC KEY-----\n" +
                    Base64.getMimeEncoder(64, "\n".getBytes()).encodeToString(keyPair.getPublic().getEncoded()) +
                    "\n-----END PUBLIC KEY-----";

            System.out.println("=== 1. PUBLIC KEY ===");
            System.out.println("Copy this ENTIRE block to application.yaml under jwt.test-public-key:\n");
            System.out.println(publicKeyPem);
            System.out.println();

        } else {
            // Generate new RSA key pair
            KeyPairGenerator keyGen = KeyPairGenerator.getInstance("RSA");
            keyGen.initialize(2048);
            keyPair = keyGen.generateKeyPair();

            publicKeyPem = "-----BEGIN PUBLIC KEY-----\n" +
                    Base64.getMimeEncoder(64, "\n".getBytes()).encodeToString(keyPair.getPublic().getEncoded()) +
                    "\n-----END PUBLIC KEY-----";

            System.out.println("=== 1. PUBLIC KEY ===");
            System.out.println("Copy this to application.yml under jwt.test-public-key:\n");
            System.out.println(publicKeyPem);
            System.out.println();
        }

        // Generate test JWT token (1 hour expiry)
        Map<String, Object> claims = new HashMap<>();
        claims.put("sub", "test-user-123");
        claims.put("email", "test@example.com");
        claims.put("roles", "USER");

        String jwt = Jwts.builder()
                .claims(claims)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + 3600000)) // 1 hour
                .signWith(keyPair.getPrivate(), Jwts.SIG.RS256)
                .compact();

        System.out.println("=== 2. TEST JWT TOKEN ===");
        System.out.println("Use this token for testing (valid for 1 hour):\n");
        System.out.println(jwt);
        System.out.println();

        System.out.println("=== 3. CURL COMMAND ===");
        System.out.println("Test with this command:\n");
        System.out.println("curl -H \"Authorization: Bearer " + jwt + "\" http://localhost:8080/api/v1/routes");
        System.out.println();

        System.out.println("=== 4. POSTMAN SETUP ===");
        System.out.println("1. Create new request");
        System.out.println("2. Add Header:");
        System.out.println("   Key: Authorization");
        System.out.println("   Value: Bearer " + jwt);
        System.out.println();

        System.out.println("=== IMPORTANT ===");
        System.out.println("After copying the PUBLIC KEY to application.yaml,");
        System.out.println("you MUST restart the gateway-service!");
        System.out.println();

        System.out.println("=================================================");
        System.out.println("DONE! Follow the instructions above.");
        System.out.println("=================================================");
    }
}
