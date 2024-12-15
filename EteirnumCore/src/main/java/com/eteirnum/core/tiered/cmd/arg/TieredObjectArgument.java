package com.eteirnum.core.tiered.cmd.arg;

import com.eteirnum.core.EteirnumCore;
import com.eteirnum.core.tiered.TieredObject;
import com.eteirnum.core.tiered.TieredObjectImpl;
import com.eteirnum.core.tiered.TieredObjectManager;
import com.eteirnum.shaded.commandapi.arguments.ArgumentSuggestions;
import com.eteirnum.shaded.commandapi.arguments.CustomArgument;
import com.eteirnum.shaded.commandapi.arguments.StringArgument;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.concurrent.CompletableFuture;

public class TieredObjectArgument extends CustomArgument<TieredObject<?>, String> {
    private static final TieredObjectManager MANAGER = EteirnumCore.instance.getTieredObjectManager();

    public TieredObjectArgument(@NotNull TieredObjectImpl impl) {
        super(new StringArgument("id"), info -> {
            String id = info.currentInput();
            if (!MANAGER.has(impl, id))
                throw CustomArgumentException.fromString("Invalid object id!");
            return MANAGER.get(impl, id);
        });

        this.replaceSuggestions(ArgumentSuggestions.stringCollectionAsync(info ->
                CompletableFuture.supplyAsync(() ->
                        MANAGER.getOrDefault(impl, new ArrayList<>()).stream()
                                .map(TieredObject::getId)
                                .toList()
                )
        ));
    }
}
