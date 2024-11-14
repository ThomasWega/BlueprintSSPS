package com.eteirnum.toolkit.placeholder;

import com.eteirnum.toolkit.eval_ex.EvalExParser;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Unmodifiable;

import java.util.Map;

/**
 * Represents a boolean that can be parsed with PlaceholderAPI and additional placeholders.
 *
 * <p>Boolean values are parsed as follows:</p>
 * <ul>
 *   <li>If the string is "true", it will return true.</li>
 *   <li>If the string is a number 1 with any number of decimal places, it will return true.</li>
 * </ul>
 */
@Unmodifiable
public class PlaceholderableBoolean extends PlaceholderableString {

    public PlaceholderableBoolean(@NotNull String string) {
        super(string);
    }

    public boolean getBoolean(@Nullable Object parser, @NotNull Map<@NotNull String, @NotNull String> additionalPlaceholders) {
        String parsed = this.getString(parser, additionalPlaceholders);
        parsed = EvalExParser.parse(parsed).getStringValue();
        return this.isConditionTrue(parsed);
    }

    private boolean isConditionTrue(String trimmed) {
        if (trimmed.length() == 1)
            return trimmed.charAt(0) == '1' || trimmed.charAt(0) == 't' || trimmed.charAt(0) == 'y';
        if (trimmed.equalsIgnoreCase("true") || trimmed.equalsIgnoreCase("yes"))
            return true;

        return this.isNumberEqualToOne(trimmed);
    }

    private boolean isNumberEqualToOne(String trimmed) {
        try {
            float number = Float.parseFloat(trimmed);
            return Float.compare(number, 1) == 0;
        } catch (NumberFormatException e) {
            return false;
        }
    }
}