package com.eteirnum.toolkit.utils;

import lombok.experimental.UtilityClass;
import org.jetbrains.annotations.NotNull;

import java.util.*;

@UtilityClass
public class MapUtils {

    public static <K, V> @NotNull Map<K, V> shuffleMap(@NotNull Map<K, V> map) {
        List<Map.Entry<K, V>> entries = new ArrayList<>(map.entrySet());

        Collections.shuffle(entries);

        Map<K, V> shuffledMap = new LinkedHashMap<>();
        for (Map.Entry<K, V> entry : entries)
            shuffledMap.put(entry.getKey(), entry.getValue());

        return shuffledMap;
    }
}
