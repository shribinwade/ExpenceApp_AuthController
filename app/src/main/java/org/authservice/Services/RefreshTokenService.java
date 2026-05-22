package org.authservice.Services;

import org.authservice.Repository.RefreshTokenRepository;
import org.authservice.Repository.UserRepository;
import org.authservice.entities.RefreshToken;
import org.authservice.entities.UserInfo;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

@Service
public class RefreshTokenService {


   private final RefreshTokenRepository refreshTokenRepository;

   private final  UserRepository userRepository;

    public RefreshTokenService(RefreshTokenRepository refreshTokenRepository, UserRepository userRepository) {
        this.refreshTokenRepository = refreshTokenRepository;
        this.userRepository = userRepository;
    }

    public RefreshToken createRefreshToken(String username){
        UserInfo userInfoExtreacted = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found with username: " + username));

        RefreshToken refreshToken = refreshTokenRepository
                .findByUserInfo(userInfoExtreacted)
                .orElse(new RefreshToken());

        refreshToken.setUserInfo(userInfoExtreacted);
        refreshToken.setToken(UUID.randomUUID().toString());
        refreshToken.setExpiryDate(Instant.now().plus(Duration.ofDays(7)));
          return refreshTokenRepository.save(refreshToken);

    }

    public RefreshToken verifyExpiration(RefreshToken token){
        if(token.getExpiryDate().compareTo(Instant.now()) < 0){
            refreshTokenRepository.delete(token);
            throw new RuntimeException(token.getToken()+"Refresh token is expired. Please make a new Login...!");
        }
        return token;
    }

    public Optional<RefreshToken> findByToken(String token){
        return refreshTokenRepository.findByToken(token);
    }
}
