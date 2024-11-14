package com.eteirnum.toolkit.pdc.data;

import lombok.RequiredArgsConstructor;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.persistence.PersistentDataAdapterContext;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;

@RequiredArgsConstructor
public class ComponentDataType implements PersistentDataType<String, Component> {

    @Override
    public @NotNull Class<String> getPrimitiveType() {
        return String.class;
    }

    @Override
    public @NotNull Class<Component> getComplexType() {
        return Component.class;
    }

    @Override
    public @NotNull String toPrimitive(@NotNull Component complex, @NotNull PersistentDataAdapterContext context) {
        return MiniMessage.miniMessage().serialize(complex);
    }

    @Override
    public @NotNull Component fromPrimitive(@NotNull String primitive, @NotNull PersistentDataAdapterContext context) {
        return MiniMessage.miniMessage().deserialize(primitive);
    }
}
