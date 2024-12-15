package com.eteirnum.core.tiered.effect;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;

@Getter
@RequiredArgsConstructor
public enum TieredObjectEffectImpl {
    ;

    private final @NotNull Class<? extends TieredObjectEffect<?>> effectClass;
}
