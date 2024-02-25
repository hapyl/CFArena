package me.hapyl.fight.command;

import me.hapyl.fight.database.rank.PlayerRank;
import me.hapyl.fight.emoji.Emojis;
import me.hapyl.fight.game.profile.PlayerProfile;
import me.hapyl.fight.ux.Message;
import me.hapyl.spigotutils.module.command.SimplePlayerCommand;
import org.bukkit.entity.Player;

import java.util.List;

public class EmojisCommand extends SimplePlayerCommand {
    public EmojisCommand(String name) {
        super(name);
    }

    @Override
    protected void execute(Player player, String[] args) {
        final PlayerProfile profile = PlayerProfile.getProfile(player);

        if (profile == null) {
            return;
        }

        final PlayerRank playerRank = profile.getRank();
        final List<Emojis> emojis = Emojis.byRank(playerRank);

        if (emojis.isEmpty()) {
            Message.error(player, "There are no emojis available to you!");
        }
        else {
            Message.success(player, "There are %s emojis available to you:".formatted(emojis.size()));

            emojis.forEach(emoji -> {
                Message.info(player, " %s -> %s".formatted(emoji.getText(), emoji.getEmoji()));
            });
        }

    }
}
