package org.authservice.controller;

import org.authservice.Services.JwtService;
import org.authservice.Services.RefreshTokenService;
import org.authservice.entities.RefreshToken;
import org.authservice.request.AuthRequestDTO;
import org.authservice.request.RefreshTokenRequestDTO;
import org.authservice.response.JwtResponseDTO;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.awt.print.Printable;
@CrossOrigin(origins = "*")
@RestController
@RequestMapping("auth/v1")
public class TokenController {


    private AuthenticationManager authenticationManager;

    private RefreshTokenService refreshTokenService;

    private JwtService jwtService;

    public TokenController(AuthenticationManager authenticationManager, RefreshTokenService refreshTokenService, JwtService jwtService) {
        this.authenticationManager = authenticationManager;
        this.refreshTokenService = refreshTokenService;
        this.jwtService = jwtService;
    }

    @PostMapping("/login")
    public ResponseEntity AuthenticateAndGetToken(@RequestBody AuthRequestDTO authRequestDTO){
        Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(authRequestDTO.getUsername(), authRequestDTO.getPassword()));
       if(authentication.isAuthenticated()){
           RefreshToken refreshToken = refreshTokenService.createRefreshToken(authRequestDTO.getUsername());
           return new ResponseEntity<>(JwtResponseDTO.builder().accessToken(jwtService.GenerateToken(authRequestDTO.getUsername()))
                   .token(refreshToken.getToken()).build(), HttpStatus.OK);
       }else{
           return new ResponseEntity("Exception in User Service",HttpStatus.INTERNAL_SERVER_ERROR);
       }
    }

    @PostMapping("/refreshToken")
    public JwtResponseDTO refreshToken(@RequestBody RefreshTokenRequestDTO refreshTokenRequestDTO){
        return refreshTokenService.findByToken(refreshTokenRequestDTO.getToken())
                .map(refreshTokenService::verifyExpiration)
                .map(RefreshToken::getUserInfo)
                .map(userInfo -> {
                    System.out.print(userInfo);
                    String accessToken = jwtService.GenerateToken(userInfo.getUsername());
                    return JwtResponseDTO.builder().accessToken(accessToken).token(refreshTokenRequestDTO.getToken()).build();
                })
                .orElseThrow(()-> new RuntimeException("Refresh Token is not in DB ..!!"));
    }

}
