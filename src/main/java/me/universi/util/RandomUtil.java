package me.universi.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

import me.universi.capacity.entidades.Folder;

public class RandomUtil {
    public static final Random random = new Random();
    private RandomUtil() {}

    public static int randomInt(int bound) {
        return randomInt(0, bound);
    }

    public static int randomInt(int min, int bound) {
        return random.nextInt(bound - min) + min;
    }

    public static String randomString(int length, String availableChars) {
        return randomString(
            length,
            availableChars
                .chars()
                .mapToObj(c -> (char) c)
                .collect(Collectors.toList())
        );
    }

    public static String randomString(int length, List<Character> availableChars) {
        StringBuilder builder = new StringBuilder();

        for (int i = 0; i < length; i++) {
            var nextChar = availableChars.get(randomInt(availableChars.size()));
            builder.append( nextChar );
        }

        return builder.toString();
    }
}
