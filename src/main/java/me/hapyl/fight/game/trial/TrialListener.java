package me.hapyl.fight.game.trial;

import me.hapyl.fight.CF;
import me.hapyl.fight.game.profile.PlayerProfile;
import me.hapyl.fight.game.trial.objecitive.TrialObjective;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.projectiles.ProjectileSource;

public class TrialListener implements Listener {

    public TrialListener() {
    }

    @EventHandler()
    public void handleProjectileHit(ProjectileHitEvent ev) {
        final ProjectileSource shooter = ev.getEntity().getShooter();

        if (!(shooter instanceof Player player)) {
            return;
        }

        workTrial(player, ev);
    }

    private <T extends Event> void workTrial(Player player, T event) {
        final PlayerProfile profile = CF.getProfile(player);
        final Trial trial = profile.getTrial();

        if (trial == null) {
            return;
        }

        final TrialObjective objective = trial.getCurrentObjective();

        if (objective == null) {
            return;
        }

        objective.handle(event);
    }


}
