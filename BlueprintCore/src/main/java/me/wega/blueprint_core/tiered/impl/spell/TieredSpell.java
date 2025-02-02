package me.wega.blueprint_core.tiered.impl.spell;

import me.wega.blueprint_core.tiered.TieredObject;
import me.wega.blueprint_core.tiered.TieredObjectImpl;
import me.wega.blueprint_core.tiered.tier.TieredObjectTier;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Getter
public class TieredSpell extends TieredObject<TieredSpellInstance> {
    private final boolean overwrite;

    public TieredSpell(@NotNull String id,
                       @NotNull TieredObjectTier @NotNull [] tiers,
                       @Nullable Boolean overwrite) {
        super(TieredObjectImpl.SPELL, id, tiers);
        this.overwrite = overwrite != null && overwrite;
    }

    @Override
    public @Nullable TieredSpellInstance createInstance(int tierNum) {
        if (tierNum < 0 || tierNum >= this.getTiers().length)
            throw new IllegalArgumentException("Invalid tier number. Must be between 0 and " + (this.getTiers().length - 1) + ". Was: " + tierNum);
        return new TieredSpellInstance(this, tierNum);
    }
}
