package me.hapyl.fight.command;

import me.hapyl.fight.database.rank.PlayerRank;
import me.hapyl.fight.game.profile.PlayerProfile;
import me.hapyl.fight.game.profile.PlayerSocialConversation;
import me.hapyl.fight.ux.Notifier;
import me.hapyl.spigotutils.module.util.ArgumentList;
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
            Notifier.error(player, "This player is not online!");
            return;
        }

        final PlayerProfile playerProfile = PlayerProfile.getProfile(player);
        final PlayerProfile targetProfile = PlayerProfile.getProfile(target);

        if (playerProfile == null || targetProfile == null) {
            Notifier.error(player, "Error sending message!");
            return;
        }

        PlayerSocialConversation.talk(playerProfile, targetProfile, message);
    }

}
