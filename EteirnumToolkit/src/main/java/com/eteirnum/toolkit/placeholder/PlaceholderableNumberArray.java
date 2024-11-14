package com.eteirnum.toolkit.placeholder;

import com.eteirnum.toolkit.eval_ex.EvalExParser;
import com.eteirnum.toolkit.utils.NumberUtils;
import lombok.Data;
import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Unmodifiable;

import java.lang.reflect.Array;
import java.math.BigDecimal;
import java.util.Map;

/**
 * Represents an array of numbers that can be parsed with PlaceholderAPI and additional placeholders.
 *
 * @param <T> Number type
 */
@Data
@Unmodifiable
public class PlaceholderableNumberArray<T extends Number> {
    private final @NotNull String[] strings;
    private final @NotNull Class<T> numberClass;

    public @NotNull T @NotNull [] getNumbers(@Nullable Object parser, @NotNull Map<@NotNull String, @NotNull String> additionalPlaceholders) {
        T[] numbers = (T[]) Array.newInstance(numberClass, strings.length);
        for (int i = 0; i < strings.length; i++) {
            String parsed = strings[i];
            for (Map.Entry<String, String> entry : additionalPlaceholders.entrySet())
                parsed = parsed.replace(entry.getKey(), entry.getValue());
            parsed = PlaceholderAPI.setPlaceholders(parser instanceof OfflinePlayer player ? player : null, parsed);
            BigDecimal numberValue = EvalExParser.parse(parsed).getNumberValue();
            numbers[i] = NumberUtils.getGeneric(numberValue, numberClass);
        }
        return numbers;
    }

    public @NotNull T getNumber(int index, @Nullable Object parser, @NotNull Map<@NotNull String, @NotNull String> additionalPlaceholders) {
        String parsed = strings[index];
        for (Map.Entry<String, String> entry : additionalPlaceholders.entrySet())
            parsed = parsed.replace(entry.getKey(), entry.getValue());
        parsed = PlaceholderAPI.setPlaceholders(parser instanceof OfflinePlayer player ? player : null, parsed);
        BigDecimal numberValue = EvalExParser.parse(parsed).getNumberValue();
        return NumberUtils.getGeneric(numberValue, numberClass);
    }
}
