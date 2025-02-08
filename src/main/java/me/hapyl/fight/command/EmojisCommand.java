package me.hapyl.fight.command;

import me.hapyl.eterna.module.command.SimplePlayerCommand;
import me.hapyl.fight.CF;
import me.hapyl.fight.Message;
import me.hapyl.fight.emoji.Emojis;
import me.hapyl.fight.game.profile.PlayerProfile;
import org.bukkit.entity.Player;

import java.util.List;

public class EmojisCommand extends SimplePlayerCommand {
    public EmojisCommand(String name) {
        super(name);
    }

    @Override
    protected void execute(Player player, String[] args) {
        final PlayerProfile profile = CF.getProfile(player);
        final List<Emojis> emojis = Emojis.getAvailable(profile);

        if (emojis.isEmpty()) {
            Message.error(player, "There are no emojis available to you!");
        }
        else {
            Message.success(player, "There are %s emojis available to you:".formatted(emojis.size()));

            emojis.forEach(emoji -> {
                Message.info(player, " %s &8-> %s".formatted(emoji.getText(), emoji.getEmoji()));
            });
        }

    }
}
