package me.wega.blueprint_core.tiered.cmd.arg;

import me.wega.blueprint_core.tiered.TieredObject;
import me.wega.blueprint_toolkit.shaded.commandapi.arguments.ArgumentSuggestions;
import me.wega.blueprint_toolkit.shaded.commandapi.arguments.CustomArgument;
import me.wega.blueprint_toolkit.shaded.commandapi.arguments.StringArgument;
import lombok.Getter;

import java.util.concurrent.CompletableFuture;
import java.util.stream.IntStream;

@Getter
public class TieredObjectTierArgument extends CustomArgument<Integer, String> {

    public TieredObjectTierArgument() {
        super(new StringArgument("tier"), info -> {
            try {
                TieredObject<?> object = info.previousArgs().getUnchecked(info.previousArgs().count() - 1);
                int i = (Integer.parseInt(info.currentInput()) - 1);
                if (i < 0 || i >= object.getTiers().length)
                    throw CustomArgumentException.fromString("Invalid tier! Max tier is " + object.getTiers().length + "!");
                return i;
            } catch (NumberFormatException e) {
                throw CustomArgumentException.fromString("Tier must be a number");
            }
        });

        this.replaceSuggestions(ArgumentSuggestions.stringCollectionAsync(info -> {
            TieredObject<?> object = info.previousArgs().getUnchecked(info.previousArgs().count() - 1);
            if (object == null) return CompletableFuture.completedFuture(null);

            return CompletableFuture.supplyAsync(() ->
                    IntStream.rangeClosed(1, object.getTiers().length)
                            .mapToObj(String::valueOf)
                            .toList()
            );
        }));
    }

}
