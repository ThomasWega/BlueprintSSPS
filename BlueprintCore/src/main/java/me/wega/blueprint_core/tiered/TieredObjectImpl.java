package me.wega.blueprint_core.tiered;

import me.wega.blueprint_core.tiered.impl.spell.TieredSpellInstance;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;

@RequiredArgsConstructor
@Getter
public enum TieredObjectImpl {
    SPELL(TieredSpellInstance.class);

    private final @NotNull String folderName = name().toLowerCase();
    private final @NotNull Class<? extends TieredObjectInstance<?>> instanceClass;
}
