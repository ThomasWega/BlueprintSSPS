package me.wega.blueprint_core.tiered.effect.type;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import me.wega.blueprint_core.tiered.effect.TieredObjectEffect;
import me.wega.blueprint_core.tiered.effect.TieredObjectEffectData;
import me.wega.blueprint_core.tiered.effect.TieredObjectEffectData.TargetType;
import me.wega.blueprint_core.tiered.effect.TieredObjectEffectImpl;
import me.wega.blueprint_core.tiered.effect.parameter.EffectPlaceholderableNumber;
import me.wega.blueprint_core.tiered.effect.parameter.JsonField;
import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Represents an effect that can be applied to a {@link TieredObjectEffectData} object that can be viewed by other targets.
 * This is because by default most {@link TieredObjectEffect} objects are only visible to the target they are applied to.
 *
 * @param <T> The type of value that this effect applies
 */
public abstract class ViewableTieredObjectEffect<T> extends TieredObjectEffect<T> {
    private final @NotNull ViewerType @Nullable [] viewers;

    public ViewableTieredObjectEffect(@NotNull TieredObjectEffectImpl impl,
                                      @NotNull TargetType @Nullable [] targetTypes,
                                      @Nullable Boolean propagate,
                                      @Nullable EffectPlaceholderableNumber<Integer> propagateDepth,
                                      @NotNull EffectPlaceholderableNumber<Integer> @Nullable [] intervalTicks,
                                      @Nullable EffectPlaceholderableNumber<Integer> repeatCount,
                                      @Nullable Boolean stopOnDeath,
                                      @Nullable Boolean stopOnQuit,
                                      @Nullable Boolean ignoreApply,
                                      @Nullable Boolean ignoreUnApply,

                                      @JsonField("viewers") @NotNull ViewerType @Nullable [] viewers) {
        super(impl, targetTypes, propagate, propagateDepth, intervalTicks, repeatCount, stopOnDeath, stopOnQuit, ignoreApply, ignoreUnApply);
        this.viewers = viewers;
    }

    private static ViewerType[] getViewersFromTargetType(@NotNull TargetType targetType) {
        return ViewerType.fromTargetType(targetType) != null ? new ViewerType[]{ViewerType.fromTargetType(targetType)} : new ViewerType[0];
    }

    /**
     * Gets the viewers of the effect. If no viewers are set, the target will be the only viewer.
     *
     * @param data   The data of the effect.
     * @param target The target of the effect.
     * @return A map of the target type to the player that is viewing the effect.
     */
    public @NotNull Map<@NotNull ViewerType, @NotNull List<@NotNull LivingEntity>> getViewers(@NotNull TieredObjectEffectData data, @NotNull Map.Entry<@NotNull TargetType, @NotNull Entity> target) {
        ViewerType[] finalViewers = (this.viewers == null) ? getViewersFromTargetType(target.getKey()) : this.viewers;
        return Arrays.stream(finalViewers)
                .map(viewerType -> Map.entry(viewerType, viewerType.targetGetter.apply(data)))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    /**
     * Gets the player viewers of the effect. If no viewers are set, the target will be the only viewer.
     *
     * @param data   The data of the effect.
     * @param target The target of the effect.
     * @return A map of the target type to the player that is viewing the effect.
     * @see #getViewers(TieredObjectEffectData, Map.Entry)
     */
    public @NotNull Map<@NotNull ViewerType, @NotNull List<@NotNull Player>> getPlayerViewers(@NotNull TieredObjectEffectData data, @NotNull Map.Entry<@NotNull TargetType, @NotNull Entity> target) {
        return getViewers(data, target).entrySet().stream()
                .map(entry -> Map.entry(entry.getKey(), entry.getValue().stream()
                        .filter(viewer -> viewer instanceof Player)
                        .map(viewer -> (Player) viewer)
                        .collect(Collectors.toList())))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    /**
     * Gets the player viewers of the effect. If no viewers are set, the target will be the only viewer.
     *
     * @param data   The data of the effect.
     * @param target The target of the effect.
     * @return A list of the player that is viewing the effect.
     * @see #getPlayerViewers(TieredObjectEffectData, Map.Entry)
     */
    public @NotNull List<@NotNull Player> getOnlyPlayerViewers(@NotNull TieredObjectEffectData data, @NotNull Map.Entry<@NotNull TargetType, @NotNull Entity> target) {
        return this.getPlayerViewers(data, target).values()
                .stream()
                .flatMap(Collection::stream)
                .toList();
    }

    /**
     * Represents the type of viewer that can view the effect.
     */
    @RequiredArgsConstructor
    @Getter
    public enum ViewerType {
        INVOKER(TargetType.INVOKER, data -> List.of(data.getInvoker())),
        TARGET(TargetType.TARGET, data -> {
            Entity target = data.getTarget(TargetType.TARGET);
            return (target instanceof Player player) ? List.of(player) : List.of();
        }),
        ALL(null, data -> List.copyOf(Bukkit.getOnlinePlayers()));

        private final @Nullable TargetType targetType;
        private final @NotNull Function<@NotNull TieredObjectEffectData, @NotNull List<@NotNull LivingEntity>> targetGetter;

        public static @Nullable ViewerType fromTargetType(@NotNull TargetType targetType) {
            return Arrays.stream(values())
                    .filter(viewerType -> viewerType.targetType == targetType)
                    .findFirst()
                    .orElse(null);
        }
    }
}
