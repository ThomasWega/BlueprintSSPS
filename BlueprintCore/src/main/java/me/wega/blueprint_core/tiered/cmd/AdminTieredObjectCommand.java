package me.wega.blueprint_core.tiered.cmd;

import me.wega.blueprint_core.tiered.TieredObject;
import me.wega.blueprint_core.tiered.TieredObjectImpl;
import me.wega.blueprint_core.tiered.TieredObjectInstance;
import me.wega.blueprint_core.tiered.TieredObjectLoader;
import me.wega.blueprint_core.tiered.cmd.arg.TieredObjectArgument;
import me.wega.blueprint_core.tiered.cmd.arg.TieredObjectTierArgument;
import me.wega.blueprint_toolkit.shaded.commandapi.CommandAPICommand;
import me.wega.blueprint_toolkit.shaded.commandapi.arguments.PlayerArgument;
import me.wega.blueprint_toolkit.utils.ColorUtils;
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
                .withPermission("blueprint.admin.cmd.tieredobject." + implName)
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
