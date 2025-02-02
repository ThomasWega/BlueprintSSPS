package me.wega.blueprint_toolkit.task;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

/**
 * Represents a task that can be run and stopped for a player.
 */
@RequiredArgsConstructor
@Getter
public abstract class PlayerTask extends Task {
    private final @NotNull Player player;
}
