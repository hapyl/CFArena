package me.hapyl.fight.game.setting;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import me.hapyl.fight.display.Display;
import me.hapyl.fight.enumclass.EnumClass;
import me.hapyl.fight.game.profile.PlayerProfile;
import me.hapyl.fight.util.Compute;
import me.hapyl.spigotutils.module.chat.Chat;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;
import java.util.Map;

public final class Settings extends EnumClass<Setting<?>> {

    public static final Setting<EnumBool> SPECTATE = new Setting<>(
            "SPECTATE",
            Display.of("Spectate")
                    .with("Whenever you will spectate instead of playing the game.")
                    .with(Material.ENDER_EYE),
            Category.GAMEPLAY,
            EnumBool.class,
            EnumBool.DISABLED
    );

    public static final Setting<EnumBool> RANDOM_HERO = new Setting<>(
            "RANDOM_HERO",
            Display.of("Always a Random Hero")
                    .with("Whenever you start the game with a random hero every time.")
                    .with(Material.TOTEM_OF_UNDYING),
            Category.GAMEPLAY,
            EnumBool.class,
            EnumBool.DISABLED
    );

    public static final Setting<EnumBool> CHAT_PING = new Setting<>(
            "CHAT_PING",
            Display.of("Chat Notification")
                    .with("Whenever you will hear a ping in chat if someone mentions you.")
                    .with(Material.GOLD_INGOT),
            Category.CHAT,
            EnumBool.class
    );

    public static final Setting<EnumBool> SEE_NOTIFICATIONS = new Setting<>(
            "SEE_NOTIFICATIONS",
            Display.of("See Notifications")
                    .with("Whenever you will see notifications.")
                    .with(Material.PAPER),
            Category.CHAT,
            EnumBool.class,
            EnumBool.ENABLED
    );

    public static final Setting<EnumBool> SHOW_YOURSELF_AS_TEAMMATE = new Setting<>(
            "SHOW_YOURSELF_AS_TEAMMATE",
            Display.of("Show Yourself as a Teammate")
                    .with("Whenever you will see yourself as a teammate in a tab list.")
                    .with(Material.PLAYER_HEAD),
            Category.CHAT,
            EnumBool.class,
            EnumBool.DISABLED
    );

    public static final Setting<EnumBool> HIDE_UI = new Setting<>(
            "HIDE_UI",
            Display.of("Hide Game UI")
                    .with("Whenever to hide most of the game UI elements, such as actionbar, scoreboard, damage indicators, etc.")
                    .with(Material.GLASS_PANE),
            Category.CHAT,
            EnumBool.class,
            EnumBool.DISABLED
    );

    public static final Setting<EnumBool> SHOW_DAMAGE_IN_CHAT = new Setting<>(
            "SHOW_DAMAGE_IN_CHAT",
            Display.of("Show Damage in Chat")
                    .with("""
                            Whenever you'll see damage dealt and taken messages in chat.

                            &9Nerds special!
                            """)
                    .with(Material.SWEET_BERRIES),
            Category.CHAT,
            EnumBool.class,
            EnumBool.DISABLED
    );

    public static final Setting<EnumBool> SEE_OTHERS_CONTRAIL = new Setting<>(
            "SEE_OTHERS_CONTRAIL",
            Display.of("See Others Contrail")
                    .with("Whenever you will see other players contrails.")
                    .with(Material.FIREWORK_ROCKET),
            Category.OTHER,
            EnumBool.class,
            EnumBool.ENABLED
    );

    public static final Setting<EnumBool> USE_SKINS_INSTEAD_OF_ARMOR = new Setting<>(
            "USE_SKINS_INSTEAD_OF_ARMOR",
            Display.of("Use Hero Skins")
                    .with("Whenever to use hero skins instead of custom head and armor if supported.")
                    .with(Material.LEATHER_CHESTPLATE),
            Category.OTHER,
            EnumBool.class,
            EnumBool.ENABLED
    );

    private static final Map<String, Setting<?>> values = Maps.newLinkedHashMap();
    private static final Map<Category, List<Setting<?>>> byCategory = Maps.newHashMap();

    static int register(Setting<?> e) {
        final int ordinal = values.size();

        values.put(e.name(), e);
        byCategory.compute(e.getCategory(), Compute.listAdd(e));

        return ordinal;
    }

    public void setEnabled(@Nonnull Player player, boolean flag) {
        //if (isEnabled(player) == flag) {
        //    Chat.sendMessage(player, "&cThe setting is already set to that value!");
        //    return;
        //}
        //
        //final PlayerProfile profile = getProfile(player);
        //
        //if (profile == null) {
        //    return;
        //}
        //
        //profile.getDatabase().getSettings().setValue(this, flag);
        //
        //if (flag) {
        //    setting.onEnable(player);
        //}
        //else {
        //    setting.onDisabled(player);
        //}
    }

    @Nonnull
    public static List<Setting<?>> values() {
        return Lists.newArrayList(values.values());
    }

    @Nullable
    public static Setting<?> byName(@Nonnull String name) {
        return values.get(name);
    }

    @Nonnull
    public static List<Setting<?>> byCategory(@Nonnull Category category) {
        return Lists.newArrayList(byCategory.getOrDefault(category, Lists.newArrayList()));
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
