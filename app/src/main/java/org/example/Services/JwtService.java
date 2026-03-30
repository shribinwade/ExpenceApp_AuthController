package org.example.Services;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Base64;
import java.util.Date;
import java.util.Map;
import java.util.function.Function;

@Service
public class JwtService {


    public static final String SECRET_KEY = "O6OHQvJj3CqwJEAsOqA45JXChuTLTWTCzf1cfVrDfO0=";


    public String extractUsername(String token){

        return extractClaim(token, Claims::getSubject);
    }

    public Boolean isTokenExpired(String token){
        return extractExpiration(token).before(new Date());
    }

    public Date extractExpiration(String token){
        return extractClaim(token, Claims::getExpiration);
    };

    public Boolean validateToken(String token, UserDetails userDetails){
        final String username = extractUsername(token);
        return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
    }


    //create Token
    private String createToken(Map<String,Object> claims , String username){
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(username)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis()+1000*60*1))
                .signWith(getSignKey(), SignatureAlgorithm.ES256).compact();
    }


    private <T> T extractClaim(String token, Function<Claims, T> claimResolver ) {
       final Claims claims = extractAllClaims(token);
       return claimResolver.apply(claims);
    };

    private Claims extractAllClaims(String token){
       return Jwts
               .parser()
               .setSigningKey(getSignKey())
               .build()
               .parseClaimsJws(token)
               .getBody();

    }

    //Decodeing Secret
    private Key getSignKey(){
        byte[] keyBytes = Decoders.BASE64.decode(SECRET_KEY);
        //returning new encrypt secret key
        return Keys.hmacShaKeyFor(keyBytes);
    }


}
