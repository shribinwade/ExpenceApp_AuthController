package org.example.Services;

import org.example.Exception.custom.ValidationException;
import org.example.Repository.UserRepository;
import org.example.Utils.ValidationUtil;
import org.example.entities.UserInfo;
import org.example.eventProducer.UserInfoProducer;
import org.example.models.UserInfoDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {


    private final UserRepository userRepository;
    private final ValidationUtil validationUtil;
    private final PasswordEncoder passwordEncoder;
    private final UserInfoProducer userInfoProducer;

    public UserDetailsServiceImpl(UserRepository userRepository, PasswordEncoder passwordEncoder,  ValidationUtil validationUtil,UserInfoProducer userInfoProducer) {
        this.userRepository = userRepository;
        this.validationUtil = validationUtil;
        this.passwordEncoder = passwordEncoder;
        this.userInfoProducer = userInfoProducer;
    }



    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        Optional<UserInfo> user = userRepository.findByUsername(username);
        if(user.isEmpty()){
            throw new UsernameNotFoundException("could not found user...!!");
        }
        return new CustomUserDetails(user.get());
    }

    public Optional<UserInfo> checkIfUserAlreadyExists(UserInfoDTO userInfoDTO){
       return userRepository.findByUsername(userInfoDTO.getUsername());

    }

    public Boolean signupUser(UserInfoDTO userInfoDTo){
        //Define a function to check if userEmail,password is correct
        //we need to check validation
        List<String> errors = validationUtil.validateUser(userInfoDTo);
        if(!errors.isEmpty()){
          // errors exist -> stop flow
            throw new ValidationException(errors);
        }

        userInfoDTo.setPassword(passwordEncoder.encode(userInfoDTo.getPassword()));
        if(checkIfUserAlreadyExists(userInfoDTo).isPresent()){
            return false;
        }
        String userId = UUID.randomUUID().toString();
        userRepository.save(new UserInfo(userId,userInfoDTo.getUsername(),
                userInfoDTo.getPassword(), new HashSet<>()));
        // pushEventToQueue
        userInfoProducer.sendEventToKafka(userInfoDTo);
        return true;
    }


}
