package com.eteirnum.toolkit.utils;

import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitTask;
import org.jetbrains.annotations.NotNull;

import static com.eteirnum.toolkit.EteirnumToolkit.instance;

/**
 * Utility class to handle scheduler tasks
 */
public class SchedulerUtils {

    /**
     * Runs a Runnable after a certain amount of ticks asynchronously.
     *
     * @return The BukkitTask
     */
    public static @NotNull BukkitTask runTaskLaterAsync(Runnable task, long delayTicks) {
        return Bukkit.getScheduler().runTaskLaterAsynchronously(instance, task, delayTicks);
    }

    /**
     * Runs a Runnable after 1 server tick asynchronously.
     *
     * @return The BukkitTask
     */
    public static @NotNull BukkitTask runTaskLaterAsync(Runnable task) {
        return runTaskLaterAsync(task, 1L);
    }

    /**
     * Runs a Runnable after a certain amount of ticks.
     *
     * @return The BukkitTask
     */
    public static @NotNull BukkitTask runTaskLater(Runnable task, long delayTicks) {
        return Bukkit.getScheduler().runTaskLater(instance, task, delayTicks);
    }

    /**
     * Runs a Runnable after 1 server tick.
     *
     * @return The BukkitTask
     */
    public static @NotNull BukkitTask runTaskLater(Runnable task) {
        return runTaskLater(task, 1L);
    }

    /**
     * Runs a Runnable every x ticks.
     *
     * @param initialDelay The time to delay the first execution.
     * @param ticks       The tick period between successive executions.
     * @return The Bukkit task
     */
    public static @NotNull BukkitTask runTaskTimer(Runnable task, long initialDelay, long ticks) {
        return Bukkit.getScheduler().runTaskTimer(instance, task, initialDelay, ticks);
    }

    /**
     * Runs a Runnable every x ticks asynchronously.
     *
     * @param initialDelay The time to delay the first execution.
     * @param ticks       The tick period between successive executions.
     * @return The Bukkit task
     */
    public static @NotNull BukkitTask runTaskTimerAsync(Runnable task, long initialDelay, long ticks) {
        return Bukkit.getScheduler().runTaskTimerAsynchronously(instance, task, initialDelay, ticks);
    }

    /**
     * Runs a Runnable at the next server tick.
     *
     * @return The BukkitTask
     */
    public static @NotNull BukkitTask runTask(Runnable task) {
        return Bukkit.getScheduler().runTask(instance, task);
    }


    /**
     * Runs a Runnable asynchronously.
     *
     * @return The BukkitTask
     */
    public static @NotNull BukkitTask runTaskAsync(Runnable task) {
        return Bukkit.getScheduler().runTaskAsynchronously(instance, task);
    }
}
