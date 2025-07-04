package me.universi.util;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.nio.ByteBuffer;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.UUID;

public class ConvertUtil {
    private static ObjectMapper mapper;
    public static String serializeToJsonString(Object object) {
        try {
            if(mapper == null) {
                mapper = new ObjectMapper();
                mapper.configOverride(UUID.class).setFormat(JsonFormat.Value.forShape(JsonFormat.Shape.STRING));
            }
            // Parse this object to Json String
            return String.valueOf(mapper.writeValueAsString(object));
        } catch (Exception e) {
            return object.toString();
        }
    }
    public static UUID convertBytesToUUID(byte[] bytes) {
        ByteBuffer byteBuffer = ByteBuffer.wrap(bytes);
        return new UUID(byteBuffer.getLong(), byteBuffer.getLong());
    }

    public static String bytesToHex(byte[] hash) {
        StringBuilder hexString = new StringBuilder(2 * hash.length);
        for (int i = 0; i < hash.length; i++) {
            String hex = Integer.toHexString(0xff & hash[i]);
            if(hex.length() == 1) {
                hexString.append('0');
            }
            hexString.append(hex);
        }
        return hexString.toString();
    }

    public static LocalDateTime getDateTimeNow() {
        return LocalDateTime.now();
    }
}
