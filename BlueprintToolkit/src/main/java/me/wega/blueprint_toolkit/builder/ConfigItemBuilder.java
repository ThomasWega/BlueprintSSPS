package me.wega.blueprint_toolkit.builder;

import me.wega.blueprint_toolkit.utils.ColorUtils;
import me.wega.blueprint_toolkit.utils.JSONUtils;
import com.google.gson.JsonObject;
import lombok.Getter;
import lombok.Setter;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Getter
@Setter
public class ConfigItemBuilder {
    private final @Nullable JsonObject object;
    private final @Nullable ConfigurationSection section;
    private @Nullable String material;
    private @Nullable String display;
    private @NotNull List<String> lore = new ArrayList<>();
    private int customModel;
    private @Nullable String base64;
    private @NotNull TagResolver @NotNull [] tagResolvers;

    public ConfigItemBuilder(@NotNull ConfigurationSection section) {
        this(section, new TagResolver[0]);
    }

    public ConfigItemBuilder(@NotNull JsonObject object) {
        this(object, new TagResolver[0]);
    }

    public ConfigItemBuilder(@NotNull ConfigurationSection section, @NotNull TagResolver @NotNull [] tagResolvers) {
        this.section = section;
        this.object = null;
        this.tagResolvers = tagResolvers;
        this.initialize();
    }

    public ConfigItemBuilder(@NotNull JsonObject object, @NotNull TagResolver @NotNull [] tagResolvers) {
        this.object = object;
        this.section = null;
        this.tagResolvers = tagResolvers;
        this.initialize();
    }

    private void initialize() {
        if (section != null) {
            material = section.getString("material");
            display = section.getString("display");
            lore = section.getStringList("lore");
            customModel = section.getInt("customModel");
            base64 = section.getString("base64");
        } else if (object != null) {
            material = JSONUtils.getStringOrDefault(object, "material", null);
            display = JSONUtils.getStringOrDefault(object, "display", null);
            lore = JSONUtils.jsonArrayToList(object.getAsJsonArray("lore"));
            customModel = JSONUtils.getIntOrDefault(object, "customModel", 0);
            base64 = JSONUtils.getStringOrDefault(object, "base64", null);
        }
    }

    public ItemBuilder builder() {
        ItemBuilder builder;
        if (base64 != null)
            builder = new ItemBuilder(SkullCreator.itemFromBase64(base64));
        else {
            Objects.requireNonNull(material, "Material is required component of item");
            builder = new ItemBuilder(Material.getMaterial(material.toUpperCase()));
        }

        if (display != null)
            builder.displayName(ColorUtils.color(display, tagResolvers));

        builder.lore(ColorUtils.color(lore, tagResolvers));

        if (customModel > 0)
            builder.customModel(customModel);

        return builder;
    }

    public ItemStack build() {
        return builder().build();
    }
}