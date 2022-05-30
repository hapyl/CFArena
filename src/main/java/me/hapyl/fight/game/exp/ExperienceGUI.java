package me.hapyl.fight.game.exp;

import me.hapyl.fight.util.Menu;
import org.bukkit.entity.Player;

public class ExperienceGUI extends Menu {

    public ExperienceGUI(Player player) {
        super(player, "translate.gui.exp", 3);
    }

    @Override
    public void createInventory() {

    }
}
