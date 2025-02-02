package me.wega.blueprint_core.tiered.effect;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import me.wega.blueprint_core.tiered.TieredObjectInstance;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.Event;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import static me.wega.blueprint_core.tiered.effect.TieredObjectEffectData.TargetType.LOCATION;
import static me.wega.blueprint_core.tiered.effect.TieredObjectEffectData.TargetType.TARGET;

/**
 * Holds data for the {@link TieredObjectEffect} execution.
 */
@AllArgsConstructor
@Setter
@Getter
public class TieredObjectEffectData implements Cloneable {
    private final @NotNull UUID uid = UUID.randomUUID();
    private final @NotNull TieredObjectInstance<?> objectInstance;
    private final @NotNull LivingEntity invoker;
    @Getter(value = AccessLevel.NONE)
    private @Nullable Entity target;
    @Getter(value = AccessLevel.NONE)
    private @Nullable Location location;
    private @Nullable Event event;
    private int depth = 0;
    private @NotNull Map<@NotNull String, @NotNull String> placeholders = new HashMap<>();

    public TieredObjectEffectData(@NotNull TieredObjectInstance<?> objectInstance, @NotNull LivingEntity invoker) {
        this.objectInstance = objectInstance;
        this.invoker = invoker;
    }

    /**
     * Gets all the targets of the effect.
     *
     * @return A map of the target type to the entity of the target.
     * @see #getTarget(TargetType)
     */
    public @NotNull Map<@NotNull TargetType, @NotNull Entity> getTargets(@NotNull TargetType @NotNull [] targets) {
        return Arrays.stream(targets)
                .map(targetType -> {
                    Entity target = (this.target != null) ? this.target : this.invoker;
                    return Map.entry(targetType, targetType == TARGET ? target : this.invoker);
                })
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    /**
     * Gets the entity of the target.
     *
     * @param targetType The target type to get the entity of.
     * @return The entity of the target or null if the target type does not have an entity.
     */
    public @NotNull Entity getTarget(@NotNull TargetType targetType) {
        Entity entity = (targetType == TARGET) ? this.target : this.invoker;
        if (entity == null)
            entity = this.invoker;
        return entity;
    }

    /**
     * Gets either the cached location (if the target type is {@link TargetType#LOCATION}) or the location of the target.
     *
     * @param targetType The target type to get the location of.
     * @return The location of the target.
     */
    public @NotNull Location getLocation(@NotNull TargetType targetType) {
        if (targetType == LOCATION) {
            if (this.location == null)
                this.location = this.getTarget(targetType).getLocation().clone();
            return this.location;
        }
        return this.getTarget(targetType).getLocation().clone();
    }

    /**
     * @return A deep copy of this object.
     */
    @Override
    public TieredObjectEffectData clone() {
        try {
            TieredObjectEffectData clone = (TieredObjectEffectData) super.clone();
            clone.setTarget(target);
            clone.setLocation((location != null) ? location.clone() : null);
            clone.setEvent(event);
            clone.setPlaceholders(new HashMap<>(placeholders));
            return clone;
        } catch (CloneNotSupportedException e) {
            throw new AssertionError();
        }
    }

    /**
     * Creates and parses the default placeholders for the effect.
     */
    public void parseDefaultPlaceholders(@NotNull TargetType targetType) {
        Map<String, String> placeholders = new HashMap<>();

        placeholders.put("%invoker%", this.invoker.getName());
        placeholders.put("%invoker_uuid%", this.invoker.getUniqueId().toString());
        placeholders.put("%target%", (this.target != null) ? this.target.getName() : "null");
        placeholders.put("%target_uuid%", (this.target != null) ? this.target.getUniqueId().toString() : "null");

        // depth
        placeholders.put("%depth%", String.valueOf(this.depth));

        // location
        Location loc = this.getLocation(targetType);
        placeholders.put("%loc_x%", String.valueOf(loc.getX()));
        placeholders.put("%loc_y%", String.valueOf(loc.getY()));
        placeholders.put("%loc_z%", String.valueOf(loc.getZ()));
        placeholders.put("%loc_yaw%", String.valueOf(loc.getYaw()));
        placeholders.put("%loc_pitch%", String.valueOf(loc.getPitch()));
        placeholders.put("%loc_world%", loc.getWorld().getName());
        placeholders.put("%loc_world_uid%", loc.getWorld().getUID().toString());

        this.placeholders.putAll(placeholders);
    }

    /**
     * Indicates to whom the effect should be applied.
     */
    public enum TargetType {
        INVOKER,
        TARGET,
        LOCATION
    }
}
