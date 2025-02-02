package me.wega.blueprint_core.tiered.effect;

import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import lombok.RequiredArgsConstructor;
import lombok.experimental.UtilityClass;
import me.wega.blueprint_core.tiered.effect.TieredObjectEffectData.TargetType;
import me.wega.blueprint_core.tiered.effect.parameter.*;
import me.wega.blueprint_core.tiered.effect.type.ViewableTieredObjectEffect.ViewerType;
import me.wega.blueprint_core.tiered.tier.TieredObjectTierAdapter;
import me.wega.blueprint_toolkit.placeholder.PlaceholderableNumber;
import me.wega.blueprint_toolkit.placeholder.PlaceholderableRandomRange;
import me.wega.blueprint_toolkit.placeholder.PlaceholderableString;
import me.wega.blueprint_toolkit.placeholder.PlaceholderableUUID;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.BiFunction;

/**
 * Provides automatic deserialization for {@link TieredObjectEffect} implementations and all its constructor parameters.
 * This adapter handles the complex task of deserializing various types of effects and their associated data from JSON format.
 * It uses reflection to find the appropriate constructor for the effect and then deserializes the JSON data into the required parameters
 * based on the {@link JsonField} annotations present on the constructor parameters.
 */
@UtilityClass
public class TieredObjectEffectAdapter {
    private static final Map<Type, FieldDeserializer<?>> FIELD_DESERIALIZERS = new ConcurrentHashMap<>();

