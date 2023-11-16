package me.hapyl.fight.game.setting;

import me.hapyl.fight.util.Described;
import me.hapyl.fight.util.PlayerItemCreator;
import me.hapyl.spigotutils.module.inventory.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import javax.annotation.Nonnull;

public enum Category implements Described, PlayerItemCreator {

    GAMEPLAY(Material.NETHER_STAR, "Gameplay", "Gameplay related settings."),
    CHAT(Material.OAK_HANGING_SIGN, "UI & Chat", "User Interface and Chat-related settings."),
    OTHER(Material.DEAD_BUSH, "Other", "Other uncategorized settings."),

    ;

    private final Material material;
    private final String name;
    private final String description;

    Category(Material material, String name, String description) {
        this.material = material;
        this.name = name;
        this.description = description;
    }

    @Nonnull
    @Override
    public String getName() {
        return name;
    }

    @Nonnull
    @Override
    public String getDescription() {
        return description;
    }

    @Nonnull
    @Override
    public ItemBuilder create(@Nonnull Player player) {
        return new ItemBuilder(material).setName(name).addLore().addTextBlockLore(description);
    }
}
