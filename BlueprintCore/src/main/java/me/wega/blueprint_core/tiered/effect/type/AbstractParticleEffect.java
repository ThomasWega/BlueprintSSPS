package me.wega.blueprint_core.tiered.effect.type;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import me.wega.blueprint_core.particle.ParticleResolver;
import me.wega.blueprint_core.tiered.effect.TieredObjectEffectData;
import me.wega.blueprint_core.tiered.effect.TieredObjectEffectData.TargetType;
import me.wega.blueprint_core.tiered.effect.TieredObjectEffectImpl;
import me.wega.blueprint_core.tiered.effect.parameter.EffectPlaceholderableNumber;
import me.wega.blueprint_core.tiered.effect.parameter.EffectPlaceholderableParticleType;
import me.wega.blueprint_core.tiered.effect.parameter.EffectPlaceholderableRandomRange;
import me.wega.blueprint_core.tiered.effect.parameter.JsonField;
import me.wega.blueprint_toolkit.placeholder.PlaceholderableRandomRange;
import me.wega.blueprint_toolkit.placeholder.PlaceholderableString;
import me.wega.blueprint_toolkit.shaded.particlenativeapi.api.packet.ParticlePacket;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Type;

/**
 * Represents an effect that utilizes {@link ParticlePacket} to display particles.
 * Handles all possible particles values and creates a {@link ParticlePacket} from them.
 *
 * @param <T> The type of value that this effect applies
 */
@Getter
public abstract class AbstractParticleEffect<T> extends ViewableTieredObjectEffect<T> {
    private final @NotNull ParticleData particleData;

    public AbstractParticleEffect(@NotNull TieredObjectEffectImpl impl,
                                  @NotNull TargetType @Nullable [] targetTypes,
                                  @Nullable Boolean propagate,
                                  @Nullable EffectPlaceholderableNumber<Integer> propagateDepth,
                                  @NotNull EffectPlaceholderableNumber<Integer> @Nullable [] intervalTicks,
                                  @Nullable EffectPlaceholderableNumber<Integer> repeatCount,
                                  @Nullable Boolean stopOnDeath,
                                  @Nullable Boolean stopOnQuit,
                                  @Nullable Boolean ignoreApply,
                                  @Nullable Boolean ignoreUnApply,

                                  @NotNull ViewerType @Nullable [] viewers,
                                  @JsonField("particle-data") @NotNull ParticleData particleData) {
        super(impl, targetTypes, propagate, propagateDepth, intervalTicks, repeatCount, stopOnDeath, stopOnQuit, ignoreApply, ignoreUnApply, viewers);
        this.particleData = particleData;
    }

