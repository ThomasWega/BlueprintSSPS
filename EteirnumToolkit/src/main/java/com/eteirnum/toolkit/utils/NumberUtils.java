package com.eteirnum.toolkit.utils;

import lombok.SneakyThrows;
import lombok.experimental.UtilityClass;
import org.jetbrains.annotations.NotNull;

import java.math.BigDecimal;
import java.text.NumberFormat;

/**
 * Utility class for number operations
 */
@UtilityClass
public class NumberUtils {

    /**
     * Parses a number from a string
     *
     * @param str String to parse
     * @return Parsed number
     */
    @SneakyThrows
    public static @NotNull Number fromString(@NotNull String str) {
        return NumberFormat.getInstance().parse(str);
    }

    /**
     * Rounds a number to the given number of decimal places
     *
     * @param toRound Number to round
     * @param roundNum Number of decimal places to round to
     * @param <T> Number type
     * @return Rounded number
     */
    @SuppressWarnings("unchecked")
    public static <T extends Number> T roundBy(@NotNull T toRound, int roundNum) {
        double result = Math.round(toRound.doubleValue() * Math.pow(10, roundNum)) / Math.pow(10, roundNum);
        return getGeneric(result, (Class<T>) toRound.getClass());
    }

    /**
     * Negates a number
     *
     * @param toRound Number to negate
     * @param <T> Number type
     * @return Negated number
     */
    public static <T extends Number> T negate(@NotNull T toRound) {
        return getGeneric(-toRound.doubleValue(), (Class<T>) toRound.getClass());
    }

    /**
     * Gets the specific generic number type
     *
     * @param val Number to get the generic type from
     * @param clazz Class of the number
     * @param <T> Number type
     * @return Generic number type
     */
    @SuppressWarnings("unchecked")
    public static <T extends Number> T getGeneric(@NotNull Number val, @NotNull Class<T> clazz) {
        if (clazz == Integer.class) {
            return (T) Integer.valueOf(val.intValue());
        } else if (clazz == Double.class) {
            return (T) Double.valueOf(val.doubleValue());
        } else if (clazz == Float.class) {
            return (T) Float.valueOf(val.floatValue());
        } else if (clazz == Long.class) {
            return (T) Long.valueOf(val.longValue());
        } else if (clazz == Short.class) {
            return (T) Short.valueOf(val.shortValue());
        } else if (clazz == Byte.class) {
            return (T) Byte.valueOf(val.byteValue());
        } else if (clazz == BigDecimal.class) {
            return (T) BigDecimal.valueOf(val.doubleValue());
        }
        throw new IllegalArgumentException("Unsupported Number type");
    }

    /**
     * Gets the maximum value of the given number type
     *
     * @param numClass Number class to get the maximum value from
     * @param <T> Number type
     * @return Maximum value of the number type
     */
    public static <T extends Number> T getMaxValue(@NotNull Class<T> numClass) {
        if (numClass == Integer.class) {
            return getGeneric(Integer.MAX_VALUE, numClass);
        } else if (numClass == Double.class || numClass == BigDecimal.class) {
            return getGeneric(Double.MAX_VALUE, numClass);
        } else if (numClass == Float.class) {
            return getGeneric(Float.MAX_VALUE, numClass);
        } else if (numClass == Long.class) {
            return getGeneric(Long.MAX_VALUE, numClass);
        } else if (numClass == Short.class) {
            return getGeneric(Short.MAX_VALUE, numClass);
        } else if (numClass == Byte.class) {
            return getGeneric(Byte.MAX_VALUE, numClass);
        }
        throw new IllegalArgumentException("Unsupported Number type");
    }
}
