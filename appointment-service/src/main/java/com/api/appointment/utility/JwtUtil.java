package com.api.appointment.utility;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import java.security.Key;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class JwtUtil {

  @Value("${jwt.secret:default_secret_key_at_least_32_characters}")
  private String SECRET;

  private Key getSigningKey() {
    return Keys.hmacShaKeyFor(SECRET.getBytes());
  }

  public Claims extractAllClaims(String token) {
    return Jwts.parserBuilder()
      .setSigningKey(getSigningKey())
      .build()
      .parseClaimsJws(token)
      .getBody();
  }

  public String extractUsername(String token) {
    return extractAllClaims(token).getSubject();
  }

  public String extractRole(String token) {
    return extractAllClaims(token).get("role", String.class);
  }

  public boolean isTokenExpired(String token) {
    return extractAllClaims(token).getExpiration().before(new java.util.Date());
  }

  public boolean validateToken(String token) {
    try {
      return !isTokenExpired(token);
    } catch (Exception e) {
      return false;
    }
  }
}
