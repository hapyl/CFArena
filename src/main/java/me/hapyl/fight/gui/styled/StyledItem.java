package me.hapyl.fight.gui.styled;

import me.hapyl.fight.game.color.Color;
import me.hapyl.spigotutils.module.inventory.ItemBuilder;
import org.bukkit.Material;

import javax.annotation.Nonnull;

public class StyledItem implements StyledBuilder {

    public static final StyledItem ICON_COSMETICS = new StyledItem(
            Material.CHEST,
            "Cosmetics",
            "Add extra uniqueness to your kills, deaths and more!"
    );

    public static final StyledItem ICON_LEVELLING = new StyledItem(
            "87d885b32b0dd2d6b7f1b582a34186f8a5373c46589a273423132b448b803462",
            "Leveling",
            "Earn experience by playing the game to unlock &bheroes&7 and &6unique &7rewards!"
    );

    public static final StyledItem ICON_ACHIEVEMENTS = new StyledItem(
            Material.DIAMOND,
            "Achievements",
            "Complete achievements!"
    );

    public static final StyledItem ICON_ACHIEVEMENTS_GENERAL = new StyledItem(
            "dbfc7fa4e74b6e401ae064148ae5e768b893f1d9297f87475101f7225a2a5a6a",
            "General Achievements",
            "Complete these achievements to earn a one-time reward."
    );

    public static final StyledItem ICON_ACHIEVEMENTS_TIERED = new StyledItem(
            Material.DIAMOND_BLOCK,
            "Tiered Achievements",
            "These achievements can be completed multiple times!"
    );

    public static final StyledItem ICON_SETTINGS = new StyledItem(
            Material.COMPARATOR,
            "Settings",
            "Customize the personal experience as you like it."
    );

    public static final StyledItem ICON_MAP_SELECT = new StyledItem(
            Material.MAP,
            "Selected Map",
            "Select the battleground."
    );

    public static final StyledItem ICON_MODE_SELECT = new StyledItem(
            Material.ITEM_FRAME,
            "Selected Mode",
            "Select the rules."
    );

    public static final StyledItem ICON_TEAM_SELECT = new StyledItem(
            Material.ITEM_FRAME,
            "Selected Mode",
            "Select the rules."
    );

    public static final StyledItem ICON_LOADOUT = new StyledItem(
            Material.LADDER,
            "Customize Hotbar",
            "Customize the hotbar to your liking."
    );

    public static final StyledItem LOCKED_HERO = new StyledItem(
            "46ba63344f49dd1c4f5488e926bf3d9e2b29916a6c50d610bb40a5273dc8c82",
            "&c???",
            "&8Locked!"
    );

    public static final StyledItem ICON_RELIC_REWARDS = new StyledItem(
            "5726d9d0632e40bda5bcf65839ba2cc98a87bd619c53adf00310d6fc71f042b5",
            "Rewards",
            "Trade your relics for unique cosmetic rewards and perks!"
    );

    public static final StyledItem RANDOM_HERO_PREFERENCES = new StyledItem(
            Material.BONE_MEAL,
            "Random Hero Preferences",
            "Tired of manually selecting a hero? Try this!"
    );

    public static final StyledItem ICON_TERMS = new StyledItem(
            Material.BONE_MEAL,
            "Terminology",
            "Get information about in game terms."
    );

    public static final StyledItem ICON_FAIR_MODE = new StyledItem(
            Material.STRING,
            "Fair Mode",
            "To guarantee fair gameplay between players, a &bFair Mode&7 can be used to increase all players &6Mastery Level&7."
    );

    public static final StyledItem ICON_MASTERY = new StyledItem(
            Material.GOLDEN_HELMET,
            "Mastery",
            """
                    &6Master&7 a hero by playing and winning!
                    """
    );

    public final String name;
    public final String description;
    private final Material material;
    protected String texture;

    public StyledItem(Material material, String name, String description) {
        this.material = material;
        this.name = Color.SUCCESS + name;
        this.description = description;
    }

    public StyledItem(String texture, String name, String description) {
        this(Material.PLAYER_HEAD, name, description);

        this.texture = texture;
    }

    @Nonnull
    public Material getMaterial() {
        return material;
    }

    @Nonnull
    public String getName() {
        return name;
    }

    @Nonnull
    public String getDescription() {
        return description;
    }

    @Nonnull
    @Override
    public ItemBuilder toBuilder() {
        ItemBuilder builder;

        if (texture != null) {
            builder = ItemBuilder.playerHeadUrl(texture);
        }
        else {
            builder = ItemBuilder.of(material);
        }

        return builder.setName(name).addLore("").addTextBlockLore(description);
    }

}
