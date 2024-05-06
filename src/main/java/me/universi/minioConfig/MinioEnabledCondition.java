package me.universi.minioConfig;

import org.jetbrains.annotations.NotNull;
import org.springframework.context.annotation.Condition;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.core.type.AnnotatedTypeMetadata;

public class MinioEnabledCondition implements Condition {
    @Override
    public boolean matches(ConditionContext context, @NotNull AnnotatedTypeMetadata metadata) {
        String disableProperty = context.getEnvironment().getProperty("minio.enabled");
        return Boolean.parseBoolean(disableProperty);
    }
}
