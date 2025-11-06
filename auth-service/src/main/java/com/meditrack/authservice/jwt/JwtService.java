package com.meditrack.authservice.jwt;


import com.meditrack.authservice.entity.UserEntity;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;


/**
 * This jwt service
 * i) creates jwt token
 * ii) validates them
 * iii) extract Claims from them
 */
@Service
public class JwtService {

    //will be used for signing jwts
    //This key changes on every server restart — in real applications, you’d want to persist this in config or env var.
    //private String secretkey = "";
    private final SecretKey secretKey;

    //It uses HmacSHA256 to generate a symmetric key (used for signing JWTs).
    public JwtService(@Value("${jwt.secret}")String secret ){
        byte[] keyBytes = Base64.getDecoder().decode(secret);
        this.secretKey = Keys.hmacShaKeyFor(keyBytes);
    }

    public String generateToken(String userName, UserEntity currentUser){
        Map<String,Object> claims = new HashMap<>();
        claims.put("id",currentUser.getId());
        claims.put("emailId",currentUser.getEmailId());
        //claims.put("roles",currentUser.get()); todo - need to add other relevant claims

        return Jwts.builder()
                .claims()
                .add(claims)
                .subject(userName)
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis()+60*30*1000))
                .and()
                .signWith(getKey())
                .compact();
    }

    private SecretKey getKey() {
        return secretKey;
    }

    public String extractUserName(String token) {
        // extract the username from jwt token
        return extractClaim(token, Claims::getSubject);
    }

    private <T> T extractClaim(String token, Function<Claims, T> claimResolver) {
        final Claims claims = extractAllClaims(token);
        return claimResolver.apply(claims);
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parser()
                .verifyWith(getKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    public boolean validateToken(String token, UserDetails userDetails) {
//        final String userName = extractUserName(token);
//        return (userName.equals(userDetails.getUsername()) && !isTokenExpired(token));
        //todo check logic of .parseSignedClaims
        try {
            Jwts.parser().verifyWith((SecretKey) secretKey)
                    .build()
                    .parseSignedClaims(token);
        } catch (JwtException e) {
            return false;
        } catch (Exception e) {
            return false;
        }
        return !isTokenExpired(token);
    }

    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    private Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

}