    /**
     * Creates a {@link ParticlePacket} from the particle data.
     *
     * @param data         The data of the effect.
     * @param loc          The location to display the particles.
     * @param targetEntity The target entity of the effect.
     * @return The created particle packet.
     */
    public @NotNull ParticlePacket createParticlePacket(@NotNull TieredObjectEffectData data, @NotNull Location loc, @NotNull Entity targetEntity) {
        @Nullable Float offsetX = this.particleData.offsetX == null ? null : this.particleData.offsetX.getRandom(data, targetEntity);
        @Nullable Float offsetY = this.particleData.offsetY == null ? null : this.particleData.offsetY.getRandom(data, targetEntity);
        @Nullable Float offsetZ = this.particleData.offsetZ == null ? null : this.particleData.offsetZ.getRandom(data, targetEntity);
        @Nullable Integer count = this.particleData.count == null ? null : this.particleData.count.getRandom(data, targetEntity);
        @Nullable Integer red = this.particleData.red == null ? null : this.particleData.red.getRandom(data, targetEntity);
        @Nullable Integer green = this.particleData.green == null ? null : this.particleData.green.getRandom(data, targetEntity);
        @Nullable Integer blue = this.particleData.blue == null ? null : this.particleData.blue.getRandom(data, targetEntity);
        @Nullable Float size = this.particleData.size == null ? null : this.particleData.size.getRandom(data, targetEntity);
        @Nullable Float speed = this.particleData.speed == null ? null : this.particleData.speed.getRandom(data, targetEntity);
        @Nullable Integer redFade = this.particleData.redFade == null ? null : this.particleData.redFade.getRandom(data, targetEntity);
        @Nullable Integer greenFade = this.particleData.greenFade == null ? null : this.particleData.greenFade.getRandom(data, targetEntity);
        @Nullable Integer blueFade = this.particleData.blueFade == null ? null : this.particleData.blueFade.getRandom(data, targetEntity);
        @Nullable Float directionX = this.particleData.directionX == null ? null : this.particleData.directionX.getRandom(data, targetEntity);
        @Nullable Float directionY = this.particleData.directionY == null ? null : this.particleData.directionY.getRandom(data, targetEntity);
        @Nullable Float directionZ = this.particleData.directionZ == null ? null : this.particleData.directionZ.getRandom(data, targetEntity);
        @Nullable Float rollRadiansAngle = this.particleData.rollRadiansAngle == null ? null : this.particleData.rollRadiansAngle.getRandom(data, targetEntity);
        @Nullable Integer shriekDelayTicks = this.particleData.shriekDelayTicks == null ? null : this.particleData.shriekDelayTicks.getRandom(data, targetEntity);

        Object unknownParticleType = this.particleData.type.getParticleType(data, targetEntity);
        ParticlePacket particlePacket = ParticleResolver.resolveParticlePacket(
                unknownParticleType,
                loc,
                offsetX, offsetY, offsetZ,
                count,
                size,
                speed,
                directionX, directionY, directionZ,
                red, green, blue,
                redFade, greenFade, blueFade,
                this.particleData.material,
                rollRadiansAngle,
                shriekDelayTicks,
                null, null
        );
        if (particlePacket == null)
            throw new IllegalStateException("Failed to resolve particle packet");

        return particlePacket;
    }

    /**
     * Holds all the possible values that a particle might have.
     */
    @RequiredArgsConstructor
    public static class ParticleData {
        protected final @NotNull EffectPlaceholderableParticleType type;
        protected final @Nullable EffectPlaceholderableRandomRange<Float> offsetX;
        protected final @Nullable EffectPlaceholderableRandomRange<Float> offsetY;
        protected final @Nullable EffectPlaceholderableRandomRange<Float> offsetZ;
        protected final @Nullable EffectPlaceholderableRandomRange<Integer> count;
        protected final @Nullable EffectPlaceholderableRandomRange<Integer> red;
        protected final @Nullable EffectPlaceholderableRandomRange<Integer> green;
        protected final @Nullable EffectPlaceholderableRandomRange<Integer> blue;
        protected final @Nullable EffectPlaceholderableRandomRange<Float> size;
        protected final @Nullable EffectPlaceholderableRandomRange<Float> speed;
        protected final @Nullable EffectPlaceholderableRandomRange<Integer> redFade;
        protected final @Nullable EffectPlaceholderableRandomRange<Integer> greenFade;
        protected final @Nullable EffectPlaceholderableRandomRange<Integer> blueFade;
        protected final @Nullable EffectPlaceholderableRandomRange<Float> directionX;
        protected final @Nullable EffectPlaceholderableRandomRange<Float> directionY;
        protected final @Nullable EffectPlaceholderableRandomRange<Float> directionZ;
        protected final @Nullable Material material;
        protected final @Nullable EffectPlaceholderableRandomRange<Float> rollRadiansAngle;
        protected final @Nullable EffectPlaceholderableRandomRange<Integer> shriekDelayTicks;

