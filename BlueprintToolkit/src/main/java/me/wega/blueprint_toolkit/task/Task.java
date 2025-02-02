package me.wega.blueprint_toolkit.task;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.bukkit.scheduler.BukkitTask;
import org.jetbrains.annotations.Nullable;

/**
 * Represents a task that can be run and stopped.
 */
@RequiredArgsConstructor
@Getter
@Setter
public abstract class Task {
    private @Nullable BukkitTask task;
    private @Nullable Runnable onComplete;

    /**
     * Runs the task.
     * @return the task that was started, or null if the task was not started.
     */
    protected abstract @Nullable BukkitTask runInternal();
    protected abstract void stopInternal();

    /**
     * Runs the task.
     * @return true if the task was started, otherwise false.
     */
    public final boolean run() {
        this.task = this.runInternal();
        return this.task != null;
    }

    public final void stop() {
        if (this.task != null) {
            this.task.cancel();
            this.task = null;
        }
        this.stopInternal();
    }

    public boolean isRunning() {
        return this.task != null;
    }
}
