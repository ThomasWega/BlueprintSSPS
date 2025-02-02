package me.wega.blueprint_core.tiered.cmd.arg;

import me.wega.blueprint_core.BlueprintCore;
import me.wega.blueprint_core.tiered.TieredObject;
import me.wega.blueprint_core.tiered.TieredObjectImpl;
import me.wega.blueprint_core.tiered.TieredObjectManager;
import me.wega.blueprint_toolkit.shaded.commandapi.arguments.ArgumentSuggestions;
import me.wega.blueprint_toolkit.shaded.commandapi.arguments.CustomArgument;
import me.wega.blueprint_toolkit.shaded.commandapi.arguments.StringArgument;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.concurrent.CompletableFuture;

public class TieredObjectArgument extends CustomArgument<TieredObject<?>, String> {
    private static final TieredObjectManager MANAGER = BlueprintCore.instance.getTieredObjectManager();

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
