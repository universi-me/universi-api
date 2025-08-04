package me.universi.user.services;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.ser.BeanSerializerModifier;
import com.fasterxml.jackson.databind.ser.std.BeanSerializerBase;
import jakarta.annotation.PostConstruct;
import java.io.IOException;
import org.hibernate.Hibernate;
import org.hibernate.proxy.HibernateProxy;
import org.springframework.context.annotation.Configuration;

@Configuration
public class JacksonConfig {

    private final ObjectMapper objectMapper;

    public JacksonConfig(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @PostConstruct
    public void customize() {
        // Register a mixin to ignore Hibernate lazy initialization properties
        objectMapper.addMixIn(Object.class, HibernateProxyMixin.class);

        // Correctly handle Hibernate proxies in JSON serialization
        SimpleModule module = new SimpleModule();
        module.setSerializerModifier(new SafeHibernateProxySerializerModifier());
        objectMapper.registerModule(module);
    }

    public abstract static class HibernateProxyMixin {
        @JsonIgnore
        public abstract Object getHibernateLazyInitializer();

        @JsonIgnore
        public abstract Object getHandler();
    }

    static class SafeHibernateProxySerializerModifier extends BeanSerializerModifier {

        @Override
        public JsonSerializer<?> modifySerializer(SerializationConfig config,
                                                  BeanDescription beanDesc,
                                                  JsonSerializer<?> serializer) {
            if (HibernateProxy.class.isAssignableFrom(beanDesc.getBeanClass()) && serializer instanceof BeanSerializerBase) {
                return new HibernateProxyUnwrappingSerializer((BeanSerializerBase) serializer, beanDesc);
            }
            return serializer;
        }

        public static class HibernateProxyUnwrappingSerializer extends JsonSerializer<Object> {
            private final BeanSerializerBase defaultSerializer;
            BeanDescription beanDesc;

            protected HibernateProxyUnwrappingSerializer(BeanSerializerBase defaultSerializer, BeanDescription beanDesc) {
                this.defaultSerializer = defaultSerializer;
                this.beanDesc = beanDesc;
            }

            @Override
            public void serialize(Object value, JsonGenerator gen, SerializerProvider provider)
                    throws IOException {
                if (value instanceof HibernateProxy proxy) {
                        // Resolve the lazy-loaded Hibernate object
                        if(!Hibernate.isInitialized(proxy)) {
                            Hibernate.initialize(proxy);
                        }
                        value = Hibernate.unproxy(proxy);

                        JsonSerializer<Object> serializer = provider.findValueSerializer(value.getClass());
                        serializer.serialize(value, gen, provider);
                        return;
                }
                defaultSerializer.serialize(value, gen, provider);
            }

            @Override
            public boolean isUnwrappingSerializer() {
                return defaultSerializer.isUnwrappingSerializer();
            }
        }
    }
}


