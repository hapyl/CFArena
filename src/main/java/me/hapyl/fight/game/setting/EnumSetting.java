package me.hapyl.fight.game.setting;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import me.hapyl.eterna.module.chat.Chat;
import me.hapyl.eterna.module.inventory.ItemBuilder;
import me.hapyl.eterna.module.registry.KeyedEnum;
import me.hapyl.fight.CF;
import me.hapyl.fight.game.color.Color;
import me.hapyl.fight.game.profile.PlayerProfile;
import me.hapyl.fight.util.PlayerItemCreator;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.Map;

public enum EnumSetting implements Setting, PlayerItemCreator, KeyedEnum {

    //////////////
    // Gameplay //
    //////////////
    SPECTATE(
            Material.ENDER_EYE,
            "Spectate", """
            Whether you'll spectate the game instead of playing it.
            """,
            Category.GAMEPLAY
    ),

    PLAYER_PINGS(
            Material.ARROW,
            "Player Pings", """
            Whether player pinging feature is enabled.
            
            Player pings allow for easier communication with a team.
            
            To ping something, press &e&lDROP&7.
            """,
            Category.GAMEPLAY,
            true
    ),

    SHOW_HEALTH_AND_SHIELD_SEPARATELY(
            Material.SHIELD,
            "Show Health and Shield Separately", """
            Whether to show health and shield amount separately, rather than combined.
            
            &aIf enabled:
            &c&c50 &c❤ &e&l50 &e🛡
            
            &cIf disabled:
            &e&l100 &e🛡
            """,
            Category.GAMEPLAY,
            true
    ),

    //////////
    // Chat //
    //////////
    CHAT_PING(
            Material.GOLD_INGOT,
            "Chat Notification", """
            Whether you will hear a ping in chat when someone @mentions you.
            """,
            Category.CHAT,
            true
    ),

    SEE_NOTIFICATIONS(
            Material.PAPER,
            "See Notifications", """
            Whether you will see notifications.
            """,
            Category.CHAT,
            true
    ),

    SHOW_DAMAGE_IN_CHAT(
            Material.SWEET_BERRIES,
            "Show Damage in Chat", """
            Whether you'll see damage dealt and taken messages in chat.
            
            &9&oNerds special!
            """,
            Category.CHAT
    ),

    SHOW_COOLDOWN_MESSAGE(
            Material.CLOCK,
            "Show Cooldown Messages & Sound", """
            Whether you'll see and hear ability cooldown messages.
            """,
            Category.CHAT,
            true
    ),

    SEE_HERO_RATING_MESSAGE(
            Material.FILLED_MAP,
            "Don't Show Hero Rating", """
            Whether you'll get a message asking to rate a hero when you haven't yet.
            """,
            Category.CHAT,
            true
    ),

    ////////
    // UI //
    ////////
    HIDE_UI(
            Material.GLASS_PANE,
            "Hide Game UI", """
            Whether to hide most of the game UI elements, such as actionbar, scoreboard, damage indicators, etc.
            """,
            Category.UI
    ) {
        @Override
        public void onEnable(@Nonnull Player player) {
            super.onEnable(player);

            final PlayerProfile profile = CF.getProfile(player);
            profile.getPlayerUI().hideScoreboard();
        }

        @Override
        public void onDisabled(@Nonnull Player player) {
            super.onDisabled(player);

            final PlayerProfile profile = CF.getProfile(player);
            profile.getPlayerUI().showScoreboard();
        }
    },

    SEE_DEBUG_DATA(
            Material.CHAIN_COMMAND_BLOCK,
            "See Debug Data", """
            Whether you'll see debug data in your inventory.
            """,
            Category.UI,
            false
    ),

    ACCELERATE_DIALOG(
            Material.FILLED_MAP,
            "Accelerate Dialog", """
            Whether the dialog messages should be accelerated.
            """,
            Category.UI,
            false
    ),

    ///////////
    // Other //
    ///////////
    SEE_OTHERS_CONTRAIL(
            Material.FIREWORK_ROCKET,
            "See Others Contrail", """
            Whether you'll see other players' contrails.
            """,
            Category.OTHER,
            true
    ),

    USE_SKINS_INSTEAD_OF_ARMOR(
            Material.LEATHER_CHESTPLATE,
            "Use Hero Skins", """
            Whether your skin will be changed instead of equipping colored armor when supported.
            """,
            Category.OTHER,
            true
    ),

    ;

    private static final Map<Category, List<EnumSetting>> BY_CATEGORY = Maps.newHashMap();

    static {
        for (EnumSetting setting : values()) {
            final List<EnumSetting> list = BY_CATEGORY.computeIfAbsent(setting.getCategory(), fn -> Lists.newArrayList());

            list.add(setting);
        }
    }

    private final Material material;
    private final String name;
    private final String smallCaps;
    private final String description;
    private final Category category;
    private final boolean defaultValue;

    EnumSetting(@Nonnull Material material, @Nonnull String name, @Nonnull String description, @Nonnull Category category) {
        this(material, name, description, category, false);
    }

    EnumSetting(@Nonnull Material material, @Nonnull String name, @Nonnull String description, @Nonnull Category category, boolean defaultValue) {
        this.material = material;
        this.name = name;
        this.smallCaps = toSmallCaps(name);
        this.description = description;
        this.category = category;
        this.defaultValue = defaultValue;
    }

    @Nonnull
    @Override
    public ItemBuilder create(@Nonnull Player player) {
        final boolean isEnabled = isEnabled(player);

        final ItemBuilder builder = new ItemBuilder(getMaterial())
                .setName((isEnabled ? Color.SUCCESS : Color.ERROR) + getName())
                .addLore("&8%s Setting".formatted(getCategory().getName()))
                .addLore()
                .addTextBlockLore(getDescription(), "&7&o")
                .addLore();

        if (isEnabled) {
            builder.addLore("&a&lCURRENTLY ENABLED!");
            builder.addLore(Color.BUTTON + "Click to disable!");
        }
        else {
            builder.addLore("&c&lCURRENTLY DISABLED!");
            builder.addLore(Color.BUTTON + "Click to enable!");
        }

        return builder.predicate(isEnabled, ItemBuilder::glow);
    }

    public void setEnabled(@Nonnull Player player, boolean flag) {
        if (isEnabled(player) == flag) {
            Chat.sendMessage(player, "&cThe setting is already set to that value!");
            return;
        }

        CF.getDatabase(player).settingEntry.setValue(this, flag);

        if (flag) {
            onEnable(player);
        }
        else {
            onDisabled(player);
        }
    }

    public boolean isEnabled(Player player) {
        return CF.getDatabase(player).settingEntry.getValue(this);
    }

    public boolean isDisabled(Player player) {
        return !isEnabled(player);
    }

    @Nonnull
    @Override
    public Material getMaterial() {
        return material;
    }

    @Nonnull
    @Override
    public Category getCategory() {
        return category;
    }

    @Override
    public boolean getDefaultValue() {
        return defaultValue;
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
    public String getNameSmallCaps() {
        return smallCaps;
    }

    @Nonnull
    public static List<EnumSetting> byCategory(@Nonnull Category category) {
        return Lists.newArrayList(BY_CATEGORY.getOrDefault(category, Lists.newArrayList()));
    }
}
