package org.example.serializer;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.common.serialization.Serializer;
import org.example.models.UserInfoDTO;

import java.nio.charset.StandardCharsets;

public class UserInfoSerializer implements Serializer<UserInfoDTO> {

    @Override
    public byte[] serialize(String topic, UserInfoDTO data) {
        byte[] retVal = null;
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            retVal = objectMapper.writeValueAsString(data).getBytes();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return retVal;
    }
}
