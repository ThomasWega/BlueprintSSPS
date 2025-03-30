package me.wega.blueprint_core.config;

import lombok.experimental.UtilityClass;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;

import static me.wega.blueprint_core.BlueprintCore.instance;

/**
 * Utility class for saving default files.
 */
@UtilityClass
public class DefaultFiles {
    private static final Map<String, Set<String>> DEFAULT_FILES = new HashMap<>();

    static {
        DEFAULT_FILES.put("tiered" + File.separator + "spell", Set.of("fireball.json", "flourish.json", "rush.json"));
    }

    public static void saveDefaultFiles() {
        Path dataFolder = instance.getDataFolder().toPath();
        DEFAULT_FILES.forEach((dir, files) -> {
            Path dirPath = dataFolder.resolve(dir);
            if (Files.notExists(dirPath)) {
                files.forEach(file -> {
                    String filePath = dir + File.separator + file;
                    try {
                        instance.saveResource(dir + File.separator + file, false);
                        instance.getLogger().log(Level.INFO, "Saved default file: " + filePath);
                    } catch (Exception e) {
                        instance.getLogger().log(Level.SEVERE, "Failed to save default file: " + filePath, e);
                    }
                });
            }
        });
    }
}