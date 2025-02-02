package me.wega.blueprint_core.tiered.effect;

import lombok.Getter;
import lombok.Setter;
import me.wega.blueprint_core.BlueprintCore;
import me.wega.blueprint_core.config.DebugStatus;
import me.wega.blueprint_core.tiered.effect.TieredObjectEffectData.TargetType;
import me.wega.blueprint_core.tiered.effect.parameter.EffectPlaceholderableNumber;
import me.wega.blueprint_core.tiered.effect.parameter.JsonField;
import me.wega.blueprint_core.tiered.effect.type.ViewableTieredObjectEffect;
import me.wega.blueprint_toolkit.placeholder.PlaceholderableNumber;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.entity.Entity;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Represents an effect that can be applied to a {@link TieredObjectEffectData} object.
 *
 * @param <T> The type of value that this effect applies
 * @implNote Effects should by default be only visible to the target they are applied to, then can be made viewable by extending {@link ViewableTieredObjectEffect}.
 */
@Getter
@Setter
public abstract class TieredObjectEffect<T> {
    private final @NotNull TieredObjectEffectImpl impl;
    /**
     * The target types that this effect will apply to. If null, the effect will apply to currently set targets in {@link TieredObjectEffectData}.
     */
    private final @NotNull TargetType @NotNull [] targetTypes;
    /**
     * Whether the effect should propagate its changes to other effects in the same data object. Default is false.
     */
    private final boolean propagate;
    /**
     * The depth at which the effect should propagate. Default is infinite (-1).
     */
    private final @NotNull EffectPlaceholderableNumber<Integer> propagateDepth;
    /**
     * The interval at which the effect should be applied
     * Last value of the array indicates the interval before the effect is unapplied (could be instant if only one value is provided).
     * Works in conjunction with {@link #repeatCount}, meaning the repeat count will be used as index for the interval ticks.
     */
    private final @NotNull EffectPlaceholderableNumber<Integer> @NotNull [] intervalTicks;
    /**
     * The number of times the effect should be repeated. If 0, the effect will not be repeated but will be applied once.
     * This works in conjunction with {@link #intervalTicks}.
     */
    private final @NotNull EffectPlaceholderableNumber<Integer> repeatCount;
    /**
     * Whether the effect should stop applying when the target dies
     */
    private final boolean stopOnDeath;
    /**
     * Whether the effect should stop applying when the target quits the server
     */
    private final boolean stopOnQuit;
    /**
     * Whether the effect should ignore the apply method
     */
    private final boolean ignoreApply;
    /**
     * Whether the effect should ignore the unapply method
     */
    private final boolean ignoreUnApply;
    /**
     * The values that have been saved by this effect, keyed by the {@link TieredObjectEffectData} object uuid
     * Is used to unapply the effect when needed
     */
    private @NotNull Map<UUID, Map<Map.Entry<TargetType, Entity>, T>> savedValues = new HashMap<>();

    public TieredObjectEffect(@NotNull TieredObjectEffectImpl impl,
                              @JsonField("target") @NotNull TargetType @Nullable [] targetTypes,
                              @JsonField("propagate") @Nullable Boolean propagate,
                              @JsonField("propagate-depth") @Nullable EffectPlaceholderableNumber<Integer> propagateDepth,
                              @JsonField("ticks") @NotNull EffectPlaceholderableNumber<Integer> @Nullable [] intervalTicks,
                              @JsonField("repeat") @Nullable EffectPlaceholderableNumber<Integer> repeatCount,
                              @JsonField("stop-on-death") @Nullable Boolean stopOnDeath,
                              @JsonField("stop-on-quit") @Nullable Boolean stopOnQuit,
                              @JsonField("ignore-apply") @Nullable Boolean ignoreApply,
                              @JsonField("ignore-unapply") @Nullable Boolean ignoreUnApply) {
        this.impl = impl;
        this.targetTypes = (targetTypes != null) ? targetTypes : new TargetType[]{TargetType.INVOKER};
        this.propagate = (propagate != null) ? propagate : false; // Default to false (every effect has own copy of data, that it can modify without affecting other effects)
        this.propagateDepth = (propagateDepth != null) ? propagateDepth : new EffectPlaceholderableNumber<>(new PlaceholderableNumber<>("-1", Integer.class)); // Default to -1 (infinite)
        this.intervalTicks = (intervalTicks != null) ? intervalTicks : new EffectPlaceholderableNumber[]{new EffectPlaceholderableNumber<>(new PlaceholderableNumber<>("0", Integer.class))}; // Default to 0 (no interval)
        this.repeatCount = (repeatCount != null) ? repeatCount : new EffectPlaceholderableNumber<>(new PlaceholderableNumber<>("0", Integer.class)); // Default to 0 (no repeat)
        this.stopOnQuit = (stopOnQuit != null) ? stopOnQuit : true;
        this.stopOnDeath = (stopOnDeath != null) ? stopOnDeath : true;
        this.ignoreApply = (ignoreApply != null) ? ignoreApply : false;
        this.ignoreUnApply = (ignoreUnApply != null) ? ignoreUnApply : false;
    }

