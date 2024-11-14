package com.eteirnum.toolkit.file;

import com.eteirnum.toolkit.EteirnumToolkit;
import lombok.experimental.UtilityClass;
import net.minecraft.gametest.framework.GameTest;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Comparator;
import java.util.logging.Level;
import java.util.stream.Stream;

@UtilityClass
public class FileSaver {

    /**
     * Saves the file with a backup
     * This operation is atomic, meaning that the file will be saved only if the backup is created successfully
     *
     * @param file File to save
     * @param backupCount Number of backups to keep
     * @throws IOException if an I/O error occurs
     */
    @GameTest
    public static void saveFileWithBackup(@NotNull File file, int backupCount) throws IOException {
        if (!file.exists()) {
            File parentDir = file.getParentFile();
            if (!parentDir.exists())
                parentDir.mkdirs();
            if (!file.createNewFile()) {
                throw new IOException("Failed to create file: " + file.getAbsolutePath());
            }
        }

        Path filePath = file.toPath();
        Path parentDir = filePath.getParent();
        String baseName = file.getName();

        // Create a temporary backup file
        Path tempBackupPath = Files.createTempFile(parentDir, baseName + "_backup_", ".tmp");

        try {
            // Copy the current file to the temporary backup
            Files.copy(filePath, tempBackupPath, StandardCopyOption.REPLACE_EXISTING);

            // Shift existing backups
            for (int i = backupCount - 1; i > 0; i--) {
                Path oldBackup = parentDir.resolve(baseName + "." + i);
                Path newBackup = parentDir.resolve(baseName + "." + (i + 1));
                if (Files.exists(oldBackup)) {
                    Files.move(oldBackup, newBackup, StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.ATOMIC_MOVE);
                }
            }

            // Move the temporary backup to .1
            Files.move(tempBackupPath, parentDir.resolve(baseName + ".1"), StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.ATOMIC_MOVE);

            // Delete excess backups
            try (Stream<Path> backupFiles = Files.list(parentDir)) {
                backupFiles
                        .filter(p -> p.getFileName().toString().matches(baseName + "\\.\\d+"))
                        .sorted(Comparator.comparing((Path p) -> {
                            String fileName = p.getFileName().toString();
                            return Integer.parseInt(fileName.substring(fileName.lastIndexOf('.') + 1));
                        }).reversed())
                        .skip(backupCount)
                        .forEach(p -> {
                            try {
                                Files.delete(p);
                            } catch (IOException e) {
                                // Log the error but continue with other deletions
                                EteirnumToolkit.instance.getLogger().log(Level.SEVERE, "Failed to delete backup file: " + p, e);
                            }
                        });
            }
        } catch (IOException e) {
            // If any error occurs, attempt to clean up the temporary file
            try {
                Files.deleteIfExists(tempBackupPath);
            } catch (IOException deleteException) {
                e.addSuppressed(deleteException);
            }
            throw new IOException("Failed to create backup for file: " + file.getAbsolutePath(), e);
        }
    }
}