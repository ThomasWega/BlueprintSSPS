package com.eteirnum.core;

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
    }

    @Override
    public void onDisable() {
        CommandAPI.onDisable();
    }

    private void initializeInstances() {
        this.dataDirPath = getDataFolder() + File.separator + "data";
    }
}
