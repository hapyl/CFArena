package me.hapyl.fight.command;

import me.hapyl.fight.game.Manager;
import me.hapyl.fight.game.profile.PlayerProfile;
import me.hapyl.fight.game.trial.Trial;
import me.hapyl.fight.ux.Notifier;
import me.hapyl.spigotutils.module.command.SimplePlayerCommand;
import org.bukkit.entity.Player;

import javax.annotation.Nonnull;

public class TrialCommand extends SimplePlayerCommand {

    public TrialCommand(@Nonnull String name) {
        super(name);
    }

    @Override
    protected void execute(Player player, String[] args) {
        if (true) {
            Notifier.error(player, "Trial is currently disabled, sorry!");
            return;
        }

        final PlayerProfile profile = PlayerProfile.getProfile(player);

        if (Manager.current().isGameInProgress()) {
            Notifier.error(player, "Cannot start trial while the game is in progress!");
            return;
        }

        if (profile == null) {
            Notifier.error(player, "No profile somehow.");
            return;
        }

        final Trial trial = profile.getTrial();

        if (trial != null) {
            profile.stopTrial();
            Notifier.success(player, "Stopped the trial!");
            return;
        }

        profile.newTrial();
        Notifier.success(player, "Starting new trial...");
    }

}
