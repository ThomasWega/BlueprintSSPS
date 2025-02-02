package me.wega.blueprint_toolkit.utils;

import lombok.experimental.UtilityClass;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.Style;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.List;

/**
 * Handles the conversion of colors
 */
@UtilityClass
public final class ColorUtils {

    /**
     * Translates colors using MiniMessage and legacy colors
     *
     * @param string String text to translate colors on
     * @param resolvers Tag resolvers to use
     * @return Component text with translated colors
     */
    public static @NotNull Component color(@NotNull String string, @NotNull TagResolver... resolvers) {
        return MiniMessage.miniMessage().deserialize(string, resolvers);
    }

    /**
     * Translates colors using MiniMessage and legacy colors
     * @param strings Collection of strings to translate colors on
     * @param resolvers Tag resolvers to use
     * @return List of Components with translated colors
     */
    public static @NotNull List<Component> color(@NotNull Collection<String> strings, @NotNull TagResolver... resolvers) {
        return strings.stream()
                .map(s -> color(s, resolvers))
                .toList();
    }

    /**
     * Removes the color from the given Component and returns String
     * with unformatted colors
     *
     * @param text Component to remove color from
     * @return String with unformatted colors
     */
    public static @NotNull String stripColor(@NotNull Component text) {
        return PlainTextComponentSerializer.plainText().serialize(
                LegacyComponentSerializer.legacyAmpersand().deserialize(
                        PlainTextComponentSerializer.plainText().serialize(text)));
    }

    /**
     * Removes the color from the given String and returns String
     * with unformatted colors
     *
     * @param text String to remove color from
     * @return String with no colors
     */
    public static @NotNull String stripColor(@NotNull String text) {
        return PlainTextComponentSerializer.plainText().serialize(color(text));
    }

    /**
     * Converts the Component to String.
     * Will apply colors by replacing ampersand or minimessage with ampersand char
     *
     * @param text Component to convert
     * @return String from Component with only unformatted color codes
     */
    public static @NotNull String toLegacy(@NotNull String text) {
        return LegacyComponentSerializer.legacyAmpersand().serialize(color(text));
    }

    /**
     * Converts the Component to String.
     * Will apply colors by replacing ampersand or minimessage with section char
     *
     * @param text Component to convert
     * @return String from Component with only formatted color codes
     */
    public static @NotNull String toColoredLegacy(@NotNull String text) {
        return LegacyComponentSerializer.legacySection().serialize(color(text));
    }

    /**
     * Retrieves the serialized MiniMessage representation of a given Style.
     *
     * @param style the Style to serialize
     * @return the serialized MiniMessage representation of the TextColor or Style
     */
    public static @NotNull String getMiniMessageColor(@NotNull Style style) {
        return MiniMessage.miniMessage().serialize(Component.empty().style(style));
    }

    /**
     * Retrieves the serialized MiniMessage representation of a given TextColor
     *
     * @param color the Color to serialize
     * @return the serialized MiniMessage representation of the TextColor
     */
    public static @NotNull String getMiniMessageColor(@NotNull TextColor color) {
        return MiniMessage.miniMessage().serialize(Component.empty().color(color));
    }
}
