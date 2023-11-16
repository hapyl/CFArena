package me.hapyl.fight.gui;

import me.hapyl.fight.game.Manager;
import me.hapyl.fight.game.color.Color;
import me.hapyl.fight.game.gamemode.Modes;
import me.hapyl.fight.game.lobby.LobbyItems;
import me.hapyl.fight.game.maps.GameMaps;
import me.hapyl.fight.game.team.GameTeam;
import me.hapyl.fight.gui.styled.Size;
import me.hapyl.fight.gui.styled.StyledGUI;
import me.hapyl.fight.gui.styled.StyledItem;
import me.hapyl.spigotutils.module.inventory.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.entity.Player;

public class GameManagementGUI extends StyledGUI implements StrictlyLobbyGUI {

    public GameManagementGUI(Player player) {
        super(player, "Game Management", Size.FOUR);
        openInventory();
    }

    @Override
    public void onUpdate() {
        final Manager manager = Manager.current();
        final GameMaps currentMap = manager.getCurrentMap();
        final Modes currentMode = manager.getCurrentMode();
        final GameTeam playerTeam = GameTeam.getPlayerTeam(player.getUniqueId());

        setHeader(LobbyItems.GAME_MANAGEMENT.getItem().getItemStack());

        // Map Select
        setItem(
                20,
                StyledItem.ICON_MAP_SELECT.asIconWithLore(
                        "",
                        "Current Map: &a" + currentMap.getName(),
                        "",
                        Color.BUTTON + "Click to change that!"
                ), MapSelectGUI::new
        );

        // Mode Select
        setItem(
                22,
                StyledItem.ICON_MODE_SELECT.asIconWithLore(
                        "",
                        "Current Mode: &a" + currentMode.getMode().getName(),
                        "",
                        Color.BUTTON + "Click to change that!"
                ),
                ModeSelectGUI::new
        );

        // Team
        setItem(24, new ItemBuilder(playerTeam == null ? Material.BLACK_BANNER : playerTeam.getMaterial())
                        .setName("Selected Team")
                        .addLore()
                        .addLore("Your Team: " + (playerTeam == null ? "&8None!" : playerTeam.getColor() + playerTeam.getName()))
                        .addLore()
                        .addLore(Color.BUTTON + "Click to select a team!")
                        .asIcon(),
                TeamSelectGUI::new
        );

    }
}
