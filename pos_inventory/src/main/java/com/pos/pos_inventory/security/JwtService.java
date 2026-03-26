package com.pos.pos_inventory.security;

import com.pos.user.entity.User;
import com.pos.user.exception.JwtAuthenticationException;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Service
@Slf4j
@RequiredArgsConstructor
public class JwtService {

    @Value("${jwt.secret}")
    private String secretKey;

    @Value("${jwt.expiration}")
    private long expiration;

    private SecretKey getSecretKey(){
        try {
            if(secretKey == null || secretKey.isEmpty()){
                throw new IllegalStateException("JWT secret key is not configured properly");
            }
            return Keys.hmacShaKeyFor(secretKey.getBytes());
        }catch (Exception e){
            log.error("failed to generate secret key: {}", e.getMessage());
            throw new IllegalStateException("failed to initialize jwt secret key", e);
        }
    }

    private Date generateExpirationDate(){
        try {
            return new Date(System.currentTimeMillis() + expiration);
        } catch (Exception e){
            log.error("Failed to generate expiration date: {}", e.getMessage());
            throw new IllegalStateException("Failed to generate token expiration date", e);
        }
    }

    private String generateToken(String username, Map<String, Object> claims){
        try {
            String token = Jwts.builder()
                    .setIssuedAt(new Date())
                    .setIssuer("POS user microservice")
                    .setSubject(username)
                    .addClaims(claims)
                    .setExpiration(generateExpirationDate())
                    .signWith(getSecretKey(), SignatureAlgorithm.HS256)
                    .compact();

            log.debug("Generated JWT token for user: {}, expires at: {}", username, generateExpirationDate());

            return token;
        } catch (JwtException e) {
            log.error("Failed to generate JWT token for user {}: {}", username, e.getMessage());
            throw new JwtAuthenticationException("Failed to generate authentication token");
        }
    }

    public String generateToken(User user){
        Map<String, Object> claims = new HashMap<>();
        claims.put("role", "ROLE_"+user.getRole());
        claims.put("userId", user.getId());
        claims.put("branchId", user.getBranchId());
        return generateToken(user.getUsername(), claims);
    }

    private Claims extractAllClaims(String token) {
        try {
            return Jwts.parserBuilder()
                    .setSigningKey(getSecretKey())
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
        } catch (ExpiredJwtException e) {
            log.error("JWT token is expired: {}", e.getMessage());
            throw new JwtAuthenticationException("Token has expired");
        } catch (UnsupportedJwtException e) {
            log.error("Unsupported JWT token: {}", e.getMessage());
            throw new JwtAuthenticationException("Unsupported token format");
        } catch (MalformedJwtException e) {
            log.error("Malformed JWT token: {}", e.getMessage());
            throw new JwtAuthenticationException("Invalid token format");
        } catch (SecurityException e) {
            log.error("Invalid JWT signature: {}", e.getMessage());
            throw new JwtAuthenticationException("Invalid token signature");
        } catch (IllegalArgumentException e) {
            log.error("JWT token is empty or null: {}", e.getMessage());
            throw new JwtAuthenticationException("Token is empty or null");
        }
    }

    public String extractUsername(String token){
        return extractAllClaims(token).getSubject();
    }

    public String extractRole(String token){
        return extractAllClaims(token).get("role", String.class);
    }

    public Integer extractBranchId(String token) { return (Integer) extractAllClaims(token).get("branchId");
    }
    public Integer extractUserId(String token) {
        return (Integer) extractAllClaims(token).get("userId");
    }

    public boolean isTokenExpired(String token){
        return extractExpiration(token).before(new Date());
    }

    public Date extractExpiration(String token){
        return extractAllClaims(token).getExpiration();
    }

    public boolean isTokenValid(String token, UserDetails userDetails) {
        final String username = extractUsername(token);
        return (username.equals(userDetails.getUsername())) && !isTokenExpired(token);
    }
}
