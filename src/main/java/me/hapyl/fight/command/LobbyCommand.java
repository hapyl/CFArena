package me.hapyl.fight.command;

import me.hapyl.eterna.module.command.SimplePlayerCommand;
import me.hapyl.fight.game.Manager;
import me.hapyl.fight.game.maps.EnumLevel;
import org.bukkit.entity.Player;

import javax.annotation.Nonnull;

public class LobbyCommand extends SimplePlayerCommand {
    public LobbyCommand(@Nonnull String name) {
        super(name);
    }

    @Override
    protected void execute(Player player, String[] args) {
        if (Manager.current().isGameInProgress()) {
            player.sendRichMessage("<dark_red>You cannot use while in game!");
            return;
        }

        player.teleport(EnumLevel.SPAWN.getLevel().getLocation());
        player.sendRichMessage("<green>Woosh!");
    }

}