    static {
        // TargetType
        FIELD_DESERIALIZERS.put(TargetType.class, new FieldDeserializer<>((ctx, json) -> TargetType.valueOf(json.getAsString())));
        FIELD_DESERIALIZERS.put(TargetType[].class, new FieldDeserializer<>((ctx, json) -> deserializeArray(ctx, json, TargetType.class)));

        // ViewerType
        FIELD_DESERIALIZERS.put(ViewerType.class, new FieldDeserializer<>((ctx, json) -> ViewerType.valueOf(json.getAsString())));
        FIELD_DESERIALIZERS.put(ViewerType[].class, new FieldDeserializer<>((ctx, json) -> deserializeArray(ctx, json, ViewerType.class)));

        // EffectPlaceholderableNumber - Integer
        FIELD_DESERIALIZERS.put(new TypeToken<EffectPlaceholderableNumber<Integer>>() {
        }.getType(), new FieldDeserializer<>((ctx, json) -> new EffectPlaceholderableNumber<>(new PlaceholderableNumber<>(json.getAsString(), Integer.class))));
        FIELD_DESERIALIZERS.put(new TypeToken<EffectPlaceholderableNumber<Integer>[]>() {
        }.getType(), new FieldDeserializer<>((ctx, json) -> deserializeArray(ctx, json, new TypeToken<EffectPlaceholderableNumber<Integer>>() {
        }.getType())));

        // EffectPlaceholderableNumber - Float
        FIELD_DESERIALIZERS.put(new TypeToken<EffectPlaceholderableNumber<Float>>() {
        }.getType(), new FieldDeserializer<>((ctx, json) -> new EffectPlaceholderableNumber<>(new PlaceholderableNumber<>(json.getAsString(), Float.class))));
        FIELD_DESERIALIZERS.put(new TypeToken<EffectPlaceholderableNumber<Float>[]>() {
        }.getType(), new FieldDeserializer<>((ctx, json) -> deserializeArray(ctx, json, new TypeToken<EffectPlaceholderableNumber<Float>>() {
        }.getType())));

        // EffectPlaceholderableRandomRange - Integer
        FIELD_DESERIALIZERS.put(new TypeToken<EffectPlaceholderableRandomRange<Integer>>() {
        }.getType(), new FieldDeserializer<>((ctx, json) -> new EffectPlaceholderableRandomRange<>(PlaceholderableRandomRange.deserializeRandomRange(json, Integer.class))));
        FIELD_DESERIALIZERS.put(new TypeToken<EffectPlaceholderableRandomRange<Integer>[]>() {
        }.getType(), new FieldDeserializer<>((ctx, json) -> deserializeArray(ctx, json, new TypeToken<EffectPlaceholderableRandomRange<Integer>>() {
        }.getType())));

        // EffectPlaceholderableRandomRange - Float
        FIELD_DESERIALIZERS.put(new TypeToken<EffectPlaceholderableRandomRange<Float>>() {
        }.getType(), new FieldDeserializer<>((ctx, json) -> new EffectPlaceholderableRandomRange<>(PlaceholderableRandomRange.deserializeRandomRange(json, Float.class))));
        FIELD_DESERIALIZERS.put(new TypeToken<EffectPlaceholderableRandomRange<Float>[]>() {
        }.getType(), new FieldDeserializer<>((ctx, json) -> deserializeArray(ctx, json, new TypeToken<EffectPlaceholderableRandomRange<Float>>() {
        }.getType())));

        // EffectPlaceholderableString
        FIELD_DESERIALIZERS.put(EffectPlaceholderableString.class, new FieldDeserializer<>((ctx, json) -> new EffectPlaceholderableString(new PlaceholderableString(json.getAsString()))));
        FIELD_DESERIALIZERS.put(EffectPlaceholderableString[].class, new FieldDeserializer<>((ctx, json) -> deserializeArray(ctx, json, EffectPlaceholderableString.class)));

        // EffectPlaceholderableUUID
        FIELD_DESERIALIZERS.put(EffectPlaceholderableUUID.class, new FieldDeserializer<>((ctx, json) -> new EffectPlaceholderableUUID(new PlaceholderableUUID(json.getAsString()))));
        FIELD_DESERIALIZERS.put(EffectPlaceholderableUUID[].class, new FieldDeserializer<>((ctx, json) -> deserializeArray(ctx, json, EffectPlaceholderableUUID.class)));

        // TieredObjectEffect
        FIELD_DESERIALIZERS.put(new TypeToken<TieredObjectEffect<?>>() {
        }.getType(), new FieldDeserializer<>((ctx, json) -> TieredObjectTierAdapter.deserializeEffects(ctx, TieredObjectTierAdapter.getEffectsArray(json))[0]));
        FIELD_DESERIALIZERS.put(new TypeToken<TieredObjectEffect<?>[]>() {
        }.getType(), new FieldDeserializer<>((ctx, json) -> TieredObjectTierAdapter.deserializeEffects(ctx, TieredObjectTierAdapter.getEffectsArray(json))));
    }

    /**
     * Deserializes the effect data from the given JSON object.
     * Supports deserialization of a single value into an array
     */
    @SuppressWarnings("unchecked")
    private static <T> T[] deserializeArray(JsonDeserializationContext ctx, JsonElement json, Type type) {
        FieldDeserializer<T> deserializer = getDeserializer(type);
        Class<?> rawType = TypeToken.get(type).getRawType();
        if (json.isJsonArray()) {
            JsonArray jsonArray = json.getAsJsonArray();
            T[] array = (T[]) Array.newInstance(rawType, jsonArray.size());
            for (int i = 0; i < jsonArray.size(); i++) {
                array[i] = deserializer.deserialize(ctx, jsonArray.get(i));
            }
            return array;
        } else {
            T[] array = (T[]) Array.newInstance(rawType, 1);
            array[0] = deserializer.deserialize(ctx, json);
            return array;
        }
    }