        public static class Adapter implements JsonDeserializer<ParticleData> {
            @Override
            public ParticleData deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) {
                JsonObject object = json.getAsJsonObject();
                String sParticle = object.get("particle").getAsString();
                EffectPlaceholderableParticleType particleType = new EffectPlaceholderableParticleType(new PlaceholderableString(sParticle));
                @Nullable EffectPlaceholderableRandomRange<Float> offsetX = (object.has("x")) ? new EffectPlaceholderableRandomRange<>(PlaceholderableRandomRange.deserializeRandomRange(object.get("x"), Float.class)) : null;
                @Nullable EffectPlaceholderableRandomRange<Float> offsetY = (object.has("y")) ? new EffectPlaceholderableRandomRange<>(PlaceholderableRandomRange.deserializeRandomRange(object.get("y"), Float.class)) : null;
                @Nullable EffectPlaceholderableRandomRange<Float> offsetZ = (object.has("z")) ? new EffectPlaceholderableRandomRange<>(PlaceholderableRandomRange.deserializeRandomRange(object.get("z"), Float.class)) : null;
                @Nullable EffectPlaceholderableRandomRange<Integer> count = (object.has("count")) ? new EffectPlaceholderableRandomRange<>(PlaceholderableRandomRange.deserializeRandomRange(object.get("count"), Integer.class)) : null;
                @Nullable EffectPlaceholderableRandomRange<Integer> red = (object.has("red")) ? new EffectPlaceholderableRandomRange<>(PlaceholderableRandomRange.deserializeRandomRange(object.get("red"), Integer.class)) : null;
                @Nullable EffectPlaceholderableRandomRange<Integer> green = (object.has("green")) ? new EffectPlaceholderableRandomRange<>(PlaceholderableRandomRange.deserializeRandomRange(object.get("green"), Integer.class)) : null;
                @Nullable EffectPlaceholderableRandomRange<Integer> blue = (object.has("blue")) ? new EffectPlaceholderableRandomRange<>(PlaceholderableRandomRange.deserializeRandomRange(object.get("blue"), Integer.class)) : null;
                @Nullable EffectPlaceholderableRandomRange<Float> size = (object.has("size")) ? new EffectPlaceholderableRandomRange<>(PlaceholderableRandomRange.deserializeRandomRange(object.get("size"), Float.class)) : null;
                @Nullable EffectPlaceholderableRandomRange<Float> speed = (object.has("speed")) ? new EffectPlaceholderableRandomRange<>(PlaceholderableRandomRange.deserializeRandomRange(object.get("speed"), Float.class)) : null;
                @Nullable EffectPlaceholderableRandomRange<Integer> redFade = (object.has("red-fade")) ? new EffectPlaceholderableRandomRange<>(PlaceholderableRandomRange.deserializeRandomRange(object.get("red-fade"), Integer.class)) : null;
                @Nullable EffectPlaceholderableRandomRange<Integer> greenFade = (object.has("green-fade")) ? new EffectPlaceholderableRandomRange<>(PlaceholderableRandomRange.deserializeRandomRange(object.get("green-fade"), Integer.class)) : null;
                @Nullable EffectPlaceholderableRandomRange<Integer> blueFade = (object.has("blue-fade")) ? new EffectPlaceholderableRandomRange<>(PlaceholderableRandomRange.deserializeRandomRange(object.get("blue-fade"), Integer.class)) : null;
                @Nullable EffectPlaceholderableRandomRange<Float> directionX = (object.has("direction-x")) ? new EffectPlaceholderableRandomRange<>(PlaceholderableRandomRange.deserializeRandomRange(object.get("direction-x"), Float.class)) : null;
                @Nullable EffectPlaceholderableRandomRange<Float> directionY = (object.has("direction-y")) ? new EffectPlaceholderableRandomRange<>(PlaceholderableRandomRange.deserializeRandomRange(object.get("direction-y"), Float.class)) : null;
                @Nullable EffectPlaceholderableRandomRange<Float> directionZ = (object.has("direction-z")) ? new EffectPlaceholderableRandomRange<>(PlaceholderableRandomRange.deserializeRandomRange(object.get("direction-z"), Float.class)) : null;
                @Nullable Material material = (object.has("material")) ? Material.matchMaterial(object.get("material").getAsString()) : null;
                @Nullable EffectPlaceholderableRandomRange<Float> rollAngle = (object.has("roll-angle")) ? new EffectPlaceholderableRandomRange<>(PlaceholderableRandomRange.deserializeRandomRange(object.get("roll-angle"), Float.class)) : null;
                @Nullable EffectPlaceholderableRandomRange<Integer> skriekDelayTicks = (object.has("shriek-delay-ticks")) ? new EffectPlaceholderableRandomRange<>(PlaceholderableRandomRange.deserializeRandomRange(object.get("shriek-delay-ticks"), Integer.class)) : null;
                return new ParticleData(particleType, offsetX, offsetY, offsetZ, count, red, green, blue, size, speed, redFade, greenFade, blueFade, directionX, directionY, directionZ, material, rollAngle, skriekDelayTicks);
            }
        }
    }
}
