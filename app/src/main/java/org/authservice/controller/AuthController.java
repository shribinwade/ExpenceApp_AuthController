package org.authservice.controller;

import org.authservice.Services.JwtService;
import org.authservice.Services.RefreshTokenService;
import org.authservice.Services.UserDetailsServiceImpl;
import org.authservice.entities.RefreshToken;
import org.authservice.models.UserInfoDTO;
import org.authservice.response.JwtResponseDTO;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("auth/v1")
public class AuthController {

    private JwtService jwtService;
    private RefreshTokenService refreshTokenService;
    private UserDetailsServiceImpl userDetailsServiceImpl;


    public AuthController(JwtService jwtService, RefreshTokenService refreshTokenService, UserDetailsServiceImpl userDetailsServiceImpl) {
        this.jwtService = jwtService;
        this.refreshTokenService = refreshTokenService;
        this.userDetailsServiceImpl = userDetailsServiceImpl;
    }

    @PostMapping("/signup")
    public ResponseEntity SignUp(@RequestBody UserInfoDTO userInfoDTO){
          try{
              Boolean isSignUp = userDetailsServiceImpl.signupUser(userInfoDTO);
              if(Boolean.FALSE.equals(isSignUp)){
                  return new ResponseEntity<>("already Exist", HttpStatus.BAD_REQUEST);
              }
              RefreshToken refreshToken = refreshTokenService.createRefreshToken(userInfoDTO.getUsername());
              String jwtToken = jwtService.GenerateToken(userInfoDTO.getUsername());
              return new ResponseEntity<>(JwtResponseDTO.builder().accessToken(jwtToken).token(refreshToken.getToken()).build(),HttpStatus.OK);
          }catch (Exception ex){
              return new ResponseEntity<>("Exception in User Service",HttpStatus.INTERNAL_SERVER_ERROR);
          }
    }

    @GetMapping("/ping")
    public ResponseEntity<String> ping (){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if(authentication != null && authentication.isAuthenticated()){
            return ResponseEntity.ok("Pong");
        }else{
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Unthorized");
        }

    }


}
