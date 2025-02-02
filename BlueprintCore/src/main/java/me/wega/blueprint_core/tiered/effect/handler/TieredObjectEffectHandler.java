package me.wega.blueprint_core.tiered.effect.handler;

import lombok.experimental.UtilityClass;
import me.wega.blueprint_core.tiered.TieredObject;
import me.wega.blueprint_core.tiered.effect.TieredObjectEffect;
import me.wega.blueprint_core.tiered.effect.TieredObjectEffectData;
import me.wega.blueprint_core.tiered.effect.TieredObjectEffectData.TargetType;
import me.wega.blueprint_core.tiered.effect.parameter.EffectPlaceholderableNumber;
import me.wega.blueprint_toolkit.utils.SchedulerUtils;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * Handles the application and removal of tiered object effects.
 */
@UtilityClass
public class TieredObjectEffectHandler {
    private static final ConcurrentHashMap<Integer, ConcurrentLinkedQueue<ScheduledEffectData>> SCHEDULED_EFFECTS = new ConcurrentHashMap<>();
    private static int CURRENT_TICK = 0;

    static {
        runScheduledEffectsTask();
    }

    /**
     * The only job of this task is to apply and unApply effects at the correct time.
     * <ul>
     *   <li>If the effect is set to ignoreApply or ignoreUnApply, it will not be applied or unApplied.</li>
     *   <li>If the effect still has remaining applies, it won't be unApplied until the last apply.</li>
     * </ul>
     * The values in the map are removed after processing.
     */
    // TODO PRIORITY: HIGH
    //  do async. Will need the effects to be thread safe, but this will be a huge performance boost
    private static void runScheduledEffectsTask() {
        SchedulerUtils.runTaskTimer(() -> {
            List<Map.Entry<Integer, ConcurrentLinkedQueue<ScheduledEffectData>>> toRemove = new ArrayList<>();

            for (Map.Entry<Integer, ConcurrentLinkedQueue<ScheduledEffectData>> entry : SCHEDULED_EFFECTS.entrySet()) {
                if (entry.getKey() <= CURRENT_TICK) {
                    for (ScheduledEffectData effectData : entry.getValue()) {
                        TieredObjectEffectData data = effectData.getData();
                        TieredObjectEffect<?> effect = effectData.getEffect();

                        if (effectData.isFirstApply() || effectData.getRemainingApplies() > 0)
                            // don't check for ignoreApply. It's intentionally checked only in the handleApply method on first apply
                            effect.apply(data);
                        else
                            // don't check for ignoreUnApply. It's intentionally checked only in the handleApply method on last unApply
                            effect.unApply(data);
                    }
                    toRemove.add(entry);
                }
            }

            toRemove.forEach(entry -> SCHEDULED_EFFECTS.remove(entry.getKey(), entry.getValue()));
            CURRENT_TICK++;
        }, 0, 1);
    }

    /**
     * Apply all the effects if they are not {@link TieredObjectEffect#isIgnoreApply()}.
     *
     * @param data    The data to apply the effects to
     * @param effects The effects to apply
     */
    public static void apply(@NotNull TieredObjectEffectData data, @NotNull TieredObjectEffect<?>... effects) {
        Arrays.stream(effects).forEach(effect -> {
            if (!effect.isIgnoreApply())
                effect.apply(data);
        });
    }

    /**
     * Unapply all the effects if they are not {@link TieredObjectEffect#isIgnoreUnApply()}.
     *
     * @param data                    The data to unApply the effects from
     * @param removeScheduledOnObject Whether to remove the currently scheduled effects on same object
     *                                As specified in {@link #removeScheduledEffects(TieredObjectEffectData, TieredObjectEffect[])}
     * @param checkForIgnoreUnApply   Whether to check for {@link TieredObjectEffect#isIgnoreUnApply()} before unApplying
     * @param effects                 The effects to unApply
     */
    public static void unApply(@NotNull TieredObjectEffectData data, boolean removeScheduledOnObject, boolean checkForIgnoreUnApply, @NotNull TieredObjectEffect<?>... effects) {
        Arrays.stream(effects)
                .filter(effect -> !checkForIgnoreUnApply || !effect.isIgnoreUnApply())
                .forEach(effect -> effect.unApply(data));

        // remove the scheduled effect for the same object and invoker
        if (removeScheduledOnObject)
            removeScheduledEffects(data, effects);
    }

