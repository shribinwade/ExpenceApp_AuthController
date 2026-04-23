package org.example.auth;

import lombok.Data;
import org.example.Repository.UserRepository;
import org.example.Services.UserDetailsServiceImpl;
import org.example.Utils.ValidationUtil;
import org.example.eventProducer.UserInfoProducer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.CorsConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableMethodSecurity
@Data
public class SecurityConfig {

    private final PasswordEncoder passwordEncoder;
    private final UserDetailsServiceImpl userDetailsServiceImpl;
    private final ValidationUtil validationUtil;
    private final UserInfoProducer userInfoProducer;

    public SecurityConfig(UserDetailsServiceImpl userDetailsServiceImpl, PasswordEncoder passwordEncoder,ValidationUtil validationUtil, UserInfoProducer userInfoProducer) {
        this.userDetailsServiceImpl = userDetailsServiceImpl;
        this.validationUtil = validationUtil;
        this.passwordEncoder = passwordEncoder;
        this.userInfoProducer = userInfoProducer;
    }

    @Bean
    public UserDetailsService userDetailsService(UserRepository userRepository, PasswordEncoder passwordEncoder,ValidationUtil validationUtil, UserInfoProducer userInfoProducer){
        return new UserDetailsServiceImpl(userRepository,passwordEncoder,validationUtil,userInfoProducer);
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http,JwtAuthFilter jwtAuthFilter) throws Exception{
      return  http.csrf(AbstractHttpConfigurer::disable).cors(CorsConfigurer::disable)
              .authorizeHttpRequests(
                      auth -> auth
                              .requestMatchers("/auth/v1/login","/auth/v1/refreshToken","/auth/v1/signup").permitAll()
                              .anyRequest().authenticated()
              )
              .sessionManagement(sess -> sess.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
              .httpBasic(Customizer.withDefaults())
              .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class)
              .authenticationProvider(authenticationProvider())
              .build();
    }

    @Bean
    public AuthenticationProvider authenticationProvider(){
        DaoAuthenticationProvider authenticationProvider = new DaoAuthenticationProvider();
        authenticationProvider.setUserDetailsService(userDetailsServiceImpl);
        authenticationProvider.setPasswordEncoder(passwordEncoder);
        return authenticationProvider;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }
}
