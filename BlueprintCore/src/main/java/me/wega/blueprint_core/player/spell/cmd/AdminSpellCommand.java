package me.wega.blueprint_core.player.spell.cmd;

import me.wega.blueprint_core.tiered.TieredObjectImpl;
import me.wega.blueprint_core.tiered.cmd.arg.TieredObjectArgument;
import me.wega.blueprint_core.tiered.cmd.arg.TieredObjectTierArgument;
import me.wega.blueprint_core.tiered.impl.spell.TieredSpell;
import me.wega.blueprint_core.tiered.impl.spell.TieredSpellInstance;
import me.wega.blueprint_toolkit.shaded.commandapi.CommandAPICommand;

public class AdminSpellCommand {

    public AdminSpellCommand() {
        this.register();
    }

    private void register() {
        new CommandAPICommand("adminspell")
                .withPermission("etheirum.admin.cmd.spell")
                .withSubcommand(new CommandAPICommand("test")
                        .withArguments(
                                new TieredObjectArgument(TieredObjectImpl.SPELL),
                                new TieredObjectTierArgument()
                        )
                        .executesPlayer((sender, args) -> {
                            TieredSpell spell = args.getUnchecked(0);
                            assert spell != null;
                            int tier = args.getUnchecked(1);

                            TieredSpellInstance spellInstance = spell.createInstance(tier);
                            assert spellInstance != null;

                            spellInstance.use(sender);
                        })
                )
                .register();
    }
}