    /**
     * Handle the application of the effects. This handles all the intervals and repeats.
     * <ul>
     *   <li>All the values are parsed first.</li>
     *   <li>All repeats and intervals are put into a queue to be applied at the correct time straight away.</li>
     *   <li>If the effect is set to {@link TieredObjectEffect#isPropagate()}, the data will be cloned for each effect.</li>
     *   <li>If the effect has only one interval of 0 and 0 repeats, it will be applied and unApplied immediately.</li>
     * </ul>
     *
     * @param targetType The target type of the effect
     * @param data       The data to apply the effects to
     * @param effects    The effects to apply
     */
    public static void handleApply(@NotNull TargetType targetType, @NotNull TieredObjectEffectData data, @NotNull TieredObjectEffect<?>... effects) {
        Entity target = data.getTarget(targetType);
        for (TieredObjectEffect<?> effect : effects) {
            EffectPlaceholderableNumber<Integer>[] intervals = effect.getIntervalTicks();
            int[] intervalsArray = Arrays.stream(intervals)
                    .map(pNum -> pNum.getNumber(data, target))
                    .mapToInt(Integer::intValue)
                    .toArray();
            int repeats = effect.getRepeatCount().getNumber(data, target);

            // because of this the order of the effects is not guaranteed (if one effect has ticks [0, 20] and one doesn't, the one without will be applied first)
            // apply and unApply immediately
//            if (intervalsArray.length == 1 && intervalsArray[0] == 0 && repeats == 0) {
//                ScheduledEffectData scheduledEffectData = new ScheduledEffectData(CURRENT_TICK, data, effect, 0, true);
//                scheduledEffectData.setPlaceholders();
//                Bukkit.getLogger().severe("APPLY IMMEDIATELY " + effect.getImpl().name());
//                if (!effect.isIgnoreApply())
//                    effect.apply(data);
//                if (!effect.isIgnoreUnApply())
//                    effect.unApply(data);
//                continue;
//            }

            int currentTick = CURRENT_TICK;

            int loops = Math.max(repeats + 2, intervalsArray.length); // repeats + 1 for the first apply, + 1 for the last unApply

            for (int i = 0; i < loops; i++) {
                int intervalIndex = Math.min(i, intervalsArray.length - 1);
                currentTick += intervalsArray[intervalIndex];
                boolean firstApply = i == 0;
                int remainingApplies = loops - i - 1;

                if (firstApply && effect.isIgnoreApply())
                    continue;

                if (remainingApplies == 0 && effect.isIgnoreUnApply())
                    continue;

                ScheduledEffectData scheduledEffectData = new ScheduledEffectData(currentTick, data, effect, loops - i - 1, i == 0);
                scheduledEffectData.setPlaceholders();

                SCHEDULED_EFFECTS.computeIfAbsent(currentTick, k -> new ConcurrentLinkedQueue<>())
                        .add(scheduledEffectData);
            }
        }
    }

    /**
     * Get all the scheduled effects for the entity.
     *
     * @param entity The entity to get the scheduled effects for
     * @return A map of the scheduled tick and the list of scheduled effects
     */
    public static @NotNull Map<@NotNull Integer, @NotNull List<ScheduledEffectData>> getScheduled(@NotNull Entity entity) {
        return SCHEDULED_EFFECTS.values().stream()
                .flatMap(Collection::stream)
                .filter(scheduled -> scheduled.getData().getInvoker().equals(entity))
                .collect(Collectors.groupingBy(ScheduledEffectData::getScheduledTick));
    }

    /**
     * Remove all the scheduled effects for the entity.
     *
     * @param entity The entity to remove the scheduled effects for
     */
    public static void removeAllScheduledEffects(@NotNull LivingEntity entity) {
        removeScheduledEffectsInternal(scheduledEffect -> scheduledEffect.getData().getInvoker().equals(entity));
    }

    /**
     * Removes the currently scheduled effects for the same {@link TieredObject} and {@link TieredObjectEffectData#getInvoker()}.
     *
     * @param data    The data to remove the scheduled effects for
     * @param effects The effects to remove the scheduled effects for
     */
    public static void removeScheduledEffects(@NotNull TieredObjectEffectData data, @NotNull TieredObjectEffect<?>... effects) {
        LivingEntity invoker = data.getInvoker();
        TieredObject<?> object = data.getObjectInstance().getObject();
        List<TieredObjectEffect<?>> effectsList = Arrays.asList(effects);
        removeScheduledEffectsInternal(scheduledEffect ->
                scheduledEffect.getData().getInvoker().equals(invoker)
                        && effectsList.contains(scheduledEffect.getEffect())
                        && scheduledEffect.getData().getObjectInstance().getObject().equals(object)
        );
    }

    private static void removeScheduledEffectsInternal(Predicate<ScheduledEffectData> removalCondition) {
        SCHEDULED_EFFECTS.forEach((tick, effectQueue) -> {
            boolean removed = effectQueue.removeIf(removalCondition);
            if (removed && effectQueue.isEmpty()) {
                SCHEDULED_EFFECTS.remove(tick, effectQueue);
            }
        });
    }
}