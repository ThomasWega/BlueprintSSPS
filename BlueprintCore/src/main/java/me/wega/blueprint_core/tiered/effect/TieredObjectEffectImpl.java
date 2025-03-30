package me.wega.blueprint_core.tiered.effect;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import me.wega.blueprint_core.tiered.effect.impl.*;
import me.wega.blueprint_core.tiered.effect.impl.loc.LocOffsetEffect;
import me.wega.blueprint_core.tiered.effect.impl.loc.LocSetEffect;
import me.wega.blueprint_core.tiered.effect.impl.loc.LookLocEffect;
import me.wega.blueprint_core.tiered.effect.impl.loc.TeleportEffect;
import me.wega.blueprint_core.tiered.effect.impl.particle.ParticleCircleEffect;
import me.wega.blueprint_core.tiered.effect.impl.particle.ParticleEffect;
import me.wega.blueprint_core.tiered.effect.impl.particle.ParticleSphereEffect;
import org.jetbrains.annotations.NotNull;

@Getter
@RequiredArgsConstructor
public enum TieredObjectEffectImpl {
    UNAPPLY_EFFECTS(UnApplyEffectsEffect.class),
    CANCEL_EVENT(CancelEventEffect.class),
    DAMAGE(DamageEffect.class),
    DIRECTION(DirectionEffect.class),
    IN_RANGE(InRangeEffect.class),
    LAUNCH_PROJECTILE(LaunchProjectileEffect.class),
    LISTENER(ListenerEffect.class),
    LOC_OFFSET(LocOffsetEffect.class),
    LOOK_LOC(LookLocEffect.class),
    MSG(MsgEffect.class),
    PARTICLE_CIRCLE(ParticleCircleEffect.class),
    PARTICLE(ParticleEffect.class),
    PARTICLE_SPHERE(ParticleSphereEffect.class),
    SET_PLACEHOLDER(SetPlaceholderEffect.class),
    SOUND(SoundEffect.class),
    LOC_SET(LocSetEffect.class),
    TELEPORT(TeleportEffect.class);

    private final @NotNull Class<? extends TieredObjectEffect<?>> effectClass;
}
