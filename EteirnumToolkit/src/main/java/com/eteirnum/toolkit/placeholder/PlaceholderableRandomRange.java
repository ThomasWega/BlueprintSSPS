package com.eteirnum.toolkit.placeholder;

import com.eteirnum.toolkit.eval_ex.EvalExParser;
import com.eteirnum.toolkit.random.RandomRange;
import com.eteirnum.toolkit.utils.NumberUtils;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import lombok.Data;
import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Unmodifiable;

import java.math.BigDecimal;
import java.util.Map;

/**
 * Represents a range of random numbers that can be parsed with PlaceholderAPI and additional placeholders.
 *
 * @param <T> Number type
 */
@Data
@Unmodifiable
public class PlaceholderableRandomRange<T extends Number> {
    private final @NotNull String minString;
    private final @NotNull String maxString;
    private final @NotNull Class<T> numberClass;

    public @NotNull T getMin(@Nullable Object parser, @NotNull Map<@NotNull String, @NotNull String> additionalPlaceholders) {
        return this.getNum(minString, parser, additionalPlaceholders);
    }

    public @NotNull T getMax(@Nullable Object parser, @NotNull Map<@NotNull String, @NotNull String> additionalPlaceholders) {
        return this.getNum(maxString, parser, additionalPlaceholders);
    }

    private @NotNull T getNum(@NotNull String string, @Nullable Object parser, @NotNull Map<@NotNull String, @NotNull String> additionalPlaceholders) {
        for (Map.Entry<String, String> entry : additionalPlaceholders.entrySet())
            string = string.replace(entry.getKey(), entry.getValue());
        string = PlaceholderAPI.setPlaceholders(parser instanceof OfflinePlayer player ? player : null, string);
        BigDecimal numberValue = EvalExParser.parse(string).getNumberValue();
        return NumberUtils.getGeneric(numberValue, numberClass);
    }

    public @NotNull T getRandom(@Nullable Object parser, @NotNull Map<@NotNull String, @NotNull String> additionalPlaceholders) {
        return new RandomRange<>(this.getMin(parser, additionalPlaceholders), this.getMax(parser, additionalPlaceholders)).getRandom();
    }

    public static <T extends Number> @NotNull PlaceholderableRandomRange<T> deserializeRandomRange(@NotNull JsonElement element, @NotNull Class<T> clazz) {
        if (element.isJsonArray()) {
            JsonArray array = element.getAsJsonArray();
            String min = array.get(0).getAsString();
            String max = (array.size() > 1) ? array.get(1).getAsString() : min;
            return new PlaceholderableRandomRange<>(min, max, clazz);
        } else {
            String value = element.getAsString();
            return new PlaceholderableRandomRange<>(value, value, clazz);
        }
    }
}
