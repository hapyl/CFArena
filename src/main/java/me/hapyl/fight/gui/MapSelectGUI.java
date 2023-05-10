package me.hapyl.fight.gui;

import me.hapyl.fight.game.Manager;
import me.hapyl.fight.game.maps.GameMap;
import me.hapyl.fight.game.maps.GameMaps;
import me.hapyl.fight.game.maps.MapFeature;
import me.hapyl.spigotutils.module.chat.Chat;
import me.hapyl.spigotutils.module.inventory.ItemBuilder;
import me.hapyl.spigotutils.module.inventory.gui.GUI;
import me.hapyl.spigotutils.module.inventory.gui.PlayerAutoGUI;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Locale;

public class MapSelectGUI extends PlayerAutoGUI {

    public MapSelectGUI(Player player) {
        super(player, "Map Selection", Math.min(GUI.getSmartMenuSize(GameMaps.getPlayableMaps()) + 2, 6));
        this.createItems();
    }

    private void createItems() {
        for (final GameMaps value : GameMaps.getPlayableMaps()) {
            final GameMap map = value.getMap();

            final ItemBuilder builder = new ItemBuilder(map.getMaterial())
                    .setName("&a" + map.getName())
                    .addLore("&8/map " + value.name().toLowerCase(Locale.ROOT), " &7&o")
                    .addLore("")
                    .addSmartLore(map.getDescription());

            if (!map.getFeatures().isEmpty()) {
                builder.addLore().addLore("&aMap Features:").addLore();
                for (final MapFeature feature : map.getFeatures()) {
                    builder.addLore(" &b" + feature.getName());
                    builder.addSmartLore(feature.getInfo(), "  &7&o");
                }
            }

            final ItemStack item = builder.addLore("").addLore("&eClick to select").build();
            addItem(item, player -> {
                final GameMaps currentMap = Manager.current().getCurrentMap();
                if (currentMap == value) {
                    Chat.sendMessage(player, "&cAlready selected!");
                    return;
                }

                Manager.current().setCurrentMap(value, player);
            });
        }

        openInventory();
    }

}
