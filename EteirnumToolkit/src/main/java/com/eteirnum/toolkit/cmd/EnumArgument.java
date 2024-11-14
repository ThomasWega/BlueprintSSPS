package com.eteirnum.toolkit.cmd;

import dev.jorel.commandapi.arguments.ArgumentSuggestions;
import dev.jorel.commandapi.arguments.CustomArgument;
import dev.jorel.commandapi.arguments.StringArgument;
import lombok.Getter;
import org.apache.commons.lang.StringUtils;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.concurrent.CompletableFuture;

@Getter
public class EnumArgument<E extends Enum<E>> extends CustomArgument<E, String> {

    private final Class<E> enumClass;

    public EnumArgument(@NotNull Class<E> enumClass) {
        super(new StringArgument(StringUtils.uncapitalize(enumClass.getSimpleName())), info -> {
            try {
                return Enum.valueOf(enumClass, info.input().toUpperCase());
            } catch (IllegalArgumentException e) {
                throw CustomArgumentException.fromString("Invalid value for enum " + enumClass.getSimpleName() + ": " + info.input());
            }
        });

        this.replaceSuggestions(ArgumentSuggestions.stringCollectionAsync(info ->
                CompletableFuture.supplyAsync(() ->
                        Arrays.stream(enumClass.getEnumConstants())
                                .map(Enum::name)
                                .map(String::toLowerCase)
                                .toList()
                )
        ));

        this.enumClass = enumClass;
    }

}
