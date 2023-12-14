package com.school.config.jwt;

import com.school.model.Person;

import com.school.repository.PersonRepository;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Component
public class JwtUtil {
    @Value(value = "${jwt.secretCode}")
    private String secretKey;

    @Value(value = "${jwt.expirationDate}")
    private Long expirationDate;

    private final PersonRepository personRepository;

    @Autowired
    public JwtUtil(PersonRepository personRepository) {
        this.personRepository = personRepository;
    }

    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    public Map<?,?> getData(String token) {
        Claims claims = Jwts
                .parserBuilder()
                .setSigningKey(getSignIngKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
        Map<String, String> data = new HashMap<>();

        data.put("username", claims.getSubject());
        data.put("role", (String) claims.get("role"));

        return data;
    }

    private <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    public String generateToken(Person person) {
        Map<String, Object> claims = new HashMap<>();

        claims.put("role", person.getRole().getName());

        return generateToken(claims, person);
    }

    public String generateToken(
            Map<String, Object> extraClaims, Person person
    ) {
        return Jwts
                .builder()
                .setClaims(extraClaims)
                .setIssuer("Dairy")
                .setSubject(person.getUsername())
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + expirationDate))
                .signWith(getSignIngKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    public boolean isTokenValid(String token) {
        final String username = extractUsername(token);
        return personRepository.findByUsername(username).isPresent() && !isTokenExpired(token);
    }

    public boolean isTokenExpired(String token) {
        try {
            return extractExpiration(token).before(new Date());
        } catch (ExpiredJwtException e) {
            return false;
        }
    }

    private Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    public Claims extractAllClaims(String token) {
        try {
            return Jwts
                    .parserBuilder()
                    .setSigningKey(getSignIngKey())
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
        } catch (MalformedJwtException e) {
            return null;
        }
    }

    private Key getSignIngKey() {
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}