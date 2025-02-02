package me.wega.blueprint_core.tiered.effect.impl;

import com.google.common.base.Preconditions;
import lombok.Getter;
import me.wega.blueprint_core.tiered.effect.TieredObjectEffect;
import me.wega.blueprint_core.tiered.effect.TieredObjectEffectData;
import me.wega.blueprint_core.tiered.effect.TieredObjectEffectData.TargetType;
import me.wega.blueprint_core.tiered.effect.TieredObjectEffectImpl;
import me.wega.blueprint_core.tiered.effect.parameter.EffectPlaceholderableNumber;
import me.wega.blueprint_core.tiered.effect.parameter.EffectPlaceholderableRandomRange;
import me.wega.blueprint_core.tiered.effect.parameter.EffectPlaceholderableString;
import me.wega.blueprint_core.tiered.effect.parameter.JsonField;
import me.wega.blueprint_toolkit.utils.ColorUtils;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.title.Title;
import net.kyori.adventure.title.TitlePart;
import org.bukkit.entity.Entity;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.time.Duration;
import java.util.Map;

/**
 * Represents an effect that will send a message to the target.
 */
@Getter
public class MsgEffect extends TieredObjectEffect<Component> {
    private final @NotNull MsgType msgType;
    private final @NotNull EffectPlaceholderableString msg;
    private final @Nullable EffectPlaceholderableRandomRange<Integer> fadeIn;
    private final @Nullable EffectPlaceholderableRandomRange<Integer> stay;
    private final @Nullable EffectPlaceholderableRandomRange<Integer> fadeOut;

    public MsgEffect(@NotNull TargetType @Nullable [] targetTypes,
                     @Nullable Boolean propagate,
                     @Nullable EffectPlaceholderableNumber<Integer> propagateDepth,
                     @NotNull EffectPlaceholderableNumber<Integer> @Nullable [] intervalTicks,
                     @Nullable EffectPlaceholderableNumber<Integer> repeatCount,
                     @Nullable Boolean stopOnDeath,
                     @Nullable Boolean stopOnQuit,
                     @Nullable Boolean ignoreApply,
                     @Nullable Boolean ignoreUnApply,
                     @JsonField("type") @Nullable MsgType msgType,
                     @JsonField("msg") @NotNull EffectPlaceholderableString msg,
                     @JsonField("fade-in") @Nullable EffectPlaceholderableRandomRange<Integer> fadeIn,
                     @JsonField("stay") @Nullable EffectPlaceholderableRandomRange<Integer> stay,
                     @JsonField("fade-out") @Nullable EffectPlaceholderableRandomRange<Integer> fadeOut) {
        super(TieredObjectEffectImpl.MSG, targetTypes, propagate, propagateDepth, intervalTicks, repeatCount, stopOnDeath, stopOnQuit, ignoreApply, ignoreUnApply);
        this.msgType = msgType == null ? MsgType.CHAT : msgType;
        this.msg = msg;
        this.fadeIn = fadeIn;
        this.stay = stay;
        this.fadeOut = fadeOut;
    }

    @Override
    public @Nullable Component applyInternal(@NotNull TieredObjectEffectData data, @NotNull Map.Entry<@NotNull TargetType, @NotNull Entity> target) {
        Entity targetEntity = target.getValue();
        Component coloredMsg = null;
        if (msgType != MsgType.TITLE_TIME) {
            Preconditions.checkNotNull(msg, "msg must be set for msg effect");
            coloredMsg = ColorUtils.color(msg.getString(data, targetEntity));
        }
        switch (msgType) {
            case CHAT -> targetEntity.sendMessage(coloredMsg);
            case ACTION_BAR -> targetEntity.sendActionBar(coloredMsg);
            case TITLE -> targetEntity.sendTitlePart(TitlePart.TITLE, coloredMsg);
            case SUBTITLE -> targetEntity.sendTitlePart(TitlePart.SUBTITLE, coloredMsg);
            case TITLE_TIME -> {
                Preconditions.checkNotNull(fadeIn, "fade-in time must be set for title time effect");
                Preconditions.checkNotNull(stay, "stay must be set for title time effect");
                Preconditions.checkNotNull(fadeOut, "fade-out time must be set for title time effect");

                int fadeIn = this.fadeIn.getRandom(data, targetEntity);
                int stay = this.stay.getRandom(data, targetEntity);
                int fadeOut = this.fadeOut.getRandom(data, targetEntity);

                targetEntity.sendTitlePart(TitlePart.TIMES, Title.Times.times(
                        Duration.ofMillis(fadeIn * 50L),
                        Duration.ofMillis(stay * 50L),
                        Duration.ofMillis(fadeOut * 50L)
                ));
            }
        }
        return coloredMsg;
    }

    public enum MsgType {
        CHAT,
        ACTION_BAR,
        TITLE,
        SUBTITLE,
        TITLE_TIME
    }
}
