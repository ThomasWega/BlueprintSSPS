package me.wega.blueprint_core.particle;

import me.wega.blueprint_toolkit.shaded.particlenativeapi.api.packet.ParticlePacket;
import me.wega.blueprint_toolkit.shaded.particlenativeapi.api.particle.type.*;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.gson.JsonParseException;
import lombok.experimental.UtilityClass;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Material;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Field;
import java.util.concurrent.ExecutionException;

import static me.wega.blueprint_core.BlueprintCore.instance;

/**
 * Utility class for particle type resolution.
 */
@UtilityClass
public class ParticleResolver {
    private static final Cache<String, Object> PARTICLE_CACHE = CacheBuilder.newBuilder()
            .build();

    /**
     * Get the particle type from the given string
     * Needs to then be cast to the correct {@link me.wega.blueprint_toolkit.shaded.particlenativeapi.api.particle.type Particle type} implementation
     *
     * @param sParticle The particle type string
     * @return The particle type
     * @throws JsonParseException If the particle type is invalid
     */
    public static @NotNull Object getParticleType(@NotNull String sParticle) {
        try {
            return PARTICLE_CACHE.get(sParticle.toUpperCase(), () -> {
                try {
                    Object list_1_13_instance = instance.getParticleAPI().LIST_1_13;
                    Field field = list_1_13_instance.getClass().getField(sParticle.toUpperCase());
                    field.setAccessible(true);
                    return field.get(list_1_13_instance);
                } catch (NoSuchFieldException | IllegalAccessException e) {
                    throw new JsonParseException("Invalid particle type: " + sParticle);
                }
            });
        } catch (ExecutionException e) {
            throw new JsonParseException("Error loading particle type: " + sParticle, e);
        }
    }

