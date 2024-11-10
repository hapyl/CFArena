package me.hapyl.fight.gui;

import me.hapyl.eterna.module.inventory.ItemBuilder;
import me.hapyl.fight.CF;
import me.hapyl.fight.game.color.Color;
import me.hapyl.fight.game.profile.PlayerProfile;
import me.hapyl.fight.game.team.Entry;
import me.hapyl.fight.game.team.GameTeam;
import me.hapyl.fight.gui.styled.Size;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class TeamSelectGUI extends GameManagementSubGUI<GameTeam> {
    public TeamSelectGUI(Player player) {
        super(player, "Select Team", Size.FIVE, GameTeam.getTeams());
    }

    @Override
    public int getStartIndex() {
        return 2;
    }

    @Nonnull
    @Override
    public String getButton(@Nonnull GameTeam team, boolean isSelected) {
        return isSelected
                ? Color.SUCCESS.color("Already in this team!") : team.isFull()
                ? Color.ERROR.color("This team is full!") : Color.BUTTON.color("Click to join!");
    }

    @Nonnull
    @Override
    public ItemStack getHeaderItem() {
        final GameTeam team = getPlayerTeam();

        return new ItemBuilder(team == null ? Material.BLACK_BANNER : team.getMaterial())
                .setName("Team")
                .addLore("Select your team!")
                .asIcon();
    }

    @Nonnull
    @Override
    public ItemBuilder createItem(@Nonnull GameTeam team, boolean isSelected) {
        final ItemBuilder builder = new ItemBuilder(team.getMaterial()).setName(team.getColor() + team.getName());

        builder.addLore();
        builder.addLore("Members:");

        for (int i = 0; i < team.getMaxPlayers(); i++) {
            final Player lobbyPlayer = team.getBukkitPlayer(i);

            if (lobbyPlayer == null) {
                builder.addLore("&8- Empty!");
                continue;
            }

            // Realistically, the above check should handle profile check, but just in case
            if (CF.hasProfile(lobbyPlayer)) {
                final PlayerProfile profile = CF.getProfile(lobbyPlayer);

                builder.addLore("&8- " + profile.getDisplay());
            }
        }

        return builder;
    }

    @Nullable
    private GameTeam getPlayerTeam() {
        return GameTeam.getEntryTeam(Entry.of(player));
    }

}
