package me.wega.blueprint_toolkit.utils;

import com.google.gson.JsonElement;
import lombok.experimental.UtilityClass;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.JoinConfiguration;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.serializer.gson.GsonComponentSerializer;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

/**
 * Handles the conversion from Components to other Types
 */
@UtilityClass
public class ComponentUtils {

    /**
     * Replace the text in the list of Components
     *
     * @param components List of Components to replace text in
     * @param match      Text to match
     * @param replace    Text to replace with
     * @return List of Components with replaced text
     */
    public static @NotNull List<Component> replace(@NotNull Collection<@NotNull Component> components, @NotNull String match, @NotNull Component replace) {
        return components.stream()
                .map(component -> replace(component, match, replace))
                .toList();
    }

    /**
     * Replace the text in the Component
     *
     * @param target  Component to replace text in
     * @param match   Text to match
     * @param replace Text to replace with
     * @return Component with replaced text
     * @see #replace(Collection, String, Component) 
     */
    public static @NotNull Component replace(@NotNull Component target, @NotNull String match, @NotNull Component replace) {
        return target.replaceText(builder ->
            builder.matchLiteral(match)
                    .replacement(replace)
        );
    }

    /**
     * Converts the Component to String.
     * Will preserve only color codes
     *
     * @param component Component to convert
     * @return String from Component with only unformatted color codes
     */
    public static @NotNull String toLegacy(@NotNull Component component) {
        return LegacyComponentSerializer.legacyAmpersand().serialize(component);
    }

    /**
     * Converts the Component to String.
     * Will apply colors by replacing ampersand with section char
     *
     * @param component Component to convert
     * @return String from Component with only formatted color codes
     */
    public static @NotNull String toColoredLegacy(@NotNull Component component) {
        return LegacyComponentSerializer.legacySection().serialize(component);
    }

    /**
     * Convert component to JSON
     * Will preserve everything (colors, events, ...)
     *
     * @param component Component to convert
     * @return JSONElement from Component
     */
    public static @NotNull JsonElement toJson(@NotNull Component component) {
        return GsonComponentSerializer.gson().serializeToTree(component);
    }

    /**
     * Convert component to String of JSON
     * Will preserve everything (colors, events, ...)
     *
     * @param component Component to convert
     * @return String of JSON from Component
     */
    public static @NotNull String toJsonString(@NotNull Component component) {
        return GsonComponentSerializer.gson().serialize(component);
    }

    /**
     * Convert list of components to JSON string
     * @param components List of components to convert
     * @return JSON string from list of components
     */
    public static @NotNull String toJsonString(@NotNull List<Component> components) {
        return GsonComponentSerializer.gson().serialize(Component.join(JoinConfiguration.newlines(), components));
    }

    /**
     * Convert JSON string to Component
     * @param jsonString JSON string to convert
     * @return Component from JSON string
     */
    public static @NotNull Component fromJsonString(@NotNull String jsonString) {
        return GsonComponentSerializer.gson().deserialize(jsonString);
    }

    /**
     * Convert Component to MiniMessage string
     * @param component Component to convert
     * @return MiniMessage string from Component
     */
    public static @NotNull String toMiniMessage(@NotNull Component component) {
        return MiniMessage.miniMessage().serialize(component);
    }

    /**
     * Split the component by newlines
     * @param component Component to split
     * @return List of components split by newlines
     */
    public static @NotNull List<Component> splitNewLines(@NotNull Component component) {
        return Arrays.stream(toMiniMessage(component).split("<newline>"))
                .map(ColorUtils::color)
                .toList();
    }

    /**
     * This feature is present in adventure, but not in the version used by the server
     * @param component Component to add decoration to
     * @param decoration Decoration to add
     * @param state State of the decoration
     * @return Component with added decoration
     * @apiNote Used for compatibility with older versions
     */
    public static @NotNull Component decorationIfAbsent(@NotNull Component component, @NotNull TextDecoration decoration, @NotNull TextDecoration.State state) {
        if (component.decoration(decoration) == TextDecoration.State.NOT_SET)
            return component.decoration(decoration, state);
        return component;
    }
}