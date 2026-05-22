package org.authservice.Services;

import org.authservice.Exception.custom.ValidationException;
import org.authservice.Repository.UserRepository;
import org.authservice.Utils.ValidationUtil;
import org.authservice.entities.UserInfo;
import org.authservice.eventProducer.UserInfoEvent;
import org.authservice.eventProducer.UserInfoProducer;
import org.authservice.models.UserInfoDTO;
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


    public String getUserByUsername(String username) throws UsernameNotFoundException {
        Optional<UserInfo> user = userRepository.findByUsername(username);
        if(user.isEmpty()){
            throw new UsernameNotFoundException("could not found user...!!");
        }
        return user.get().getUserId();
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
        UserInfo userInfo = new UserInfo(userId, userInfoDTo.getUsername(),
                userInfoDTo.getPassword(), new HashSet<>());
        userRepository.save(userInfo);
        // pushEventToQueue
        userInfoProducer.sendEventToKafka(userInfoEventToPublish(userInfoDTo,userId));
        return true;
    }

    private UserInfoEvent userInfoEventToPublish(UserInfoDTO userInfoDTO, String userId){
         return UserInfoEvent.builder()
                 .userId(userId)
                 .firstName(userInfoDTO.getFirstName())
                 .lastName(userInfoDTO.getLastName())
                 .email(userInfoDTO.getEmail())
                 .phoneNumber(Long.valueOf(userInfoDTO.getPhoneNumber()))
                 .build();
    }



}
