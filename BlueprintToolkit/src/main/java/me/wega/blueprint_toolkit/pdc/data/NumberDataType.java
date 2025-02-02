package me.wega.blueprint_toolkit.pdc.data;

import me.wega.blueprint_toolkit.utils.NumberUtils;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.bukkit.persistence.PersistentDataAdapterContext;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;

@RequiredArgsConstructor
public class NumberDataType implements PersistentDataType<String, Number> {

    @Override
    public @NotNull Class<String> getPrimitiveType() {
        return String.class;
    }

    @Override
    public @NotNull Class<Number> getComplexType() {
        return Number.class;
    }

    @Override
    public @NotNull String toPrimitive(@NotNull Number complex, @NotNull PersistentDataAdapterContext context) {
        return complex.toString();
    }

    @Override
    @SneakyThrows
    public @NotNull Number fromPrimitive(@NotNull String primitive, @NotNull PersistentDataAdapterContext context) {
        return NumberUtils.fromString(primitive);
    }
}