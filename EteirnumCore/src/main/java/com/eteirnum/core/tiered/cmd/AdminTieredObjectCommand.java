package com.eteirnum.core.tiered.cmd;

import com.eteirnum.core.tiered.TieredObject;
import com.eteirnum.core.tiered.TieredObjectImpl;
import com.eteirnum.core.tiered.TieredObjectInstance;
import com.eteirnum.core.tiered.TieredObjectLoader;
import com.eteirnum.core.tiered.cmd.arg.TieredObjectArgument;
import com.eteirnum.core.tiered.cmd.arg.TieredObjectTierArgument;
import com.eteirnum.shaded.commandapi.CommandAPICommand;
import com.eteirnum.shaded.commandapi.arguments.PlayerArgument;
import com.eteirnum.toolkit.utils.ColorUtils;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class AdminTieredObjectCommand {

    public AdminTieredObjectCommand() {
        for (TieredObjectImpl impl : TieredObjectImpl.values())
            this.register(impl);
    }

    private void register(TieredObjectImpl objectImpl) {
        String implName = objectImpl.getFolderName();
        new CommandAPICommand("admin" + implName)
                .withPermission("eteirnum.admin.cmd.tieredobject." + implName)
                .withSubcommand(new CommandAPICommand("give")
                        .withArguments(
                                new TieredObjectArgument(objectImpl),
                                new TieredObjectTierArgument(),
                                new PlayerArgument("player").setOptional(true)
                        )
                        .executes((sender, args) -> {
                            TieredObject<?> object = args.getUnchecked(0);
                            assert object != null;
                            int tier = args.getUnchecked(1);
                            CommandSender targetSender = (CommandSender) args.getOptionalUnchecked(2).orElse(sender);
                            if (!(targetSender instanceof Player player)) {
                                sender.sendMessage(ColorUtils.color("<red>Only players can receive " + implName + "!"));
                                return;
                            }

                            TieredObjectInstance<?> instance = object.createInstance(tier);
                            assert instance != null;

                            player.getInventory().addItem(instance.getItemStack());
                            player.sendMessage(ColorUtils.color("<green>Successfully given " + implName + " to <white>" + player.getName() + "<green>!"));
                        })
                )
                .withSubcommand(new CommandAPICommand("reload")
                        .executes((sender, args) -> {
                            TieredObjectLoader.reloadObject(objectImpl);
                            sender.sendMessage(ColorUtils.color("<green>Successfully reloaded " + implName + "!"));
                        })
                )
                .register();
    }
}