    /**
     * Internal logic for applying the effect
     *
     * @param data The effect data
     * @return The value that was applied, or null
     */
    public abstract @Nullable T applyInternal(@NotNull TieredObjectEffectData data, @NotNull Map.Entry<@NotNull TargetType, @NotNull Entity> target);

    /**
     * Internal logic for unapplying the effect.
     *
     * @param data       The effect data
     * @param target     The target that the effect was applied to
     * @param savedValue The value that was applied
     * @implNote should only be implemented if the effect needs to unapply something.
     */
    public void unApplyInternal(@NotNull TieredObjectEffectData data, @NotNull Map.Entry<@NotNull TargetType, @NotNull Entity> target, @NotNull T savedValue) {
    }

    /**
     * Applies the effect to the given data.
     * If the target types are set, the effect will only apply to those target types.
     * <ul>
     *   <li>If the conditions are set, the effect will only apply if the conditions are met.</li>
     *   <li>If the effect is set to propagate, the effect will apply to the data object itself, otherwise a clone of the data object will be used.</li>
     *   <li>The applied values will be stored in the {@link #savedValues} map.</li>
     * </ul>
     */
    public final void apply(@NotNull TieredObjectEffectData data) {
        Map<Map.Entry<TargetType, Entity>, T> applicationMap = new HashMap<>();
        for (Map.Entry<TargetType, Entity> targetEntry : data.getTargets(this.targetTypes).entrySet()) {
            Entity target = targetEntry.getValue();
            this.debug(data, targetEntry, "Applying to " + target.getName() + " (" + targetEntry.getKey() + ")");

            data.parseDefaultPlaceholders(targetEntry.getKey());

            TieredObjectEffectData finalData = this.getPropagatedData(data, target);
            T val = this.applyInternal(finalData, targetEntry);
            applicationMap.put(targetEntry, val);
        }
        this.savedValues.put(data.getUid(), applicationMap);
    }

    /**
     * Returns the propagated data if the effect is set to propagate.
     * The depth at which the effect should propagate is determined by the {@link #propagateDepth} value.
     *
     * @param data   The effect data
     * @param parser The entity that the data is being applied to
     * @return The propagated data, or a clone of the data
     */
    protected @NotNull TieredObjectEffectData getPropagatedData(@NotNull TieredObjectEffectData data, @NotNull Entity parser) {
        int effectDepth = this.propagateDepth.getNumber(data, parser);
        int depth = data.getDepth();
        boolean depthInfinity = effectDepth == -1;
        boolean depthEnough = effectDepth > depth;

        return (this.propagate && (depthInfinity || depthEnough))
                ? data
                : data.clone();
    }

    /**
     * Unapplies the effect from the given data.
     * The applied values will be removed from the {@link #savedValues} map.
     */
    public final void unApply(@NotNull TieredObjectEffectData data) {
        Map<Map.Entry<TargetType, Entity>, T> applicationMap = this.savedValues.remove(data.getUid());
        if (applicationMap == null) return;
        for (Map.Entry<Map.Entry<TargetType, Entity>, T> entry : applicationMap.entrySet()) {
            this.debug(data, entry.getKey(), "Unapplying from " + entry.getKey().getValue().getName() + " (" + entry.getKey().getKey() + ")");
            this.unApplyInternal(data, entry.getKey(), entry.getValue());
        }
    }

    public void reportIssue(@NotNull TieredObjectEffectData data, @NotNull Map.Entry<@NotNull TargetType, @NotNull Entity> target, @NotNull String message) {
        String msg = "Effect " + this.getImpl() + " reported issue: " + message;
        Component playerMsg = Component.text(msg, NamedTextColor.RED);
        BlueprintCore.instance.getLogger().warning(msg);
        data.getInvoker().sendMessage(playerMsg);
        target.getValue().sendMessage(playerMsg);
    }

    public void reportLivingEntityIssue(@NotNull TieredObjectEffectData data, @NotNull Map.Entry<@NotNull TargetType, @NotNull Entity> target) {
        this.reportIssue(data, target, "Target entity is not a living entity");
    }

    public void debug(@NotNull TieredObjectEffectData data, @NotNull Map.Entry<@NotNull TargetType, @NotNull Entity> target, @NotNull String message) {
        if (!DebugStatus.TIERED_OBJECT_EFFECTS) return;
        String msg = "Effect " + this.getImpl() + " debug: " + message;
        Component playerMsg = Component.text(msg, NamedTextColor.DARK_PURPLE);
        BlueprintCore.instance.getLogger().info(msg);
        target.getValue().sendMessage(playerMsg);
    }
}
