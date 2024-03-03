package me.hapyl.fight.command;

import com.google.common.collect.Sets;
import me.hapyl.fight.database.rank.PlayerRank;
import me.hapyl.fight.filter.ProfanityFilter;
import me.hapyl.fight.game.profile.PlayerProfile;
import me.hapyl.fight.game.profile.PlayerDisplay;
import me.hapyl.fight.ux.Notifier;
import me.hapyl.spigotutils.module.command.DisabledCommand;
import me.hapyl.spigotutils.module.util.ArgumentList;
import org.bukkit.entity.Player;

import javax.annotation.Nonnull;
import java.util.Set;
import java.util.regex.Pattern;

public class NickCommand extends CFCommand implements DisabledCommand {

    private final Set<String> disallowedNames;
    private final Pattern namePattern = Pattern.compile("^[a-zA-Z0-9_]{3,16}[^_]$");

    public NickCommand() {
        super("nick", PlayerRank.PREMIUM);

        disallowedNames = Sets.newHashSet();
        disallowedNames.add("hapyl");
        disallowedNames.add("DiDenPro");
        disallowedNames.add("sdimas74");
        disallowedNames.add("DirtyEl");
    }

    @Override
    protected void execute(@Nonnull Player player, @Nonnull ArgumentList args, @Nonnull PlayerRank rank) {
        if (!ProfanityFilter.isInstantiated()) {
            Notifier.error(player, "This feature cannot be used yet, try again in a moment!");
            return;
        }

        final PlayerProfile profile = PlayerProfile.getProfile(player);

        if (profile == null) {
            Notifier.error(player, "Bad profile!");
            return;
        }

        final PlayerDisplay display = profile.getDisplay();
        final String newNick = args.get(0).toString();

        if (newNick.isEmpty() || newNick.equalsIgnoreCase("reset")) {
            display.resetNick();
            Notifier.success(player, "Reset your nick!");
            return;
        }

        if (!namePattern.matcher(newNick).matches()) {
            Notifier.error(player, "Invalid nick!");
            return;
        }

        if (disallowedNames.contains(newNick) && !rank.isOrHigher(PlayerRank.ADMIN)) {
            Notifier.error(player, "This nick is disallowed!");
            return;
        }

        if (ProfanityFilter.isProfane(newNick)) {
            Notifier.error(player, "You cannot use that as a nick!");
            return;
        }

        display.setNick(newNick);

        Notifier.success(player, "Set your nick to: {}!", newNick);
        Notifier.error(player, "Keep in mind abusing the nick system is a bannable offense!");

        Notifier.broadcastStaff("{} changed their name to {}.", player.getName(), newNick);
    }

}
