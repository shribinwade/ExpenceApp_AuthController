package org.example.Services;

import org.example.Repository.UserRepository;
import org.example.entities.UserInfo;
import org.example.models.UserInfoDTO;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

public class UserDetailsServiceImpl implements UserDetailsService {


    private final UserRepository userRepository;

    private final PasswordEncoder passwordEncoder;

    public UserDetailsServiceImpl(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        Optional<UserInfo> user = userRepository.findByUsername(username);
        if(user.isEmpty()){
            throw new UsernameNotFoundException("could not found user...!!");
        }
        return new CustomUserDetails(user.get());
    }

    public UserInfo checkIfUserAlreadyExists(UserInfoDTO userInfoDTO){
       return userRepository.findByUsername(userInfoDTO.getUsername())
               .orElseThrow(()-> new UsernameNotFoundException("user not already Exists"));

    }

    public Boolean signupUser(UserInfoDTO userInfoDTo){

    }


}
