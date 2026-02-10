package me.hapyl.fight.command;

import me.hapyl.eterna.module.command.SimplePlayerCommand;
import me.hapyl.fight.CF;
import me.hapyl.fight.Message;
import me.hapyl.fight.game.Manager;
import me.hapyl.fight.game.profile.PlayerProfile;
import me.hapyl.fight.game.trial.Trial;
import org.bukkit.entity.Player;

import javax.annotation.Nonnull;

public class TrialCommand extends SimplePlayerCommand {

    public TrialCommand(@Nonnull String name) {
        super(name);
    }

    @Override
    protected void execute(Player player, String[] args) {
        if (true) {
            Message.error(player, "Trial is currently disabled, sorry!");
            return;
        }

        final PlayerProfile profile = CF.getProfile(player);

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
