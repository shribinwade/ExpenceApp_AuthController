package org.example.models;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.*;
import org.example.entities.UserInfo;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserInfoDTO extends UserInfo {
    private String firstName;
    private String lastName;
    private String email;
    private String phoneNumber;
}
