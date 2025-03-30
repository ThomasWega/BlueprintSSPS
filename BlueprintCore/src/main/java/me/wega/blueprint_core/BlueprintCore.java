package me.wega.blueprint_core;

import me.wega.blueprint_core.config.DefaultFiles;
import me.wega.blueprint_core.config.MaterialGroups;
import me.wega.blueprint_core.player.spell.cmd.AdminSpellCommand;
import me.wega.blueprint_core.tiered.TieredObjectLoader;
import me.wega.blueprint_core.tiered.TieredObjectManager;
import me.wega.blueprint_core.tiered.cmd.AdminTieredObjectCommand;
import me.wega.blueprint_core.tiered.effect.handler.TieredObjectEffectListener;
import me.wega.blueprint_toolkit.shaded.commandapi.CommandAPI;
import me.wega.blueprint_toolkit.shaded.commandapi.CommandAPIBukkitConfig;
import me.wega.blueprint_toolkit.BlueprintToolkit;
import lombok.Getter;
import me.wega.blueprint_toolkit.shaded.particlenativeapi.api.ParticleNativeAPI;
import me.wega.blueprint_toolkit.shaded.particlenativeapi.core.ParticleNativeCore;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;

@Getter
public final class BlueprintCore extends JavaPlugin {
    public static BlueprintCore instance;
    public String dataDirPath;
    private ParticleNativeAPI particleAPI;
    private TieredObjectManager tieredObjectManager;

    @Override
    public void onLoad() {
        instance = this;
        BlueprintToolkit.instance = this;
        CommandAPI.onLoad(new CommandAPIBukkitConfig(this)
                .shouldHookPaperReload(true)
        );
    }

    @Override
    public void onEnable() {
        CommandAPI.onEnable();
        DefaultFiles.saveDefaultFiles();
        this.initializeInstances();
        this.loadData();
        this.registerCommands();
        this.registerListeners();
        this.loadAndCache();
    }

    @Override
    public void onDisable() {
        CommandAPI.onDisable();
        this.saveData();
    }

    // changing the order of these might cause issues (most likely null pointers)
    private void initializeInstances() {
        this.dataDirPath = getDataFolder() + File.separator + "data";
        this.particleAPI = ParticleNativeCore.loadAPI(this);
        this.tieredObjectManager = new TieredObjectManager();
    }

    // data not included here is most likely loaded only when needed
    private void loadData() {
        TieredObjectLoader.loadAllObjectsAsync();
    }

    // data not included here is most likely saved in timed intervals or straight after changes
    private void saveData() {
    }

    private void registerCommands() {
        // ADMIN
        new AdminTieredObjectCommand();
        new AdminSpellCommand();
    }

    private void registerListeners() {
        new TieredObjectEffectListener();
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    private void loadAndCache() {
        MaterialGroups.TRANSPARENT_MATERIALS.size();
        MaterialGroups.PASS_THROUGH_MATERIALS.size();
    }
}
