package me.wega.blueprint_toolkit.pdc.data;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.bukkit.persistence.PersistentDataAdapterContext;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;

@RequiredArgsConstructor
public class ClassDataType<T> implements PersistentDataType<String, Class<T>> {
    private final Class<T> clazz;

    @Override
    public @NotNull Class<String> getPrimitiveType() {
        return String.class;
    }

    @Override
    public @NotNull Class<Class<T>> getComplexType() {
        return (Class<Class<T>>) clazz;
    }

    @Override
    public @NotNull String toPrimitive(@NotNull Class<T> complex, @NotNull PersistentDataAdapterContext context) {
        return complex.getName();
    }

    @Override
    @SneakyThrows
    public @NotNull Class<T> fromPrimitive(@NotNull String primitive, @NotNull PersistentDataAdapterContext context) {
        return (Class<T>) Class.forName(primitive);
    }
}