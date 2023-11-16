package me.hapyl.fight.game.setting;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import me.hapyl.fight.game.color.Color;
import me.hapyl.fight.game.profile.PlayerProfile;
import me.hapyl.fight.util.EnumWrapper;
import me.hapyl.fight.util.PlayerItemCreator;
import me.hapyl.spigotutils.module.chat.Chat;
import me.hapyl.spigotutils.module.inventory.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;
import java.util.Map;

public enum Settings implements EnumWrapper<Setting>, PlayerItemCreator {

    // Gameplay
    SPECTATE(new Setting(
            Material.ENDER_EYE,
            "Spectate",
            "Whenever you will spectate the game instead of playing it.",
            Category.GAMEPLAY
    )),

    RANDOM_HERO(new Setting(
            Material.TOTEM_OF_UNDYING,
            "Random Hero",
            "Whenever you start the game with a random hero every time.",
            Category.GAMEPLAY
    )),

    // Chat
    CHAT_PING(new Setting(
            Material.GOLD_INGOT,
            "Chat Notification",
            "Whenever you will hear a ping in chat if someone mentions you.",
            Category.CHAT,
            true
    )),

    SEE_NOTIFICATIONS(new Setting(
            Material.PAPER,
            "See Notifications",
            "Whenever you will see notifications.",
            Category.CHAT,
            true
    )),

    SHOW_YOURSELF_AS_TEAMMATE(new Setting(
            Material.PLAYER_HEAD,
            "Show Yourself as a Teammate",
            "Whenever you will see yourself as a teammate in a tab list.",
            Category.CHAT
    )),

    HIDE_UI(new Setting(
            Material.GLASS_PANE,
            "Hide Game UI",
            "Whenever to hide most of the game UI elements, such as actionbar, scoreboard, damage indicators, etc.",
            Category.CHAT
    ) {
        @Override
        public void onEnable(@Nonnull Player player) {
            super.onEnable(player);

            final PlayerProfile profile = PlayerProfile.getProfile(player);

            if (profile == null) {
                Chat.sendMessage(player, "&cThere was an error loading your profile! Try rejoining the server before reporting this!");
                return;
            }

            profile.getPlayerUI().hideScoreboard();
        }

        @Override
        public void onDisabled(@Nonnull Player player) {
            super.onDisabled(player);

            final PlayerProfile profile = getProfile(player);

            if (profile == null) {
                Chat.sendMessage(player, "&cThere was an issue loading your profile! Try rejoining the server before reporting this!");
                return;
            }

            profile.getPlayerUI().showScoreboard();
        }
    }),

    SHOW_DAMAGE_IN_CHAT(new Setting(
            Material.SWEET_BERRIES,
            "Show Damage in Chat", """
            Whenever you'll see damage dealt and taken messages in chat.
                                
            &9Nerds special!
            """,
            Category.CHAT
    )),

    // Other
    SEE_OTHERS_CONTRAIL(new Setting(
            Material.FIREWORK_ROCKET,
            "See Others Contrail",
            "Whenever you will see other players contrails.",
            Category.OTHER,
            true
    )),

    USE_SKINS_INSTEAD_OF_ARMOR(new Setting(
            Material.LEATHER_CHESTPLATE,
            "Use Hero Skins",
            "Whenever to use hero skins instead of custom head and armor if supported.",
            Category.OTHER,
            true
    )),

    ;

    private static final Map<Category, List<Settings>> BY_CATEGORY = Maps.newHashMap();

    static {
        for (Settings setting : values()) {
            final List<Settings> list = BY_CATEGORY.computeIfAbsent(setting.setting.getCategory(), fn -> Lists.newArrayList());

            list.add(setting);
        }
    }

    private final Setting setting;

    Settings(Setting setting) {
        this.setting = setting;
    }

    @Nonnull
    @Override
    public Setting get() {
        return setting;
    }

    @Nonnull
    public Setting getSetting() {
        return setting;
    }

    @Nonnull
    @Override
    public ItemBuilder create(@Nonnull Player player) {
        final boolean isEnabled = isEnabled(player);

        final ItemBuilder builder = new ItemBuilder(setting.getMaterial())
                .setName((isEnabled ? Color.SUCCESS : Color.ERROR) + setting.getName())
                .addLore("&8%s Setting", Chat.capitalize(setting.getCategory()))
                .addLore()
                .addTextBlockLore(setting.getDescription())
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

        final PlayerProfile profile = getProfile(player);

        if (profile == null) {
            return;
        }

        profile.getDatabase().getSettings().setValue(this, flag);

        if (flag) {
            setting.onEnable(player);
        }
        else {
            setting.onDisabled(player);
        }
    }

    public boolean isEnabled(Player player) {
        return PlayerProfile.getOrCreateProfile(player).getDatabase().getSettings().getValue(this);
    }

    public boolean isDisabled(Player player) {
        return !isEnabled(player);
    }

    @Nonnull
    public static List<Settings> byCategory(@Nonnull Category category) {
        return Lists.newArrayList(BY_CATEGORY.getOrDefault(category, Lists.newArrayList()));
    }

    @Nullable
    private static PlayerProfile getProfile(@Nonnull Player player) {
        final PlayerProfile profile = PlayerProfile.getProfile(player);

        if (profile == null) {
            Chat.sendMessage(player, "&cThere was an issue loading your profile! Try rejoining the server before reporting this!");
            return null;
        }

        return profile;
    }
}
