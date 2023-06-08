package me.hapyl.fight.cmds;

import me.hapyl.fight.gui.PlayerProfileGUI;
import org.bukkit.entity.Player;

public class ProfileCommand extends LobbyPlayerCommand {
    public ProfileCommand(String name) {
        super(name);
        setAliases("pf");
    }

    @Override
    protected void onCommand(Player player, String[] strings) {
        new PlayerProfileGUI(player);
    }


}
