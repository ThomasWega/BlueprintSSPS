package com.eteirnum.toolkit.random;

import com.eteirnum.toolkit.utils.NumberUtils;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import lombok.Data;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Unmodifiable;

import java.util.Random;

/**
 * Represents a range of random numbers.
 *
 * @param <T> Number type
 */
@Data
@Unmodifiable
public class RandomRange<T extends Number> {
    private static final Random RANDOM = new Random();
    private final @NotNull T min;
    private final @NotNull T max;

    public RandomRange(@NotNull T min, @NotNull T max) {
        this.min = min;
        this.max = max;

        if (min.doubleValue() > max.doubleValue()) {
            throw new IllegalArgumentException("min cannot be greater than max");
        }
    }

    @SuppressWarnings("unchecked")
    public T getRandom() {
        if (min.doubleValue() == max.doubleValue())
            return min;

        Double randomValue = RANDOM.nextDouble() * (max.doubleValue() - min.doubleValue()) + min.doubleValue();
        return NumberUtils.getGeneric(randomValue, (Class<T>) min.getClass());
    }

    /**
     * Deserializes a RandomRange from a JsonObject.
     * Supports both single value and array of 2 values.
     *
     * @param effectData JsonObject to deserialize from
     * @param key        Key to deserialize from
     * @param clazz      Class of the number
     * @param <T>        Number type
     * @return Deserialized RandomRange
     */
    public static <T extends Number> @Nullable RandomRange<T> deserializeRandomRange(@NotNull JsonObject effectData, @NotNull String key, @NotNull Class<T> clazz) {
        if (effectData.has(key)) {
            JsonElement element = effectData.get(key);
            if (element.isJsonArray()) {
                JsonArray array = element.getAsJsonArray();
                Number min = array.get(0).getAsNumber();
                Number max = (array.size() > 1) ? array.get(1).getAsNumber() : min;
                min = NumberUtils.getGeneric(min, clazz);
                max = NumberUtils.getGeneric(max, clazz);
                return new RandomRange<>((T) min, (T) max);
            } else {
                T value = NumberUtils.getGeneric(element.getAsNumber(), clazz);
                return new RandomRange<>(value, value);
            }
        }
        return null;
    }
}