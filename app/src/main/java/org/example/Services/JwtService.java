package org.example.Services;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Base64;
import java.util.function.Function;

@Service
public class JwtService {


    public static final String SECRET_KEY = "whyso$erious";

    public String extractUsername(String token){
        return extractClaim(token, Claims::getSubject);
    }

    private <T> T extractClaim(String token, Function<Claims, T> claimResolver ) {
       final Claims claims = extractAllClaims(token);
    }

    private Claims extractAllClaims(String token){
       return Jwts
               .parser()
               .setSigningKey(getSignKey())
               .build()
               .parseClaimsJws(token)
               .getBody();

    }

    private Key getSignKey(){
        byte[] keyBytes = Decoders.BASE64.decode(SECRET_KEY);
        return Keys.hmacShaKeyFor(keyBytes);
    }


}
