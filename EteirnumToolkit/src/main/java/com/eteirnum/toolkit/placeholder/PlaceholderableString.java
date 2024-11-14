package com.eteirnum.toolkit.placeholder;

import lombok.Data;
import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Unmodifiable;

import java.util.Map;

/**
 * Represents a string that can be parsed with PlaceholderAPI and additional placeholders.
 */
@Data
@Unmodifiable
public class PlaceholderableString {
    private final @NotNull String string;

    public @NotNull String getString(@Nullable Object parser, @NotNull Map<@NotNull String, @NotNull String> additionalPlaceholders) {
        String parsed = string;
        for (Map.Entry<String, String> entry : additionalPlaceholders.entrySet())
            parsed = parsed.replace(entry.getKey(), entry.getValue());
        parsed = PlaceholderAPI.setPlaceholders(parser instanceof OfflinePlayer player ? player : null, parsed);

        return parsed;
    }
}
