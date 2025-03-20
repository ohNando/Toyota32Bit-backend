package com.toyota.toyotabackend.restapi.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SecurityException;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Component
public class JwtUtil {
    @Value("${jwt.secret-key}")
    private String secret;
    @Value("${jwt.expiration-time}")
    private long expirationTime;
    private SecretKey key;

    @PostConstruct
    public void init(){
        this.key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }

    public String generateToken(String username) {
        return Jwts.builder()
                .setSubject(username)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + expirationTime))
                .signWith(key)
                .compact();
    }

    public String getUsernameFromToken(String token) {
        return Jwts.parser().verifyWith(key).build().parseSignedClaims(token).getBody().getSubject();
    }

    public boolean validateToken(String token) {
        try{
            Jwts.parser().setSigningKey(key).build().parseClaimsJws(token);
            return true;
        }catch (SecurityException error){
            System.out.println("Invalid JWT Signature : " + error.getMessage());
        }catch (MalformedJwtException error){
            System.out.println("Invalid JWT Token : " + error.getMessage());
        }catch (ExpiredJwtException error){
            System.out.println("JWT Token is expired : " + error.getMessage());
        }catch (IllegalArgumentException error){
            System.out.println("JWT Token is null : " + error.getMessage());
        }
        return false;
    }


}
