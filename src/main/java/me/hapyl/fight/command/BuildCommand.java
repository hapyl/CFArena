package me.hapyl.fight.command;

import me.hapyl.eterna.module.util.ArgumentList;
import me.hapyl.fight.CF;
import me.hapyl.fight.Message;
import me.hapyl.fight.database.rank.PlayerRank;
import me.hapyl.fight.game.profile.PlayerProfile;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

import javax.annotation.Nonnull;

public class BuildCommand extends CFCommand {
    public BuildCommand(@Nonnull String name) {
        super(name, PlayerRank.BUILD);
    }

    @Override
    protected void execute(@Nonnull Player player, @Nonnull ArgumentList args, @Nonnull PlayerRank rank) {
        final PlayerProfile profile = CF.getProfile(player);
        final boolean value = profile.buildMode();

        profile.buildMode(!value);

        Message.info(player, "&b&lBUILD MODE %s&b!".formatted(!value ? "&aYou can now build" : "&cYou can no longer build"));
        Message.sound(player, Sound.ENTITY_PLAYER_LEVELUP, 2.0f);
    }
}