    /**
     * Deserializes the effect data from the given JSON object and constructs a new instance of the effect.
     * This method uses reflection to find the appropriate constructor for the effect and then deserializes the JSON data into the required parameters
     * based on the {@link JsonField} annotations present on the constructor parameters.
     *
     * @param ctx        The deserialization context
     * @param effectImpl The effect implementation
     * @param effectData The JSON object containing the effect data
     * @return A new instance of the effect
     * @throws JsonParseException If the effect instance could not be created
     */
    public static @NotNull TieredObjectEffect<?> deserializeEffect(JsonDeserializationContext ctx, TieredObjectEffectImpl effectImpl, JsonObject effectData) throws JsonParseException {
        Constructor<?> constructor = findMatchingConstructor(effectImpl.getEffectClass());
        String[] fields = extractFieldNamesFromAnnotations(constructor);
        Object[] args = deserializeConstructorArguments(constructor, ctx, effectData, fields);
        try {
            return (TieredObjectEffect<?>) constructor.newInstance(args);
        } catch (Exception e) {
            throw new JsonParseException("Failed to create effect instance", e);
        }
    }

    private static @NotNull Constructor<?> findMatchingConstructor(Class<?> effectClass) {
        return Arrays.stream(effectClass.getDeclaredConstructors())
                .filter(c -> Modifier.isPublic(c.getModifiers()))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("No public constructor found for " + effectClass.getName()));
    }


    /**
     * Extracts the field names from the {@link JsonField} annotations present on the constructor parameters.
     * The field names are extracted from the most super class to the subclass.
     * That allows the subclass parameters to not have the {@link JsonField} annotation if the superclass parameters already have it.
     *
     * @param constructor The constructor to extract the field names from
     * @return An array of field names
     */
    private static String[] extractFieldNamesFromAnnotations(Constructor<?> constructor) {
        List<String> fieldNames = new ArrayList<>();
        List<Class<?>> classHierarchy = new ArrayList<>();
        Class<?> clazz = constructor.getDeclaringClass();

        // Build the class hierarchy
        while (clazz != null && !clazz.equals(Object.class)) {
            classHierarchy.add(clazz);
            clazz = clazz.getSuperclass();
        }

        // Process classes from most super to subclass
        for (int i = classHierarchy.size() - 1; i >= 0; i--) {
            clazz = classHierarchy.get(i);
            Constructor<?> currentConstructor = findMatchingConstructor(clazz);

            for (Parameter parameter : currentConstructor.getParameters()) {
                JsonField fieldNameAnnotation = parameter.getAnnotation(JsonField.class);
                if (fieldNameAnnotation == null) continue;
                fieldNames.add(fieldNameAnnotation.value());
            }
        }

        return fieldNames.toArray(String[]::new);
    }

    private static @Nullable Object @NotNull [] deserializeConstructorArguments(Constructor<?> constructor, JsonDeserializationContext ctx, JsonObject effectData, String... fields) {
        Object[] args = new Object[constructor.getParameterCount()];
        Parameter[] parameters = constructor.getParameters();
        for (int i = 0; i < constructor.getParameterCount(); i++) {
            if (fields.length <= i)
                throw new JsonParseException("Missing field name for constructor parameter index: " + i);

            args[i] = deserializeField(ctx, effectData, fields[i], parameters[i].getParameterizedType());
        }
        return args;
    }

    private static <T> @Nullable T deserializeField(JsonDeserializationContext ctx, JsonObject json, String fieldName, Type fieldType) {
        if (!json.has(fieldName)) return null;
        FieldDeserializer<T> deserializer = getDeserializer(fieldType);
        return deserializer.deserialize(ctx, json.get(fieldName));
    }

    @SuppressWarnings("unchecked")
    private static <T> @NotNull FieldDeserializer<T> getDeserializer(Type type) {
        return (FieldDeserializer<T>) FIELD_DESERIALIZERS.getOrDefault(type, new FieldDeserializer<>(((context, element) -> context.deserialize(element, type))));
    }

    @RequiredArgsConstructor
    private static class FieldDeserializer<T> {
        private final BiFunction<JsonDeserializationContext, JsonElement, T> deserializer;

        public T deserialize(JsonDeserializationContext context, JsonElement element) {
            return deserializer.apply(context, element);
        }
    }
}
