package me.hapyl.fight.gui;

import me.hapyl.fight.database.rank.PlayerRank;
import me.hapyl.fight.game.FairMode;
import me.hapyl.fight.game.Manager;
import me.hapyl.fight.game.color.Color;
import me.hapyl.fight.game.gamemode.Modes;
import me.hapyl.fight.game.lobby.LobbyItems;
import me.hapyl.fight.game.maps.EnumLevel;
import me.hapyl.fight.game.team.Entry;
import me.hapyl.fight.game.team.GameTeam;
import me.hapyl.fight.gui.styled.Size;
import me.hapyl.fight.gui.styled.StyledGUI;
import me.hapyl.fight.gui.styled.StyledItem;
import me.hapyl.eterna.module.inventory.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.entity.Player;

public class GameManagementGUI extends StyledGUI {

    public GameManagementGUI(Player player) {
        super(player, "Game Management", Size.FOUR);
        openInventory();
    }

    @Override
    public void onUpdate() {
        final Manager manager = Manager.current();
        final EnumLevel currentMap = manager.getCurrentMap();
        final Modes currentMode = manager.getCurrentMode();
        final GameTeam playerTeam = GameTeam.getEntryTeam(Entry.of(player));

        setHeader(LobbyItems.GAME_MANAGEMENT.getItem().getItemStack());

        // Map Select
        setItemRanked(
                20,
                StyledItem.ICON_MAP_SELECT.toBuilder()
                        .addLore()
                        .addLore("Current Map: &a" + currentMap.getName()),
                PlayerRank.GAME_MANAGER, "Click to change that!", MapSelectGUI::new
        );

        // Mode Select
        setItemRanked(
                22,
                StyledItem.ICON_MODE_SELECT.toBuilder()
                        .addLore()
                        .addLore("Current Mode: &a" + currentMode.getMode().getName()),
                PlayerRank.GAME_MANAGER, "Click to change that!", ModeSelectGUI::new
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

        // Fair Mode
        final FairMode fairMode = manager.getFairMode();

        setItemRanked(42,
                StyledItem.ICON_FAIR_MODE
                        .toBuilder()
                        .addLore()
                        .addLore(Color.DEFAULT + "Current Mode")
                        .addLore(" " + fairMode.getName())
                        .addLore("  " + fairMode.getDescription()),
                PlayerRank.GAME_MANAGER, "Click to adjust", FairModeGUI::new
        );
    }

}
