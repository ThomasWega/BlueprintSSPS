package me.wega.blueprint_core.tiered.impl.spell;

import me.wega.blueprint_core.tiered.TieredObjectInstance;
import lombok.Getter;
import me.wega.blueprint_core.tiered.effect.TieredObjectEffectData;
import me.wega.blueprint_core.tiered.effect.handler.TieredObjectEffectHandler;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.List;

import static me.wega.blueprint_core.tiered.effect.TieredObjectEffectData.TargetType.INVOKER;

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
        TieredObjectEffectData data = new TieredObjectEffectData(this, player);

        if (this.getObject().isOverwrite())
            handleOverwrite(data);

        TieredObjectEffectHandler.handleApply(INVOKER, data, this.getTier().getEffects());
    }

    /**
     * Handles the removal of the effects that are overwritten by this effect.
     *
     * @param data The data of the effect.
     */
    private void handleOverwrite(@NotNull TieredObjectEffectData data) {
        TieredObjectEffectHandler.getScheduled(data.getInvoker()).values().stream()
                .flatMap(List::stream)
                .forEach(effectData -> TieredObjectEffectHandler.unApply(effectData.getData(), true, false, effectData.getEffect()));
    }
}
