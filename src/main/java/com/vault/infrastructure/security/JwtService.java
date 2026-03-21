package com.vault.infrastructure.security;

import com.vault.domain.model.user.Email;
import com.vault.domain.model.user.UserId;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.Date;

@Component
public class JwtService {

    private final RsaKeyProvider rsaKeyProvider;
    private final String issuer;
    private final long accessTokenExpirationMinutes;

    public JwtService(RsaKeyProvider rsaKeyProvider,
                      @Value("${app.security.jwt.issuer:password-vault}") String issuer,
                      @Value("${app.security.jwt.access-token-expiration-minutes:15}") long accessTokenExpirationMinutes) {
        this.rsaKeyProvider = rsaKeyProvider;
        this.issuer = issuer;
        this.accessTokenExpirationMinutes = accessTokenExpirationMinutes;
    }

    public String generateToken(UserId userId, Email email) {
        var now = Instant.now();
        var expiry = now.plusSeconds(accessTokenExpirationMinutes * 60);
        return Jwts.builder()
                .subject(userId.value().toString())
                .claim("email", email.value())
                .issuer(issuer)
                .issuedAt(Date.from(now))
                .expiration(Date.from(expiry))
                .signWith(rsaKeyProvider.privateKey())
                .compact();
    }

    public Claims parseToken(String token) {
        return Jwts.parser()
                .verifyWith(rsaKeyProvider.publicKey())
                .requireIssuer(issuer)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    public UserId extractUserId(String token) {
        return UserId.fromString(parseToken(token).getSubject());
    }

    public long accessTokenExpirationSeconds() {
        return accessTokenExpirationMinutes * 60;
    }
}
