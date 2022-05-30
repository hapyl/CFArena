package me.hapyl.fight.gui;

import me.hapyl.fight.game.team.GameTeam;
import me.hapyl.spigotutils.module.chat.Chat;
import me.hapyl.spigotutils.module.inventory.ItemBuilder;
import me.hapyl.spigotutils.module.inventory.gui.PlayerGUI;
import org.bukkit.entity.Player;

public class TeamSelectGUI extends PlayerGUI {
    public TeamSelectGUI(Player player) {
        super(player, "Select Team", 3);
        updateMenu();
        openInventory();
    }

    public void updateMenu() {
        clearEverything();
        final Player owner = getPlayer();
        int slot = 10;

        for (GameTeam team : GameTeam.values()) {
            final ItemBuilder builder = new ItemBuilder(team.getMaterial()).setName(team.getColor() + team.getName());

            builder.addLore();
            builder.addLore("&7Members:");

            for (int i = 0; i < team.getMaxPlayers(); i++) {
                final Player player = team.getLobbyPlayer(i);
                builder.addLore("- %s", player == null ? "&8Empty!" : (player == owner ? "&a&l" : "&a") + (player.getName()));
            }

            builder.addLore();

            if (team.isLobbyPlayer(owner)) {
                builder.addLore("&eClick to leave");
                setClick(slot, player -> {
                    team.removeFromTeam(player);
                    updateMenu();
                });
            }
            else if (team.isFull()) {
                builder.addLore("&cTeam is full!");
            }
            else {
                builder.addLore("&eClick to join");
                setClick(slot, player -> {
                    if (team.addToTeam(player)) {
                        Chat.sendMessage(player, "&aJoined %s team.", team.getName());
                    }
                    else {
                        Chat.sendMessage(player, "&cThis team is full!");
                    }
                    updateMenu();
                });
            }

            setItem(slot, builder.predicate(team.isLobbyPlayer(owner), ItemBuilder::glow).build());
            slot += 2;
        }
    }

}
