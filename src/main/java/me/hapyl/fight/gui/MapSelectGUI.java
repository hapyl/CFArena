package me.hapyl.fight.gui;

import me.hapyl.fight.game.Manager;
import me.hapyl.fight.game.maps.GameMap;
import me.hapyl.fight.game.maps.GameMaps;
import me.hapyl.fight.game.maps.MapFeature;
import me.hapyl.fight.util.Nulls;
import me.hapyl.spigotutils.module.chat.Chat;
import me.hapyl.spigotutils.module.inventory.ItemBuilder;
import me.hapyl.spigotutils.module.inventory.gui.GUI;
import me.hapyl.spigotutils.module.inventory.gui.SlotPattern;
import me.hapyl.spigotutils.module.inventory.gui.SmartComponent;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Locale;

public class MapSelectGUI extends GUI {

    private static final MapSelectGUI staticGUI = new MapSelectGUI();

    private MapSelectGUI() {
        super("Map Selection", Math.min(GUI.getSmartMenuSize(GameMaps.getPlayableMaps()) + 2, 6));
        this.createItems();
    }

    private void createItems() {
        final SmartComponent component = newSmartComponent();

        for (final GameMaps value : GameMaps.getPlayableMaps()) {
            final GameMap map = value.getMap();

            final ItemBuilder builder = new ItemBuilder(map.getMaterial())
                    .setName("&a" + map.getName())
                    .addLore("&8/map " + value.name().toLowerCase(Locale.ROOT), " &7&o")
                    .addLore("")
                    .addSmartLore(map.getInfo());

            if (!map.getFeatures().isEmpty()) {
                builder.addLore().addLore("&aMap Features:").addLore();
                for (final MapFeature feature : map.getFeatures()) {
                    builder.addLore(" &b" + feature.getName());
                    builder.addSmartLore(feature.getInfo(), "  &7&o");
                }
            }

            final ItemStack item = builder.addLore("").addLore("&eClick to select").build();
            component.add(item, player -> {
                final GameMaps currentMap = Manager.current().getCurrentMap();
                if (currentMap == value) {
                    Chat.sendMessage(player, "&cAlready selected!");
                    return;
                }

                Manager.current().setCurrentMap(value, player);
            });
        }

        component.fillItems(this, SlotPattern.CHUNKY);
    }

    public static void openGUI(Player player) {
        Nulls.runIfNotNull(staticGUI, mapSelectGUI -> mapSelectGUI.openInventory(player));
    }

}
