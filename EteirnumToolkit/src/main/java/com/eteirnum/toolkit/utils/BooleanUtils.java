package com.eteirnum.toolkit.utils;

import lombok.experimental.UtilityClass;
import org.jetbrains.annotations.NotNull;

@UtilityClass
public class BooleanUtils {

    /**
     * Converts a boolean to a "yes" or "no" string.
     *
     * @param bool the boolean to convert
     * @return "yes" if the boolean is true, "no" if the boolean is false
     */
    public static @NotNull String booleanToYesNo(boolean bool) {
        return bool ? "yes" : "no";
    }
}
