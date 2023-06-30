package me.hapyl.fight.game.setting;

import me.hapyl.fight.game.Manager;
import me.hapyl.fight.game.profile.PlayerProfile;
import me.hapyl.fight.game.ui.GamePlayerUI;
import me.hapyl.spigotutils.module.chat.Chat;
import org.bukkit.Material;
import org.bukkit.entity.Player;

public enum Setting {

    SPECTATE(10, Material.ENDER_EYE, "Spectate", "Whenever you will spectate the game instead of playing it."),
    CHAT_PING(11, Material.GOLD_INGOT, "Chat Notification", "Whenever you will hear a ping in chat if someone mentions you.", true),
    RANDOM_HERO(12, Material.TOTEM_OF_UNDYING, "Always Random Hero", "Whenever you start the game with a random hero every time."),

    SEE_OTHERS_CONTRAIL(14, Material.FIREWORK_ROCKET, "See Others Contrail", "Whenever you will see other players contrails.", true),
    SEE_NOTIFICATIONS(15, Material.PAPER, "See Notifications", "Whenever you will see notifications.", true),
    SHOW_DAMAGE_IN_CHAT(
            16,
            Material.SWEET_BERRIES,
            "Show Damage in Chat",
            "Whenever to show the damage dealt and taken in chat.____&9Nerds special!"
    ),

    SHOW_YOURSELF_AS_TEAMMATE(
            28,
            Material.PLAYER_HEAD,
            "Show Yourself as a Teammate",
            "Whenever you will see yourself as a teammate in a tab list."
    ),

    HIDE_UI(
            29,
            Material.GLASS_PANE,
            "Hide Game UI",
            "Whenever to hide most of the game UI elements, such as actionbar, scoreboard, damage indicators, etc."
    ) {
        @Override
        public void onEnable(Player player) {
            final GamePlayerUI ui = PlayerProfile.getOrCreateProfile(player).getPlayerUI();

            ui.hideScoreboard();
        }

        @Override
        public void onDisabled(Player player) {
            final GamePlayerUI ui = PlayerProfile.getOrCreateProfile(player).getPlayerUI();

            ui.showScoreboard();
        }
    },

    ;

    private final Material material;
    private final String name;
    private final String info;
    private final boolean def;
    private final int slot;

    Setting(int slot, Material material, String name, String info, boolean def) {
        this.slot = slot;
        this.name = name;
        this.material = material;
        this.info = info;
        this.def = def;
    }

    Setting(int slot, Material material, String name, String info) {
        this(slot, material, name, info, false);
    }

    public int getSlot() {
        return slot;
    }

    public Material getMaterial() {
        return material;
    }

    public String getName() {
        return name;
    }

    public boolean getDefaultValue() {
        return def;
    }

    public String getPath() {
        return name();
    }

    public String getInfo() {
        return info;
    }

    public void onEnable(Player player) {
    }

    public void onDisabled(Player player) {
    }

    public final void setEnabled(Player player, boolean flag) {
        if (isEnabled(player) == flag) {
            Chat.sendMessage(player, "&c%s is already %s!", this.getName(), flag ? "enabled" : "disabled");
            return;
        }

        Manager.current().getOrCreateProfile(player).getDatabase().getSettings().setValue(this, flag);
        Chat.sendMessage(player, "%s%s is now %s.", flag ? "&a" : "&c", this.getName(), flag ? "enabled" : "disabled");

        if (flag) {
            onEnable(player);
        }
        else {
            onDisabled(player);
        }
    }

    public boolean isEnabled(Player player) {
        return PlayerProfile.getOrCreateProfile(player).getDatabase().getSettings().getValue(this);
    }

    public boolean isDisabled(Player player) {
        return !isEnabled(player);
    }

    public String getPathLegacy() {
        return "setting." + getPath();
    }
}
