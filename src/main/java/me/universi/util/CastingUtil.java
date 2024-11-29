package me.universi.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

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

    public static Optional<Boolean> getBoolean(Object obj) {
        if (obj instanceof Boolean)
            return Optional.of((Boolean) obj);

        if (obj instanceof String) {
            String objString = (String) obj;
            if (objString.equalsIgnoreCase("true"))
                return Optional.of(true);

            else if (objString.equalsIgnoreCase("false"))
                return Optional.of(false);
        }

        return Optional.empty();
    }

    public static Optional<Integer> getInteger(Object obj) {
        if (obj instanceof Integer)
            return Optional.of((Integer) obj);

        else if (obj == null)
            return Optional.empty();

        try {
            return Optional.of(Integer.parseInt(getString(obj).get()));
        } catch (NumberFormatException e) {
            return Optional.empty();
        }
    }

    public static <T extends Enum<T>> Optional<T> getEnum(Class<T> enumeration, Object obj) {
        if (enumeration == null || obj == null)
            return Optional.empty();

        String value = getString(obj).get();

        try {
            return Optional.ofNullable(Enum.valueOf(enumeration, value));
        } catch (IllegalArgumentException e) {
            return Optional.empty();
        }
    }

    public static Optional<ArrayList<Object>> getList(Object obj) {
        if (obj == null) return Optional.empty();

        if (obj instanceof Collection) {
            return Optional.of( new ArrayList<>( (Collection<?>) obj ) );
        }

        return Optional.empty();
    }

    public static Optional<HashMap<Object, Object>> getMap(Object obj) {
        if (obj == null) return Optional.empty();

        if (obj instanceof Map) {
            return Optional.of( new HashMap<>((Map<?, ?>) obj) );
        }

        if (obj instanceof List) {
            var list = (List<?>) obj;
            var map = new HashMap<>();

            for (int i = 0; i < list.size(); i++) {
                map.put(i, list.get(i));
            }

            return Optional.of(map);
        }

        return Optional.empty();
    }
}
