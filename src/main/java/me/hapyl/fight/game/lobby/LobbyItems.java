package me.hapyl.fight.game.lobby;

import me.hapyl.fight.game.Debugger;
import me.hapyl.fight.gui.HeroSelectGUI;
import me.hapyl.fight.gui.MapSelectGUI;
import me.hapyl.fight.gui.PlayerProfileGUI;
import me.hapyl.fight.gui.SettingsGUI;
import me.hapyl.spigotutils.module.inventory.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.entity.Player;

public enum LobbyItems {

    HERO_SELECT(new LobbyItem(Material.TOTEM_OF_UNDYING, 1, "Hero Selector", "Click to browse and select a hero!") {
        @Override
        public void onClick(Player player) {
            new HeroSelectGUI(player);
        }
    }),

    MAP_SELECT(new LobbyItem(Material.MAP, 2, "Map Selector", "Click to browse and select a map!") {
        @Override
        public void onClick(Player player) {
            new MapSelectGUI(player);
        }
    }),

    PLAYER_PROFILE(new LobbyItem(Material.PLAYER_HEAD, 4, "Profile", "Click to browse your profile!") {
        @Override
        public void onClick(Player player) {
            new PlayerProfileGUI(player);
        }

        @Override
        public void modifyItem(Player player, ItemBuilder builder) {
            builder.setSkullOwner(player.getName());
        }
    }),

    SETTING(new LobbyItem(Material.COMPARATOR, 6, "Settings", "Click to browse and change your settings!") {
        @Override
        public void onClick(Player player) {
            new SettingsGUI(player);
        }
    }),

    START_GAME(new LobbyItem(Material.CLOCK, 7, "Start Vote", "Click to start a vote to start the game!") {
        @Override
        public void onClick(Player player) {
            if (player.isOp()) {
            }
            Debugger.info("Ignoring start vote");
            player.performCommand("cf start");
        }
    }),

    ;

    private final LobbyItem lobbyItem;

    LobbyItems(LobbyItem lobbyItem) {
        this.lobbyItem = lobbyItem;
    }

    public static void giveAll(Player player) {
        for (LobbyItems value : values()) {
            value.give(player);
        }
    }

    public void give(Player player) {
        lobbyItem.give(player);
    }


}
