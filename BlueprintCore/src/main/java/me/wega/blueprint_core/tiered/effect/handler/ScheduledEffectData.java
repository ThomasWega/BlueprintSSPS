package me.wega.blueprint_core.tiered.effect.handler;

import lombok.Getter;
import lombok.Setter;
import me.wega.blueprint_core.tiered.effect.TieredObjectEffect;
import me.wega.blueprint_core.tiered.effect.TieredObjectEffectData;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

/**
 * Represents a scheduled effect that will be applied to a player at a certain tick.
 */
@Getter
@Setter
public class ScheduledEffectData {
    private final int scheduledTick;
    @NotNull
    private final TieredObjectEffectData data;
    @NotNull
    private final TieredObjectEffect<?> effect;
    private final int remainingApplies;
    private final boolean firstApply;

    public ScheduledEffectData(int scheduledTick, @NotNull TieredObjectEffectData data, @NotNull TieredObjectEffect<?> effect, int remainingApplies, boolean firstApply) {
        this.scheduledTick = scheduledTick;
        this.data = data;
        this.effect = effect;
        this.remainingApplies = remainingApplies;
        this.firstApply = firstApply;
    }

    /**
     * Sets all the placeholders and their values of this iteration
     */
    public void setPlaceholders() {
        data.getPlaceholders().putAll(Map.of(
                "%remaining_applies%", String.valueOf(remainingApplies),
                "%first_apply%", String.valueOf(firstApply)
        ));
    }
}
