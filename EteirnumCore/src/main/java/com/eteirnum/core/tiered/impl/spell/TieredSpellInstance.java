package com.eteirnum.core.tiered.impl.spell;

import com.eteirnum.core.tiered.TieredObjectInstance;
import lombok.Getter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

/**
 * Represents a tiered spell instance.
 * A tiered spell instance is an instance of a tiered spell, which has specific data for the instance.
 */
@Getter
public class TieredSpellInstance extends TieredObjectInstance<TieredSpell> {

    public TieredSpellInstance(@NotNull TieredSpell item, int tierNum) {
        super(item, tierNum);
    }

    /**
     * Applies or casts the effects of the spell to the player.
     *
     * @param player The player to apply the effects to.
     */
    public void use(@NotNull Player player) {
    }


    private void handleOverwrite() {
    }
}
