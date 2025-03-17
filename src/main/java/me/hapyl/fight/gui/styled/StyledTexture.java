package me.hapyl.fight.gui.styled;

import me.hapyl.eterna.module.inventory.ItemBuilder;
import me.hapyl.fight.game.collectible.relic.Type;
import me.hapyl.fight.game.color.Color;
import org.bukkit.Material;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class StyledTexture implements StyledBuilder {

    public static final StyledTexture ICON_COSMETICS = new StyledTexture(
            Material.CHEST,
            "Cosmetics",
            "Add extra uniqueness to your kills, deaths and more!"
    );

    public static final StyledTexture ICON_LEVELLING = new StyledTexture(
            "87d885b32b0dd2d6b7f1b582a34186f8a5373c46589a273423132b448b803462",
            "Leveling",
            "Earn experience by playing the game to unlock &bheroes&7 and &6unique &7rewards!"
    );

    public static final StyledTexture ICON_ACHIEVEMENTS = new StyledTexture(
            Material.DIAMOND,
            "Achievements",
            "Complete achievements!"
    );

    public static final StyledTexture ICON_ACHIEVEMENTS_GENERAL = new StyledTexture(
            "dbfc7fa4e74b6e401ae064148ae5e768b893f1d9297f87475101f7225a2a5a6a",
            "General Achievements",
            "Complete these achievements to earn a one-time reward."
    );

    public static final StyledTexture ICON_ACHIEVEMENTS_TIERED = new StyledTexture(
            Material.DIAMOND_BLOCK,
            "Tiered Achievements",
            "These achievements can be completed multiple times!"
    );

    public static final StyledTexture ICON_ACHIEVEMENTS_HERO_RELATED = new StyledTexture(
            Material.PLAYER_HEAD,
            "Hero-Specific Achievements",
            "Hero specifc "
    );

    public static final StyledTexture ICON_SETTINGS = new StyledTexture(
            Material.COMPARATOR,
            "Settings",
            "Customize the personal experience as you like it."
    );

    public static final StyledTexture ICON_MAP_SELECT = new StyledTexture(
            Material.MAP,
            "Selected Map",
            "Select the battleground."
    );

    public static final StyledTexture ICON_MODE_SELECT = new StyledTexture(
            Material.ITEM_FRAME,
            "Selected Mode",
            "Select the rules."
    );

    public static final StyledTexture ICON_TEAM_SELECT = new StyledTexture(
            Material.ITEM_FRAME,
            "Selected Mode",
            "Select the rules."
    );

    public static final StyledTexture ICON_LOADOUT = new StyledTexture(
            Material.LADDER,
            "Customize Hotbar",
            "Customize the hotbar to your liking."
    );

    public static final StyledTexture LOCKED_HERO = new StyledTexture(
            "46ba63344f49dd1c4f5488e926bf3d9e2b29916a6c50d610bb40a5273dc8c82",
            "&c???",
            "&8Locked!"
    );

    public static final StyledTexture ICON_RELIC_REWARDS = new StyledTexture(
            "5726d9d0632e40bda5bcf65839ba2cc98a87bd619c53adf00310d6fc71f042b5",
            "Rewards",
            "Trade your relics for unique cosmetic rewards and perks!"
    );

    public static final StyledTexture RANDOM_HERO_PREFERENCES = new StyledTexture(
            Material.BONE_MEAL,
            "Random Hero Preferences",
            "Tired of manually selecting a hero? Try this!"
    );

    public static final StyledTexture ICON_TERMS = new StyledTexture(
            Material.BONE_MEAL,
            "Terminology",
            "Get information about in game terms."
    );

    public static final StyledTexture ICON_FAIR_MODE = new StyledTexture(
            Material.STRING,
            "Fair Mode",
            "To guarantee fair gameplay between players, a &bFair Mode&7 can be used to increase all players &6Mastery Level&7."
    );

    public static final StyledTexture ICON_MASTERY = new StyledTexture(
            Material.GOLDEN_HELMET,
            "Hero Mastery &4&lαʟᴘʜᴀ",
            """
                    Master a hero by &awinning&7 and &cslaying&7 your enemies to unlock &badditional&7 abilities and become the &4&lMaster&7!
                    """
    );

    public static final StyledTexture ICON_STORY = new StyledTexture(
            Material.WRITABLE_BOOK,
            "Story",
            """
                    Learn the story of this hero!
                    """
    );

    public static final StyledTexture ICON_STORE = new StyledTexture(
            "d3e0d25542317d9a7be70d7ef4cb5b2c257b53aec0fc3dfde2885a3a121b0a83",
            "Store",
            """
                    Do purchase my items~
                    """
    );

    public static final StyledTexture ARROW_LEFT = new StyledTexture(
            "b76230a0ac52af11e4bc84009c6890a4029472f3947b4f465b5b5722881aacc7"
    );

    public static final StyledTexture ARROW_RIGHT = new StyledTexture(
            "dbf8b6277cd36266283cb5a9e6943953c783e6ff7d6a2d59d15ad0697e91d43c"
    );

    public static final StyledTexture QUESTION = new StyledTexture(
            "46ba63344f49dd1c4f5488e926bf3d9e2b29916a6c50d610bb40a5273dc8c82",
            "???", ""
    );

    public static final StyledTexture ACHIEVEMENT_TIERED_COMPLETE = new StyledTexture(
            "dbfc7fa4e74b6e401ae064148ae5e768b893f1d9297f87475101f7225a2a5a6a"
    );

    public static final StyledTexture ACHIEVEMENT_TIERED_INCOMPLETE = new StyledTexture(
            "d7f5766d2928dc0df1b3404c3bd073c9476d26c80573b0332e7cce73df15482a"
    );

    public static final StyledTexture CRATE_CONVERT = new StyledTexture(
            "d5c6dc2bbf51c36cfc7714585a6a5683ef2b14d47d8ff714654a893f5da622"
    );

    public static final StyledTexture THE_EYE = new StyledTexture(
            "a88b1cd9574672e8e3262f210c0dddbc082ea7569e8e70f0c07b4bee75e32f62",
            "&dThe Eye",
            "&8Overseer."
    );

    public static final StyledTexture RELIC_HUNT = new StyledTexture(
            Type.AMETHYST.getTexture(),
            "&dRelic Hunt", """
            &7&o;;There are relics scattered all over the world.
            
            &7&o;;Help me see them again, and I shall grant you rewards.
            """
    );

    public static final StyledTexture DAILY = new StyledTexture(
            "86f1c9ecbcd49842dcbe9a3d85abba6479ea46bdd424dbd9d0ef54c28bf502d7",
            "&aDaily Bonds", """
            &7&o;;You and I have bonds, experience them to gain wisdom!
            """
    );

    public static final StyledTexture TOKEN_STORE = new StyledTexture(
            "5d1f382262db6e1a5c095049f99a9946f087f7d782c1ca25d0873ebf567e79be",
            "Token Exchange",
            """
                    &7&o;;Exchange your tokens for powerful buffs!
                    """
    );

    public static final StyledTexture CHEST = new StyledTexture(
            "47ec41e0df8e170d97f9b9af1d65edad4979c78c89b01b180f389ee08a61af82"
    );

    public static final StyledTexture CHEST_EMERALD = new StyledTexture(
            "4ba55671f97ff3bfc5be335ae92cd9749abd619e7afc2a6673597b80b755c741"
    );

    public static final StyledTexture CHEST_DIAMOND = new StyledTexture(
            "31f7cdfea2d21cd5f6ebbf48481761c6cbdf36d00fe64083686e9aeaa3f1f217"
    );

    public static final StyledTexture TNT = new StyledTexture(
            "94f90c7bd60bfd0dfc31808d0484d8c2db9959f68df91fbf29423a3da62429a6"
    );

    public static final StyledTexture ICON_COMMISSIONS = new StyledTexture(
            "a792b6997d739f535beed3ab1d4aeadfa76777bf8e38a666f54f82ff9f858186",
            "Commissions",
            """
                    You accept, you slay, you get paid!
                    """
    );

    @Nullable public final String name;
    @Nullable public final String description;

    private final Material material;

    protected String texture;

    StyledTexture(@Nonnull Material material, @Nullable String name, @Nullable String description) {
        this.material = material;
        this.name = name != null ? Color.SUCCESS + name : "";
        this.description = description;
    }

    StyledTexture(@Nonnull String texture) {
        this(texture, null, null);
    }

    StyledTexture(@Nonnull String texture, @Nullable String name, @Nullable String description) {
        this(Material.PLAYER_HEAD, name, description);

        this.texture = texture;
    }

    @Nonnull
    public Material getMaterial() {
        return material;
    }

    @Nullable
    public String getName() {
        return name;
    }

    @Nullable
    public String getDescription() {
        return description;
    }

    public boolean isPlayerHead() {
        return texture != null && material == Material.PLAYER_HEAD;
    }

    @Nonnull
    public ItemBuilder toBuilderClean() {
        return isPlayerHead() ? ItemBuilder.playerHeadUrl(texture) : ItemBuilder.of(material);
    }

    @Nonnull
    @Override
    public ItemBuilder toBuilder() {
        final ItemBuilder builder = toBuilderClean();

        if (name != null) {
            builder.setName(name);
        }

        if (description != null) {
            builder.addTextBlockLore(description);
        }

        return builder;
    }

}
