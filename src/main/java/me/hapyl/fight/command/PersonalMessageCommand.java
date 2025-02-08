package me.hapyl.fight.command;

import me.hapyl.eterna.module.util.ArgumentList;
import me.hapyl.fight.CF;
import me.hapyl.fight.Message;
import me.hapyl.fight.database.rank.PlayerRank;
import me.hapyl.fight.game.profile.PlayerProfile;
import me.hapyl.fight.game.profile.PlayerSocialConversation;
import org.bukkit.entity.Player;

import javax.annotation.Nonnull;

public class PersonalMessageCommand extends CFCommand {
    public PersonalMessageCommand(@Nonnull String name) {
        super(name, PlayerRank.DEFAULT);

        setAliases("msg", "w");
        setUsage("/tell (player) (message...)");
    }

    @Override
    protected void execute(@Nonnull Player player, @Nonnull ArgumentList args, @Nonnull PlayerRank rank) {
        // tell (player) (message...)

        if (args.length == 0) {
            sendInvalidUsageMessage(player);
            return;
        }

        final Player target = args.get(0).toPlayer();
        final String message = args.makeStringArray(1);

        if (target == null) {
            Message.error(player, "This player is not online!");
            return;
        }

        final PlayerProfile playerProfile = CF.getProfile(player);
        final PlayerProfile targetProfile = CF.getProfile(target);

        PlayerSocialConversation.talk(playerProfile, targetProfile, message);
    }

}
