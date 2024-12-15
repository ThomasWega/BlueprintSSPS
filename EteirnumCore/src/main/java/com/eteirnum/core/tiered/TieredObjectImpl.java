package com.eteirnum.core.tiered;

import com.eteirnum.core.tiered.impl.spell.TieredSpellInstance;
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
