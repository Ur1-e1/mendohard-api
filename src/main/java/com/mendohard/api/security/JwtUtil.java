package com.mendohard.api.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Component
public class JwtUtil {

    private static final String SECRET_KEY_STRING = "MendoHardSecretKey2026MendoHardSecretKey2026MendoHard";
    // En 0.12.x se utiliza la interfaz nativa SecretKey
    private final SecretKey SECRET_KEY = io.jsonwebtoken.security.Keys.hmacShaKeyFor(SECRET_KEY_STRING.getBytes(StandardCharsets.UTF_8));

    private static final long EXPIRATION_TIME = 1000 * 60 * 60 * 24; // 24 horas

    public String generarToken(String email, String rol) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("rol", rol);

        return Jwts.builder()
                .claims(claims) // Cambiado: antes setClaims()
                .subject(email) // Cambiado: antes setSubject()
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
                .signWith(SECRET_KEY) // El algoritmo se autodetecta según el tipo de clave
                .compact();
    }

    public String extraerEmail(String token) {
        return extraerClaim(token, Claims::getSubject);
    }

    public String extraerRol(String token) {
        final Claims claims = extraerTodosLosClaims(token);
        return claims.get("rol", String.class);
    }

    public boolean esTokenValido(String token, String email) {
        final String emailToken = extraerEmail(token);
        return (emailToken.equals(email) && !esTokenExpirado(token));
    }

    private boolean esTokenExpirado(String token) {
        return extraerClaim(token, Claims::getExpiration).before(new Date());
    }

    public <T> T extraerClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extraerTodosLosClaims(token);
        return claimsResolver.apply(claims);
    }

    private Claims extraerTodosLosClaims(String token) {
        return Jwts.parser() // Cambiado: antes parserBuilder()
                .verifyWith(SECRET_KEY) // Cambiado: antes setSigningKey()
                .build()
                .parseSignedClaims(token) // Cambiado: antes parseClaimsJws()
                .getPayload(); // Cambiado: antes getBody()
    }
}