package me.universi.util;

import java.util.Optional;
import java.util.UUID;

public class CastingUtil {
    private CastingUtil() {}

    public static Optional<String> getString(Object obj) {
        if (obj instanceof String) return Optional.of((String) obj);
        if (obj == null) return Optional.empty();

        return Optional.of(obj.toString());
    }

    public static Optional<UUID> getUUID(Object obj) {
        if (obj instanceof UUID) return Optional.of((UUID) obj);
        if (obj == null) return Optional.empty();

        try {
            return Optional.of(UUID.fromString(obj.toString()));
        }
        catch (IllegalArgumentException invalidUuid) {
            return Optional.empty();
        }
    }
}
