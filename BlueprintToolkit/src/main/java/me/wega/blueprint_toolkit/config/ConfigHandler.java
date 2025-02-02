package me.wega.blueprint_toolkit.config;

/*
  Spigot-CustomConfig
  <p>
  Copyright Katorly Lab
  (github.com/katorlys)
  <p>
  License under GPLv3
 */

import me.wega.blueprint_toolkit.BlueprintToolkit;
import me.wega.blueprint_toolkit.utils.SchedulerUtils;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Range;

import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.logging.Logger;

/**
 * A class to handle the config file.
 */
public class ConfigHandler {
    private final JavaPlugin plugin;
    private final String name;
    private final String path;
    private File file;
    private File filepath;
    private YamlConfiguration config;

    public ConfigHandler(@NotNull JavaPlugin plugin, @NotNull String pathname, @NotNull String filename) {
        this.plugin = plugin;
        this.path = pathname;
        this.name = filename;
        this.filepath = new File(plugin.getDataFolder(), path);
        this.file = new File(filepath, name);
    }

    /**
     * Excute the function of saveConfig() and reloadConfig()
     */
    public static void save(ConfigHandler config) {
        config.saveConfig();
        config.reloadConfig();
    }

    /**
     * Create the default config if the config does not exist.
     */
    public void saveDefaultConfig() {
        if (!filepath.exists()) {
            boolean success = filepath.mkdirs();
            if (!success)
                LOGGER.severe("Error creating the config. Please try again.");
        }
        if (!file.exists()) {
            if (this.plugin.getResource(path + name) != null) {
                this.plugin.saveResource(path + name, false);
                LOGGER.info("Created file " + this.file.getPath() + " with default values.");
            } else try {
                file.createNewFile();
            } catch (Throwable t) {
                Bukkit.getLogger().severe("Error creating the config. Please try again.");
            }
        }
    }

    /**
     * Get values in the config.
     */
    public YamlConfiguration getConfig() {
        if (!filepath.exists())
            this.saveDefaultConfig();
        if (config == null)
            this.reloadConfig();
        return config;
    }

    /**
     * Reload the config. This will remove all the comments in it.
     */
    public void reloadConfig() {
        if (filepath == null)
            filepath = new File(plugin.getDataFolder(), path);
        if (file == null)
            file = new File(filepath, name);
        config = YamlConfiguration.loadConfiguration(file);
        InputStream stream = plugin.getResource(name);
        if (stream != null) {
            YamlConfiguration YmlFile = YamlConfiguration.loadConfiguration(new InputStreamReader(stream));
            config.setDefaults(YmlFile);
        }
    }
    
    private static final Logger LOGGER = BlueprintToolkit.instance.getLogger();

    /**
     * Save the config to apply changes.
     */
    public void saveConfig() {
        try {
            config.save(file);
        } catch (Throwable t) {
            LOGGER.severe("Error saving the config. Please try again.");
        }
    }

    public void saveConfigWithBackup() {
        try {
            File backup = new File(filepath, name + ".bak");
            if (backup.exists())
                backup.delete();
            file.renameTo(backup);
            config.save(file);
        } catch (Throwable t) {
            LOGGER.severe("Error saving the config with backup. Please try again.");
        }
    }

    /**
     * Every x ticks the config will be saved asynchronously.
     * @param ticks Tick amount
     */
    public void scheduleAsyncBackup(@Range(from = 0, to = Long.MAX_VALUE) int ticks) {
        this.scheduleAsyncBackup(ticks, () -> {});
    }

    /**
     * Every x ticks the config will be saved asynchronously.
     * @param ticks Tick amount
     * @param actionBefore Action to be executed before the config is saved
     */
    public void scheduleAsyncBackup(@Range(from = 0, to = Long.MAX_VALUE) int ticks, @NotNull Runnable actionBefore) {
        SchedulerUtils.runTaskTimerAsync(() -> {
            actionBefore.run();
            this.saveConfig();
            LOGGER.fine("Saved file " + this.file.getPath() + " asynchronously with backup task.");
        }, ticks, ticks);
    }
}