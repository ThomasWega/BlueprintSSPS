package com.eteirnum.core;

import com.eteirnum.core.player.spell.cmd.AdminSpellCommand;
import com.eteirnum.core.tiered.TieredObjectLoader;
import com.eteirnum.core.tiered.TieredObjectManager;
import com.eteirnum.core.tiered.cmd.AdminTieredObjectCommand;
import com.eteirnum.shaded.commandapi.CommandAPI;
import com.eteirnum.shaded.commandapi.CommandAPIBukkitConfig;
import com.eteirnum.toolkit.EteirnumToolkit;
import lombok.Getter;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;

@Getter
public final class EteirnumCore extends JavaPlugin {
    public static EteirnumCore instance;
    public String dataDirPath;
    private TieredObjectManager tieredObjectManager;

    @Override
    public void onLoad() {
        instance = this;
        EteirnumToolkit.instance = this;
        CommandAPI.onLoad(new CommandAPIBukkitConfig(this)
                .shouldHookPaperReload(true)
        );
    }

    @Override
    public void onEnable() {
        CommandAPI.onEnable();
        this.initializeInstances();
        this.loadData();
        this.registerCommands();
        this.registerListeners();
    }

    @Override
    public void onDisable() {
        CommandAPI.onDisable();
        this.saveData();
    }

    // changing the order of these might cause issues (most likely null pointers)
    private void initializeInstances() {
        this.dataDirPath = getDataFolder() + File.separator + "data";
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

    }
}