    public static @Nullable ParticlePacket resolveParticlePacket(
            @Nullable Object unknownParticleType,
            @NotNull Location loc,
            @Nullable Float offsetX,
            @Nullable Float offsetY,
            @Nullable Float offsetZ,
            @Nullable Integer count,
            @Nullable Float size,
            @Nullable Float speed,
            @Nullable Float directionX,
            @Nullable Float directionY,
            @Nullable Float directionZ,
            @Nullable Integer red,
            @Nullable Integer green,
            @Nullable Integer blue,
            @Nullable Integer redFade,
            @Nullable Integer greenFade,
            @Nullable Integer blueFade,
            @Nullable Material material,
            @Nullable Float rollAngle,
            @Nullable Integer shriekDelayTicks,
            @Nullable Location flyingTo,
            @Nullable Integer flyingToTicks) {

        // DEFAULTS
        offsetX = (offsetX == null) ? 0 : offsetX;
        offsetY = (offsetY == null) ? 0 : offsetY;
        offsetZ = (offsetZ == null) ? 0 : offsetZ;
        count = (count == null) ? 0 : count;
        size = (size == null) ? 1 : size;
        speed = (speed == null) ? 1 : speed;
        directionX = (directionX == null) ? 0 : directionX;
        directionY = (directionY == null) ? 0 : directionY;
        directionZ = (directionZ == null) ? 0 : directionZ;
        red = (red == null) ? 255 : red;
        green = (green == null) ? 255 : green;
        blue = (blue == null) ? 255 : blue;
        redFade = (redFade == null) ? 255 : redFade;
        greenFade = (greenFade == null) ? 255 : greenFade;
        blueFade = (blueFade == null) ? 255 : blueFade;
        material = (material == null) ? Material.STONE : material;
        rollAngle = (rollAngle == null) ? 0 : (float) Math.toRadians(rollAngle);
        shriekDelayTicks = (shriekDelayTicks == null) ? 0 : shriekDelayTicks;
        flyingTo = (flyingTo == null) ? loc : flyingTo;
        flyingToTicks = (flyingToTicks == null) ? 0 : flyingToTicks;
        
        boolean isMotion = speed != 0;

        ParticlePacket particlePacket = null;
        Location offsetLoc = loc.clone().add(offsetX, offsetY, offsetZ);
        if (unknownParticleType instanceof ParticleTypeBlock particleTypeBlock) {
            particlePacket = particleTypeBlock
                    .of(material)
                    .packet(
                            true,
                            offsetLoc,
                            size,
                            count
                    );
        } else if (unknownParticleType instanceof ParticleTypeBlockMotion particleTypeBlockMotion && isMotion) {
            particlePacket = particleTypeBlockMotion
                    .of(material)
                    .packetMotion(
                            true,
                            offsetLoc,
                            directionX,
                            directionY,
                            directionZ
                    );
        } else if (unknownParticleType instanceof ParticleTypeColor particleTypeColor) {
            particlePacket = particleTypeColor
                    .color(red, green, blue)
                    .packet(
                            true,
                            offsetLoc,
                            size,
                            count
                    );
        } else if (unknownParticleType instanceof ParticleTypeColorable particleTypeColorable) {
            particlePacket = particleTypeColorable
                    .packetColored(
                            true,
                            offsetLoc,
                            red,
                            green,
                            blue
                    );
        } else if (unknownParticleType instanceof ParticleTypeDust particleTypeDust) {
            particlePacket = particleTypeDust
                    .color(red, green, blue, size)
                    .packet(
                            true,
                            offsetLoc,
                            speed,
                            count
                    );
        } else if (unknownParticleType instanceof ParticleTypeDustColorTransition particleTypeDustColorTransition) {
            particlePacket = particleTypeDustColorTransition
                    .color(Color.fromRGB(red, green, blue), Color.fromRGB(redFade, greenFade, blueFade), size)
                    .packet(
                            true,
                            offsetLoc,
                            speed,
                            count
                    );
        } else if (unknownParticleType instanceof ParticleTypeItemMotion particleTypeItemMotion && isMotion) {
            particlePacket = particleTypeItemMotion
                    .of(material)
                    .packetMotion(
                            true,
                            offsetLoc,
                            directionX,
                            directionY,
                            directionZ
                    );
        } else if (unknownParticleType instanceof ParticleTypeMotion particleTypeMotion && isMotion) {
            particlePacket = particleTypeMotion
                    .packetMotion(
                            true,
                            offsetLoc,
                            directionX,
                            directionY,
                            directionZ
                    );
        } else if (unknownParticleType instanceof ParticleTypeNote particleTypeNote) {
            particlePacket = particleTypeNote
                    .packetNote(
                            true,
                            offsetLoc,
                            red,
                            green,
                            blue
                    );
        } else if (unknownParticleType instanceof ParticleTypeRedstone particleTypeRedstone) {
            particlePacket = particleTypeRedstone
                    .packetColored(
                            true,
                            offsetLoc,
                            red,
                            green,
                            blue
                    );
        } else if (unknownParticleType instanceof ParticleTypeSculkChargeMotion particleTypeSculkChargeMotion && isMotion) {
            particlePacket = particleTypeSculkChargeMotion
                    .roll(rollAngle)
                    .packetMotion(
                            true,
                            offsetLoc,
                            directionX,
                            directionY,
                            directionZ
                    );
        } else if (unknownParticleType instanceof ParticleTypeShriek particleTypeShriek) {
            particlePacket = particleTypeShriek
                    .delay(shriekDelayTicks)
                    .packet(
                            true,
                            offsetLoc,
                            speed,
                            count
                    );
        } else if (unknownParticleType instanceof ParticleTypeVibration particleTypeVibration) {
            particlePacket = particleTypeVibration
                    .flyingTo(flyingTo, flyingToTicks)
                    .packet(
                            true,
                            offsetLoc,
                            speed,
                            count
                    );
        } else if (unknownParticleType instanceof ParticleTypeVibrationSingle particleTypeVibrationSingle) {
            particlePacket = particleTypeVibrationSingle
                    .packet(
                            true,
                            offsetLoc,
                            flyingTo,
                            flyingToTicks
                    );
        } else if (unknownParticleType instanceof ParticleType particleType) {
            particlePacket = particleType.detachCopy()
                    .packet(
                            true,
                            offsetLoc,
                            speed,
                            count
                    );
        }

        return particlePacket;
    }
}