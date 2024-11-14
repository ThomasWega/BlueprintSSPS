package com.eteirnum.toolkit.chat;

import com.eteirnum.toolkit.EteirnumToolkit;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import java.util.function.Predicate;

/**
 * A utility class to use player chat input using callbacks
 *
 * @param <T> Type to parse player input to
 */
public class ChatCallback<T> implements Listener {
    private static final Cache<UUID, ChatCallback<?>> RUNNING_CHAT_CONSUMERS = CacheBuilder.newBuilder()
            .expireAfterWrite(1, TimeUnit.MINUTES)
            .build();
    private final IParser<T> parser;
    private final Player player;
    private final List<Consumer<T>> onSuccess = new ArrayList<>();
    private final List<Consumer<Player>> onCancel = new ArrayList<>();
    private final List<Consumer<String>> onFail = new ArrayList<>();
    private final List<Predicate<T>> onCondition = new ArrayList<>();

    /**
     * Constructs a new ChatCallback instance.
     *
     * @param player        The player associated with this callback.
     * @param parser        The parser used to parse the chat input into a desired type.
     */
    public ChatCallback(@NotNull Player player, @NotNull IParser<T> parser) {
        this.player = player;
        this.parser = parser;
        ChatCallback<?> previousCallback = RUNNING_CHAT_CONSUMERS.getIfPresent(player.getUniqueId());
        if (previousCallback != null) previousCallback.cancel();
    }

    /**
     * Adds a callback to be executed upon successful parsing of the chat input.
     *
     * @param callback The onSuccess callback.
     * @return This ChatCallback instance.
     */
    public @NotNull ChatCallback<T> onSuccess(@NotNull Consumer<T> callback) {
        onSuccess.add(callback);
        return this;
    }

    /**
     * Adds a callback to be executed when the player cancels the chat input.
     *
     * @param callback The onCancel callback.
     * @return This ChatCallback instance.
     */
    public @NotNull ChatCallback<T> onCancel(@NotNull Consumer<Player> callback) {
        onCancel.add(callback);
        return this;
    }

    /**
     * Adds a callback to be executed when the chat input fails to parse.
     *
     * @param callback The onFail callback.
     * @return This ChatCallback instance.
     */
    public @NotNull ChatCallback<T> onFail(@NotNull Consumer<String> callback) {
        onFail.add(callback);
        return this;
    }

    /**
     * Adds a condition predicate to be checked before executing the onSuccess callback.
     *
     * @param condition The onCondition predicate.
     * @return This ChatCallback instance.
     */
    public @NotNull ChatCallback<T> onCondition(@NotNull Predicate<T> condition) {
        onCondition.add(condition);
        return this;
    }

    /**
     * Cancels the chat callback and removes it from the running callbacks map.
     */
    public void cancel() {
        onSuccess.clear();
        onCancel.clear();
        onFail.clear();
        onCondition.clear();
        RUNNING_CHAT_CONSUMERS.invalidate(player.getUniqueId());
    }

    /**
     * The interface for parsing chat input into a desired type.
     *
     * @param <T> The type of the parsed value.
     */
    public interface IParser<T> {
        T parse(String input);
    }

    /**
     * Provides built-in parsers for String, Integer, and Double types.
     */
    public static class Parser {
        /**
         * A parser that returns the input string as is.
         */
        public static final IParser<String> STRING = input -> input;

        /**
         * A parser that parses the input string into an Integer.
         */
        public static final IParser<Integer> INTEGER = input -> {
            try {
                return Integer.valueOf(input);
            } catch (NumberFormatException e) {
                return null;
            }
        };

        /**
         * A parser that parses the input string into a Double.
         */
        public static final IParser<Double> DOUBLE = input -> {
            try {
                return Double.valueOf(input);
            } catch (NumberFormatException e) {
                return null;
            }
        };

        /**
         * A parser that parses the input string into a Float.
         */
        public static final IParser<Float> FLOAT = input -> {
            try {
                return Float.valueOf(input);
            } catch (NumberFormatException e) {
                return null;
            }
        };
    }

    static {
        Bukkit.getServer().getPluginManager().registerEvents(new Listeners(), EteirnumToolkit.instance);
    }

    private static class Listeners implements Listener {

        @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
        private void onPlayerQuit(PlayerQuitEvent event) {
            ChatCallback<?> chatCallback = RUNNING_CHAT_CONSUMERS.getIfPresent(event.getPlayer().getUniqueId());
            if (chatCallback != null) chatCallback.cancel();
        }

        @EventHandler(priority = EventPriority.NORMAL)
        private <T> void onPlayerChat(AsyncPlayerChatEvent event) {
            Player player = event.getPlayer();
            ChatCallback<T> chatCallback = (ChatCallback<T>) RUNNING_CHAT_CONSUMERS.getIfPresent(player.getUniqueId());
            if (chatCallback == null) return;

            event.setCancelled(true);
            String msg = event.getMessage();

            if (!chatCallback.onCancel.isEmpty() && msg.equalsIgnoreCase("cancel")) {
                chatCallback.onCancel.forEach(callback -> callback.accept(player));
                chatCallback.cancel();
                return;
            }

            T parsedValue = chatCallback.parser.parse(msg);
            if (parsedValue == null) {
                chatCallback.onFail.forEach(callback -> callback.accept(msg));
                return;
            }

            if (!chatCallback.onCondition.isEmpty() && chatCallback.onCondition.stream().anyMatch(condition -> !condition.test(parsedValue))) {
                return;
            }

            chatCallback.onSuccess.forEach(callback -> callback.accept(parsedValue));
            chatCallback.cancel();
        }
    }
}