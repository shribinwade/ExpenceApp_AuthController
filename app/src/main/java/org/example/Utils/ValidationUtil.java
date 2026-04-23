package org.example.Utils;

import org.example.models.UserInfoDTO;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.regex.Pattern;

@Service
public class ValidationUtil {

    //Regex Patterns
    private static final Pattern EMAIL_PATTERN =
          Pattern.compile("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$");

    // At least 8 chars, 1 uppercase, 1 lowercase, 1 digit, 1 special char
    private static final Pattern PASSWORD_PATTERN =
            Pattern.compile("^(?=.*[A-Z])(?=.*[a-z])(?=.*\\d)(?=.*[@#$%^&+=]).{8,}$");

    private static final Pattern PHONE_PATTERN =
            Pattern.compile("^[0-9]{10}$");

    private static final Pattern NAME_PATTERN =
            Pattern.compile("^[A-Za-z ]{2,50}$");

    public List<String> validateUser(UserInfoDTO userInfoDTO){
       List<String> errors = new ArrayList<>();
       addError(errors,validateField(userInfoDTO, UserInfoDTO::getEmail,EMAIL_PATTERN,"Invalid Email"));
       addError(errors, validateField(userInfoDTO, UserInfoDTO::getPassword, PASSWORD_PATTERN, "Invalid Password"));
       addError(errors, validateField(userInfoDTO, UserInfoDTO::getPhoneNumber, PHONE_PATTERN, "Invalid Phone"));
       addError(errors, validateField(userInfoDTO, UserInfoDTO::getUsername, NAME_PATTERN, "Invalid Name"));
       return errors;
    }

    private <T> String validateField(T obj,
                                     Function<T,String> extractor,
                                     Pattern pattern,
                                     String errorMsg) {
         String value = extractor.apply(obj);
         if(value == null || !pattern.matcher(value).matches()){
             return errorMsg;
         }
         return null;
    }

    private void addError(List<String> errors, String error){
        if(error!= null) errors.add(error);
    }

}
