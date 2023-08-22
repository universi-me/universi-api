package me.universi.util;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.nio.ByteBuffer;
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
}
