package org.authservice.models;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.*;
import org.authservice.entities.UserInfo;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class UserInfoDTO extends UserInfo {

    @NonNull
    private String firstName; //first_name
    @NonNull
    private String lastName; //last_name
    @NonNull
    private String email;
    @NonNull
    private String phoneNumber; //phone_number
}
