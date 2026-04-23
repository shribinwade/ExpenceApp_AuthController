package org.example.controller;

import lombok.AllArgsConstructor;
import org.example.Services.JwtService;
import org.example.Services.RefreshTokenService;
import org.example.Services.UserDetailsServiceImpl;
import org.example.entities.RefreshToken;
import org.example.models.UserInfoDTO;
import org.example.response.JwtResponseDTO;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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


}
