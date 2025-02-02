package me.wega.blueprint_toolkit.file;

import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * A utility class to load files from the resources folder of a plugin
 */
public class FileLoader {

    /**
     * Creates the new files (if not exists)
     * with the default contents of the same files in the resources folder
     *
     * @param plugin    The plugin to load the files from
     * @param filePaths The paths of the files to load
     * @return The created files with filled in default values
     */
    public static @NotNull Map<String, File> loadFile(@NotNull JavaPlugin plugin, @NotNull String... filePaths) {
        Map<String, File> files = new HashMap<>();
        for (String filePath : filePaths) {
            Path configPath = plugin.getDataFolder().toPath().resolve(filePath);
            if (!Files.exists(configPath)) {
                plugin.saveResource(filePath, false);
            }
            files.put(filePath, configPath.toFile());
        }
        return files;
    }

    /**
     * Creates the new files (if not exists)
     * with the default contents of the same files in the resources folder
     *
     * @param plugin    The plugin to load the files from
     * @param dir       The directory to load the files from
     * @param filePaths The paths of the files to load
     * @return The created files with filled in default values
     * @see #loadFile(JavaPlugin, String...)
     */
    public static @NotNull Map<String, File> loadFile(@NotNull JavaPlugin plugin, @NotNull String dir, @NotNull String[] filePaths) {
        String[] paths = Arrays.stream(filePaths)
                .map(path -> dir + File.separator + path)
                .toArray(String[]::new);
        return loadFile(plugin, paths);
    }

    /**
     * Creates the new files (if not exists)
     * with the default contents of the same files in the resources folder
     *
     * @param plugin The plugin to load the files from
     * @param files  The files to load
     * @return The created files with filled in default values
     * @see #loadFile(JavaPlugin, String...)
     */
    public static @NotNull Map<String, File> loadFile(@NotNull JavaPlugin plugin, @NotNull File... files) {
        Map<String, File> fileMap = new HashMap<>();
        Path filePath = plugin.getDataFolder().toPath();
        for (File file : files) {
            Path relativePath = filePath.relativize(file.toPath());
            String path = relativePath.toString();
            if (!file.exists())
                plugin.saveResource(path, false);
            fileMap.put(path, file);
        }
        return fileMap;
    }
}
