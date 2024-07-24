package com.example.SpringBootSecurity.service;


import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwt;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Service
public class JwtService {

    @Value("${jwt.key}")
    private String SECRET_KEY;

    private int EXPIRE_TIME = 1000 * 60 * 2;

   public boolean validateToken(String token,UserDetails userDetails){
            String username = extractUser(token);
            Date expirationDate = extractExpiration(token);
            return userDetails.getUsername().equals(username) && !expirationDate.before(new Date());
   }
    public Date extractExpiration(String token){
        Claims claims = Jwts
                .parserBuilder()
                .setSigningKey(generateKey())
                .build()
                .parseClaimsJwt(token)
                .getBody();
          return claims.getExpiration();
    }
    public String extractUser(String token){
        Claims claims = Jwts
                .parserBuilder()
                .setSigningKey(generateKey())
                .build()
                .parseClaimsJwt(token)
                .getBody();

        return claims.getSubject();
    }
    public String generateToken(String username){
        Map<String,Object> claims = new HashMap<>();
        return Jwts.builder()
                .setSubject(username)
                .setClaims(claims)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRE_TIME ))
                .signWith(generateKey(), SignatureAlgorithm.HS256)
                .compact();
    }
    public Key generateKey(){
        byte[] keyBytes = Decoders.BASE64.decode(SECRET_KEY);
        return Keys.hmacShaKeyFor(keyBytes);
    }


}
