package com.eteirnum.toolkit.placeholder;

import lombok.Data;
import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Unmodifiable;

import java.util.Map;
import java.util.UUID;

/**
 * Represents an uuid that can be parsed with PlaceholderAPI and additional placeholders.
 */
@Data
@Unmodifiable
public class PlaceholderableUUID {
    private final @NotNull String string;

    public @NotNull UUID getUUID(@Nullable Object parser, @NotNull Map<@NotNull String, @NotNull String> additionalPlaceholders) {
        String parsed = string;
        for (Map.Entry<String, String> entry : additionalPlaceholders.entrySet())
            parsed = parsed.replace(entry.getKey(), entry.getValue());

        if (parser instanceof OfflinePlayer player)
            parsed = PlaceholderAPI.setPlaceholders(player, parsed);

        return UUID.fromString(parsed);
    }
}
