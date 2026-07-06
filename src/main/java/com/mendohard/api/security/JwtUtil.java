package com.mendohard.api.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Component
public class JwtUtil {

    private final SecretKey secretKey;
    private final long expirationTime;

    // Los valores se inyectan dinámicamente desde el archivo properties al instanciarse el componente
    public JwtUtil(
            @Value("${mendohard.security.jwt.secret}") String secretKeyString,
            @Value("${mendohard.security.jwt.expiration-ms}") long expirationTime) {
        this.secretKey = io.jsonwebtoken.security.Keys.hmacShaKeyFor(secretKeyString.getBytes(StandardCharsets.UTF_8));
        this.expirationTime = expirationTime;
    }

    public String generarToken(String email, String rol) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("rol", rol);

        return Jwts.builder()
                .claims(claims)
                .subject(email)
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + expirationTime)) // Usa la variable inyectada
                .signWith(secretKey)
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
                .verifyWith(secretKey) // Cambiado: antes setSigningKey()
                .build()
                .parseSignedClaims(token) // Cambiado: antes parseClaimsJws()
                .getPayload(); // Cambiado: antes getBody()
    }
}