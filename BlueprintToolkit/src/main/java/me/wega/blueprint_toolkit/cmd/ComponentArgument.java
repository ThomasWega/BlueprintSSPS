package me.wega.blueprint_toolkit.cmd;

import me.wega.blueprint_toolkit.utils.ColorUtils;
import dev.jorel.commandapi.arguments.ArgumentSuggestions;
import dev.jorel.commandapi.arguments.CustomArgument;
import dev.jorel.commandapi.arguments.TextArgument;
import lombok.Getter;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;

@Getter
public class ComponentArgument extends CustomArgument<Component, String> {

    public ComponentArgument(@NotNull String nodeName) {
        super(new TextArgument(nodeName), info -> ColorUtils.color(info.currentInput()));
        this.replaceSuggestions(ArgumentSuggestions.empty());
    }
}
