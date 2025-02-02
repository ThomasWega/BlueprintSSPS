package me.wega.blueprint_toolkit.pdc.data;

import lombok.experimental.UtilityClass;

/**
 * Contains list of all custom data types.
 */
@UtilityClass
public class CustomDataType {
    public static final ComponentDataType COMPONENT = new ComponentDataType();
    public static final NumberDataType NUMBER = new NumberDataType();

    public static <T extends Enum<T>> EnumDataType<T> enumDataType(Class<T> enumClass) {
        return new EnumDataType<>(enumClass);
    }

    public static <T extends Class<T>> ClassDataType<T> classDataType(Class<T> clazz) {
        return new ClassDataType<>(clazz);
    }
}
