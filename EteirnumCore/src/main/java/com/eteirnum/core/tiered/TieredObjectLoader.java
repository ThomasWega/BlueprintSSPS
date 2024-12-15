package com.eteirnum.core.tiered;

import com.eteirnum.core.json.GsonHandler;
import com.eteirnum.toolkit.utils.SchedulerUtils;
import com.google.gson.stream.JsonReader;
import lombok.SneakyThrows;
import lombok.experimental.UtilityClass;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.FileReader;
import java.util.Arrays;
import java.util.Objects;
import java.util.logging.Logger;

import static com.eteirnum.core.EteirnumCore.instance;

/**
 * A loader for tiered objects.
 */
@UtilityClass
public class TieredObjectLoader {
    public static final @NotNull String DIR_PATH = instance.getDataFolder() + File.separator + "tiered" + File.separator;
    private static final Logger LOGGER = instance.getLogger();
    private static final TieredObjectManager OBJECT_MANAGER = instance.getTieredObjectManager();

    public static void reloadAllObjects() {
        OBJECT_MANAGER.clear();
        loadAllObjectsAsync();
    }

    public static void reloadObject(@NotNull TieredObjectImpl impl) {
        OBJECT_MANAGER.remove(impl);
        loadObject(impl);
    }

    public static void loadAllObjectsAsync() {
        SchedulerUtils.runTaskAsync(() -> Arrays.stream(TieredObjectImpl.values()).forEach(TieredObjectLoader::loadObject));
    }

    private static void loadObject(@NotNull TieredObjectImpl impl) {
        Arrays.stream(getTypeFiles())
                .filter(folder -> folder.getName().startsWith(impl.getFolderName()))
                .forEach(TieredObjectLoader::loadImpl);
    }

    private static @NotNull File[] getTypeFiles() {
        File dir = new File(DIR_PATH);
        if (!dir.exists() && !dir.mkdirs())
            throw new RuntimeException("Failed to create directory: " + dir);
        @Nullable File[] files = dir.listFiles();
        return files == null ? new File[0] : files;
    }

    private static void loadImpl(File implFolder) {
        TieredObjectImpl objectImpl = TieredObjectImpl.valueOf(implFolder.getName().toUpperCase());
        processFolder(implFolder, objectImpl);
    }

    /**
     * Recursively process subdirectories (unpack them)
     * @param folder The folder/file to process
     * @param objectImpl The object implementation
     */
    private static void processFolder(File folder, TieredObjectImpl objectImpl) {
        File[] files = Objects.requireNonNull(folder.listFiles());
        for (File file : files) {
            if (file.isDirectory()) {
                processFolder(file, objectImpl);
            } else if (file.getName().endsWith(".json")) {
                loadObject(objectImpl, file);
            }
        }
    }

    @SneakyThrows
    private static void loadObject(TieredObjectImpl objectImpl, File file) {
        LOGGER.info("Loading tiered object " + objectImpl.name() + ": " + file.getName() + "...");
        try (JsonReader reader = new JsonReader(new FileReader(file))) {
            TieredObject<?> tieredObject = GsonHandler.GSON.fromJson(reader, TieredObject.class);
            if (tieredObject != null)
                OBJECT_MANAGER.add(objectImpl, tieredObject);
        }
    }
}
