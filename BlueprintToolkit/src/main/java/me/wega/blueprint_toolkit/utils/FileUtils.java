package me.wega.blueprint_toolkit.utils;

import me.wega.blueprint_toolkit.BlueprintToolkit;
import lombok.experimental.UtilityClass;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.logging.Level;

@UtilityClass
public class FileUtils {

    /**
     * Creates a backup file of the given file
     *
     * @param file File to create a backup of
     */
    public static void createBackupFile(@NotNull File file) {
        if (!file.getParentFile().exists())
            file.getParentFile().mkdirs();

        // Create backup of old file
        File backupFile = new File(file.getAbsolutePath() + ".bak");
        if (file.exists()) {
            try {
                Files.copy(file.toPath(), backupFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
            } catch (IOException e) {
                BlueprintToolkit.instance.getLogger().log(Level.SEVERE, "Failed to create backup file of " + file.getPath(), e);
            }
        }
    }
}
