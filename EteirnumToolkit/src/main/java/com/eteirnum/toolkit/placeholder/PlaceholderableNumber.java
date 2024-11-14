package com.eteirnum.toolkit.placeholder;

import com.eteirnum.toolkit.eval_ex.EvalExParser;
import com.eteirnum.toolkit.utils.NumberUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Unmodifiable;

import java.util.Map;

/**
 * Represents a number that can be parsed with PlaceholderAPI and additional placeholders.
 *
 * @param <T> Number type
 */
@Unmodifiable
public class PlaceholderableNumber<T extends Number> extends PlaceholderableString {
    private final @NotNull Class<T> numberClass;

    public PlaceholderableNumber(@NotNull String string, @NotNull Class<T> numberClass) {
        super(string);
        this.numberClass = numberClass;
    }

    public @NotNull T getNumber(@Nullable Object parser, @NotNull Map<@NotNull String, @NotNull String> additionalPlaceholders) {
        String parsed = this.getString(parser, additionalPlaceholders);
        return NumberUtils.getGeneric(EvalExParser.parse(parsed).getNumberValue(), this.numberClass);
    }
}
