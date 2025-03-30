package me.wega.blueprint_core.tiered.effect.impl;

import me.wega.blueprint_core.tiered.effect.TieredObjectEffect;
import me.wega.blueprint_core.tiered.effect.TieredObjectEffectData;
import me.wega.blueprint_core.tiered.effect.TieredObjectEffectData.TargetType;
import me.wega.blueprint_core.tiered.effect.TieredObjectEffectImpl;
import me.wega.blueprint_core.tiered.effect.parameter.EffectPlaceholderableNumber;
import me.wega.blueprint_core.tiered.effect.parameter.EffectPlaceholderableRandomRange;
import me.wega.blueprint_core.tiered.effect.parameter.EffectPlaceholderableString;
import me.wega.blueprint_core.tiered.effect.parameter.JsonField;
import com.jeff_media.morepersistentdatatypes.DataType;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import me.wega.blueprint_toolkit.pdc.key.IPDCKey;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.projectiles.ProjectileSource;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

import static me.wega.blueprint_core.BlueprintCore.instance;


/**
 * Represents an effect that will launch a projectile from the target location.
 */
@Getter
public class LaunchProjectileEffect extends TieredObjectEffect<Projectile> {
    private final @NotNull EffectPlaceholderableString projectileType;
    private final @Nullable EffectPlaceholderableRandomRange<Float> velocity;

    static {
        Bukkit.getPluginManager().registerEvents(new Listeners(), instance);
    }

    public LaunchProjectileEffect(@NotNull TargetType @Nullable [] targetTypes,
                                  @Nullable Boolean propagate,
                                  @Nullable EffectPlaceholderableNumber<Integer> propagateDepth,
                                  @NotNull EffectPlaceholderableNumber<Integer> @Nullable [] intervalTicks,
                                  @Nullable EffectPlaceholderableNumber<Integer> repeatCount,
                                  @Nullable Boolean stopOnDeath,
                                  @Nullable Boolean stopOnQuit,
                                  @Nullable Boolean ignoreApply,
                                  @Nullable Boolean ignoreUnApply,
                                  @JsonField("projectile") @NotNull EffectPlaceholderableString projectileType,
                                  @JsonField("velocity") @Nullable EffectPlaceholderableRandomRange<Float> velocity) {
        super(TieredObjectEffectImpl.LAUNCH_PROJECTILE, targetTypes, propagate, propagateDepth, intervalTicks, repeatCount, stopOnDeath, stopOnQuit, ignoreApply, ignoreUnApply);
        this.projectileType = projectileType;
        this.velocity = velocity;
    }

    @Override
    public @Nullable Projectile applyInternal(@NotNull TieredObjectEffectData data, @NotNull Map.Entry<@NotNull TargetType, @NotNull Entity> target) {
        Entity targetEntity = target.getValue();
        if (!(targetEntity instanceof ProjectileSource source)) return null;
        Location targetLocation = data.getLocation(target.getKey());
        ProjectileType projectileType = ProjectileType.valueOf(this.projectileType.getString(data, source));

        float velocity = (this.velocity != null) ? this.velocity.getRandom(data, source) : 1.0f;
        Vector dir = targetLocation.getDirection().normalize().multiply(velocity);

        Projectile projectile = source.launchProjectile(projectileType.projectileClass, dir);
        projectile.setShooter(source);
        projectile.setInvulnerable(false);
        projectile.setSilent(true);

        if (projectileType == ProjectileType.FIREBALL)
            ((Fireball) projectile).setIsIncendiary(false);

        PROJECTILE_KEY.set(projectile, true);

        data.setTarget(projectile);

        return projectile;
    }

    @Override
    public void unApplyInternal(@NotNull TieredObjectEffectData data, Map.@NotNull Entry<@NotNull TargetType, @NotNull Entity> target, @NotNull Projectile savedValue) {
        PROJECTILE_KEY.remove(savedValue);
        savedValue.remove();
    }

    @RequiredArgsConstructor
    public enum ProjectileType {
        ARROW(Arrow.class),
        FIREBALL(Fireball.class);

        private final @NotNull Class<? extends Projectile> projectileClass;
    }

    public static final IPDCKey PROJECTILE_KEY = new IPDCKey() {
        @Override
        public @NotNull JavaPlugin getInstance() {
            return instance;
        }

        @Override
        public @NotNull String getKey() {
            return "projectile_effect";
        }

        @Override
        public @NotNull PersistentDataType<?, ?> getType() {
            return DataType.BOOLEAN;
        }
    };

    private static class Listeners implements Listener {
        @EventHandler(ignoreCancelled = true, priority = EventPriority.LOW)
        private void onEntityExplode(EntityExplodeEvent event) {
            if (!PROJECTILE_KEY.has(event.getEntity())) return;
            event.setCancelled(true);
        }

        @EventHandler(ignoreCancelled = true, priority = EventPriority.LOW)
        private void onFireballRedirect(EntityDamageByEntityEvent event) {
            if (!(event.getEntity() instanceof Fireball fireball && event.getDamager() instanceof Player)) return;
            if (!PROJECTILE_KEY.has(fireball)) return;
            event.setCancelled(true);
        }

        @EventHandler(ignoreCancelled = true, priority = EventPriority.NORMAL)
        private void entityHitEvent(EntityDamageByEntityEvent event) {
            if (!PROJECTILE_KEY.has(event.getDamager())) return;
            event.setDamage(0);
        }
    }
}
