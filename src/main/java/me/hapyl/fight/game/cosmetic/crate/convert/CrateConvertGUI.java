package me.hapyl.fight.game.cosmetic.crate.convert;

import me.hapyl.fight.gui.styled.Size;
import me.hapyl.fight.gui.styled.StyledGUI;
import org.bukkit.entity.Player;

public class CrateConvertGUI extends StyledGUI {
    public CrateConvertGUI(Player player) {
        super(player, "Convert Crates", Size.FOUR);
    }

    @Override
    public void onUpdate() {

    }
}
