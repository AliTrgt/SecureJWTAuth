package com.example.JwtUtil.service;


import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
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


    @Value("{jwt.key}")
    private String SECRET_KEY;

    private int ACCES_TOKEN_EXPIRATION_DATE = 1000 * 3600 * 1;// 1 GÜN

    private int REFRESH_TOKEN_EXPIRATION_DATE = ACCES_TOKEN_EXPIRATION_DATE * 30; // 30 gün

    public boolean validateToken(String token, UserDetails userDetails){
            String username = extractUser(token);
            Date expirationDate = extractExpiration(token);

            return userDetails.getUsername().equals(username) && !expirationDate.before(new Date());
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


    public Date extractExpiration(String token){
        Claims claims = Jwts
                .parserBuilder()
                .setSigningKey(generateKey())
                .build()
                .parseClaimsJws(token)
                .getBody();

        return claims.getExpiration();
    }



    public String generateAccessToken(String username){
        Map<String,Object> claims = new HashMap<>();

        return Jwts
                .builder()
                .setClaims(claims)
                .setSubject(username)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + ACCES_TOKEN_EXPIRATION_DATE))
                .signWith(generateKey())
                .compact();
    }


    public String generateRefreshToken(String username){
            Map<String,Object> claims = new HashMap<>();
                return  Jwts
                        .builder()
                        .setClaims(claims)
                        .signWith(generateKey())
                        .setSubject(username)
                        .setIssuedAt(new Date(System.currentTimeMillis()))
                        .setExpiration(new Date(System.currentTimeMillis() + REFRESH_TOKEN_EXPIRATION_DATE))
                        .compact();
    }



    public Key generateKey(){
            byte[] hashKey = Decoders.BASE64.decode(SECRET_KEY);
            return Keys.hmacShaKeyFor(hashKey);
    }


}
