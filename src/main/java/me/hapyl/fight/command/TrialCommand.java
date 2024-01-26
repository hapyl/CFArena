package me.hapyl.fight.command;

import me.hapyl.fight.game.Manager;
import me.hapyl.fight.game.profile.PlayerProfile;
import me.hapyl.fight.game.trial.Trial;
import me.hapyl.fight.ux.Message;
import me.hapyl.spigotutils.module.command.SimplePlayerCommand;
import org.bukkit.entity.Player;

import javax.annotation.Nonnull;

public class TrialCommand extends SimplePlayerCommand {
    public TrialCommand(@Nonnull String name) {
        super(name);
    }

    @Override
    protected void execute(Player player, String[] args) {
        final PlayerProfile profile = PlayerProfile.getProfile(player);

        if (Manager.current().isGameInProgress()) {
            Message.error(player, "Cannot start trial while the game is in progress!");
            return;
        }

        if (profile == null) {
            Message.error(player, "No profile somehow.");
            return;
        }

        final Trial trial = profile.getTrial();

        if (trial != null) {
            profile.stopTrial();
            Message.success(player, "Stopped the trial!");
            return;
        }

        profile.newTrial();
        Message.success(player, "Starting new trial...");
    }

}
