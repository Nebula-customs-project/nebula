package pse.nebula.user.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import pse.nebula.user.model.User;

@Data
@AllArgsConstructor
public class AuthenticationResult {
    private String token;
    private String refreshToken;
    private long accessTokenTtl;
    private long refreshTokenTtl;
    private User user;
}
