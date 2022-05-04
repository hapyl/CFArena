package kz.hapyl.fight.util;

import kz.hapyl.spigotutils.module.inventory.gui.PlayerGUI;
import org.bukkit.entity.Player;

public abstract class Menu extends PlayerGUI {

    public Menu(Player player, String name, int rows) {
        super(player, name, rows);
        this.createInventory();
        this.openInventory();
    }

    public abstract void createInventory();

    @Override
    public final void openInventory() {
        super.openInventory();
    }
}
