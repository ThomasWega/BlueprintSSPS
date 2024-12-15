package com.eteirnum.core.player.spell.cmd;

import com.eteirnum.core.tiered.TieredObjectImpl;
import com.eteirnum.core.tiered.cmd.arg.TieredObjectArgument;
import com.eteirnum.core.tiered.cmd.arg.TieredObjectTierArgument;
import com.eteirnum.core.tiered.impl.spell.TieredSpell;
import com.eteirnum.core.tiered.impl.spell.TieredSpellInstance;
import com.eteirnum.shaded.commandapi.CommandAPICommand;

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
