package me.hapyl.fight.game.lobby;

import me.hapyl.fight.game.cosmetic.gui.CollectionGUI;
import me.hapyl.fight.gui.HeroSelectGUI;
import me.hapyl.fight.gui.MapSelectGUI;
import me.hapyl.fight.gui.SettingsGUI;
import me.hapyl.spigotutils.module.inventory.ItemBuilder;
import me.hapyl.spigotutils.module.util.Action;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public enum LobbyItems {

    CLASS_SELECTOR(Material.TOTEM_OF_UNDYING, 1, "Hero Selector", "Click to open hero selection GUI.", HeroSelectGUI::new),
    MAP_SELECTOR(Material.FILLED_MAP, 2, "Map Selector", "Click to open map selection GUI.", MapSelectGUI::openGUI),
    COLLECTION(Material.CHEST, 4, "Collection", "Click to browse your cosmetic collection.", CollectionGUI::new),
    MODE_SELECTOR(Material.COMPARATOR, 6, "Settings", "Click to settings GUI.", SettingsGUI::new),
    START_GAME(Material.CLOCK, 7, "Start Game", "Click to start the game", player -> player.performCommand("cf start")),

    ;

    private final Material material;
    private final int slot;
    private final String name;
    private final String description;

    private final ItemStack itemStack;

    LobbyItems(Material material, int slot, String name, String description, Action<Player> click) {
        this.material = material;
        this.slot = slot;
        this.name = name;
        this.description = description;

        // allow click event from inventory
        this.itemStack = new ItemBuilder(material, "cf_" + name()).setName(name)
                .addSmartLore(description)
                .addClickEvent(click::use)
                //.setAllowInventoryClick(true)
                .asIcon();

    }

    public static void giveAll(Player player) {
        for (LobbyItems value : values()) {
            value.give(player);
        }
    }

    public void give(Player player) {
        player.getInventory().setItem(slot, itemStack);
    }

}
