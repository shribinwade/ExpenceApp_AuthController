package org.example.serializer;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.common.serialization.Serializer;
import org.example.eventProducer.UserInfoEvent;
import org.example.models.UserInfoDTO;

import java.nio.charset.StandardCharsets;

public class UserInfoSerializer implements Serializer<UserInfoEvent> {

    @Override
    public byte[] serialize(String topic, UserInfoEvent data) {
        if (data == null) {
            return null;
        }
        byte[] retVal = null;
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            retVal = objectMapper.writeValueAsString(data).getBytes(StandardCharsets.UTF_8);
        } catch (Exception ex) {
            throw new RuntimeException("Error serializing UserInfoEvent", ex);
        }
        return retVal;
    }
}
