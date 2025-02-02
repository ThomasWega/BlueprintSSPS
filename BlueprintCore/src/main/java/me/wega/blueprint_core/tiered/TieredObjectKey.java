package me.wega.blueprint_core.tiered;

import me.wega.blueprint_core.BlueprintCore;
import me.wega.blueprint_toolkit.pdc.data.CustomDataType;
import me.wega.blueprint_toolkit.pdc.key.IPDCKey;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

/**
 * Represents a PDC key for a tiered object.
 * This data is stored in every tiered object item stack.
 * Some implementations may store additional data.
 */
@Getter
@RequiredArgsConstructor
public enum TieredObjectKey implements IPDCKey {
    IMPL("tiered_object.impl", CustomDataType.enumDataType(TieredObjectImpl.class)),
    ID("tiered_object.id", PersistentDataType.STRING),
    TIER("tiered_object.tier", PersistentDataType.INTEGER);

    private final @NotNull String key;
    private final @NotNull PersistentDataType<?, ?> type;

    @Override
    public @NotNull JavaPlugin getInstance() {
        return BlueprintCore.instance;
    }
}
