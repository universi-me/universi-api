package me.universi.activity.services;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializerProvider;
import java.io.IOException;
import me.universi.group.entities.Group;

public class ActivitySerializeGroup extends JsonSerializer<Group> {

    @Override
    public void serialize(Group value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
        // Temporarily disable "activity" property
        ObjectMapper mapper = ((ObjectMapper) gen.getCodec())
                .copy()
                .addMixIn(Group.class, GroupIgnoreActivityMixin.class);

        gen.writeRawValue(mapper.writeValueAsString(value));
    }

    @JsonIgnoreProperties("activity")
    private static abstract class GroupIgnoreActivityMixin {}
}
