package me.hapyl.fight.gui;

import me.hapyl.eterna.module.chat.Chat;
import me.hapyl.eterna.module.inventory.ItemBuilder;
import me.hapyl.eterna.module.inventory.gui.PlayerPageGUI;
import me.hapyl.eterna.module.parkour.Stats;
import me.hapyl.fight.game.parkour.LeaderboardData;
import me.hapyl.fight.game.parkour.ParkourCourse;
import me.hapyl.fight.game.parkour.ParkourLeaderboard;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

import javax.annotation.Nonnull;
import java.util.LinkedHashMap;
import java.util.UUID;

public class ParkourLeaderboardGUI extends PlayerPageGUI<LeaderboardData> {

    private final ParkourCourse parkour;
    private final ParkourLeaderboard leaderboard;

    public ParkourLeaderboardGUI(Player player, ParkourCourse parkour) {
        super(player, parkour.getParkour().getName() + " Leaderboard", 5);
        this.parkour = parkour;
        this.leaderboard = parkour.getParkour().getLeaderboard();

        if (leaderboard == null) {
            throw new IllegalArgumentException("parkour %s does not have a leaderboard".formatted(parkour));
        }

        final LinkedHashMap<UUID, LeaderboardData> topPlayers = leaderboard.getTop(100);

        setContents(topPlayers.values().stream().toList());
        openInventory(0);
    }

    @Nonnull
    @Override
    public ItemStack asItem(@Nonnull Player player, LeaderboardData data, int index, int page) {
        final ItemBuilder builder = ItemBuilder.of(Material.PLAYER_HEAD)
                .setName(data.getNameFormatted())
                .addLore("&8#" + (index + 1))
                .addLore()
                .addLore("Completion Time: &f&l%s&fs".formatted(leaderboard.formatTime(data)))
                .addLore();

        // Apply texture
        final SkullMeta meta = builder.getMeta(SkullMeta.class);

        if (meta != null) {
            meta.setOwningPlayer(Bukkit.getOfflinePlayer(data.getUuid()));
            builder.setItemMeta(meta);
        }

        if (data.isDirty()) {
            builder.addSmartLore("&cThis record was modified by an admin!");
            builder.addLore();
        }

        if (data.hasStats()) {
            builder.addLore("&e&lStats:");
            for (Stats.Type value : Stats.Type.values()) {
                builder.addLore(" &7%s &f&l%s".formatted(Chat.capitalize(value.name()), data.getStat(value)));
            }
        }


        return builder.asIcon();
    }

}
