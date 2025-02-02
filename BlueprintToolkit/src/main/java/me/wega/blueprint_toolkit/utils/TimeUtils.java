package me.wega.blueprint_toolkit.utils;

import lombok.experimental.UtilityClass;
import org.jetbrains.annotations.NotNull;

import java.time.Duration;

/**
 * Utility class to handle time related operations
 */
@UtilityClass
public class TimeUtils {

    /**
     * Formats a time in seconds to a day-hour-minute-second format,
     * omitting the time unit if it is 0
     *
     * @param timeSec Time in seconds
     * @return Formatted time
     */
    public static @NotNull String formatTime(int timeSec) {
        Duration duration = Duration.ofSeconds(timeSec);

        long days = duration.toDays();
        long hours = duration.toHours() % 24;
        long minutes = duration.toMinutes() % 60;
        long seconds = duration.getSeconds() % 60;

        StringBuilder formattedTime = new StringBuilder();

        if (days > 0)
            formattedTime.append(days).append("d ");
        if (hours > 0)
            formattedTime.append(hours).append("h ");
        if (minutes > 0)
            formattedTime.append(minutes).append("m ");
        if (seconds > 0 && days == 0 && hours == 0)
            formattedTime.append(seconds).append("s");

        return formattedTime.toString().trim();
    }
}
