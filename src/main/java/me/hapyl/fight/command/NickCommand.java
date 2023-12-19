package me.hapyl.fight.command;

import com.google.common.collect.Sets;
import me.hapyl.fight.database.rank.PlayerRank;
import me.hapyl.fight.filter.ProfanityFilter;
import me.hapyl.fight.game.profile.PlayerProfile;
import me.hapyl.fight.game.profile.ProfileDisplay;
import me.hapyl.fight.ux.Message;
import me.hapyl.spigotutils.module.command.DisabledCommand;
import org.bukkit.entity.Player;

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
    protected void execute(Player player, String[] args, PlayerRank rank) {
        if (!ProfanityFilter.isInstantiated()) {
            Message.error(player, "This feature cannot be used yet, try again in a moment!");
            return;
        }

        final PlayerProfile profile = PlayerProfile.getProfile(player);

        if (profile == null) {
            Message.error(player, "Bad profile!");
            return;
        }

        final ProfileDisplay display = profile.getDisplay();
        final String newNick = getArgument(args, 0).toString();

        if (newNick.isEmpty() || newNick.equalsIgnoreCase("reset")) {
            display.resetNick();
            Message.success(player, "Reset your nick!");
            return;
        }

        if (!namePattern.matcher(newNick).matches()) {
            Message.error(player, "Invalid nick!");
            return;
        }

        if (disallowedNames.contains(newNick) && !rank.isOrHigher(PlayerRank.ADMIN)) {
            Message.error(player, "This nick is disallowed!");
            return;
        }

        if (ProfanityFilter.isProfane(newNick)) {
            Message.error(player, "You cannot use that as a nick!");
            return;
        }

        display.setNick(newNick);

        Message.success(player, "Set your nick to: {}!", newNick);
        Message.error(player, "Keep in mind abusing the nick system is a bannable offense!");

        Message.broadcastStaff("{} changed their name to {}.", player.getName(), newNick);
    }

}
