package me.wega.blueprint_toolkit.cmd;

import me.wega.blueprint_toolkit.utils.WGUtils;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import dev.jorel.commandapi.arguments.ArgumentSuggestions;
import dev.jorel.commandapi.arguments.CustomArgument;
import dev.jorel.commandapi.arguments.StringArgument;
import lombok.Getter;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;

@Getter
public class WGRegionArgument extends CustomArgument<ProtectedRegion, String> {

    public WGRegionArgument() {
        super(new StringArgument("regionId"), info -> {
            Optional<ProtectedRegion> region = WGUtils.getRegion(info.input());
            if (region.isEmpty())
                throw CustomArgumentException.fromString("No region with id: " + info.input() + " found");
            return region.get();
        });

        this.replaceSuggestions(ArgumentSuggestions.stringCollectionAsync(info ->
                CompletableFuture.supplyAsync(WGUtils::getRegionNames)
        ));
    }
}
