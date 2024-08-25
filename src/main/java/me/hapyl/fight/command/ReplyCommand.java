package me.hapyl.fight.command;

import me.hapyl.fight.database.rank.PlayerRank;
import me.hapyl.fight.game.profile.PlayerProfile;
import me.hapyl.fight.game.profile.PlayerSocialConversation;
import me.hapyl.fight.Notifier;
import me.hapyl.eterna.module.util.ArgumentList;
import org.bukkit.entity.Player;

import javax.annotation.Nonnull;

public class ReplyCommand extends CFCommand {
    public ReplyCommand(@Nonnull String name) {
        super(name, PlayerRank.DEFAULT);

        setAliases("r");
        setUsage("/reply (message)");
    }

    @Override
    protected void execute(@Nonnull Player player, @Nonnull ArgumentList args, @Nonnull PlayerRank rank) {
        final PlayerProfile profile = PlayerProfile.getProfile(player);

        if (profile == null) {
            Notifier.error(player, "Error getting your profile!");
            return;
        }

        final PlayerSocialConversation conversation = profile.getConversation();
        final Player lastMessenger = conversation.getLastMessenger();

        if (lastMessenger == null) {
            Notifier.error(player, "No one to reply to!");
            return;
        }

        final PlayerProfile targetProfile = PlayerProfile.getProfile(lastMessenger);

        if (targetProfile == null) {
            Notifier.error(player, "The player you're trying to reply is no longer online.");
            conversation.lastMessenger = null;
            return;
        }

        final String message = args.makeStringArray(0);

        PlayerSocialConversation.talk(profile, targetProfile, message);
    }
}
