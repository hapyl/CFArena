package me.hapyl.fight.game.talents.inferno;

import com.google.common.collect.Lists;
import me.hapyl.fight.game.damage.DamageCause;
import me.hapyl.fight.game.entity.GamePlayer;
import me.hapyl.fight.game.task.TickingGameTask;
import me.hapyl.fight.util.Collect;
import org.bukkit.Location;
import org.bukkit.Sound;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.function.Consumer;

public class FirePitHandler extends TickingGameTask {

    private final GamePlayer player;
    private final FirePitTalent talent;
    private final List<FirePit> firePits;

    private final int totalStages;
    private final int delayPerStage;

    FirePitHandler(@Nonnull GamePlayer player, @Nonnull FirePitTalent talent) {
        this.player = player;
        this.talent = talent;
        this.firePits = Lists.newArrayList();
        this.totalStages = talent.firePitsMaterials.length;
        this.delayPerStage = talent.transformDelay / totalStages;

        // Spawn fire pits
        final Location location = player.getLocation();

        for (int[] offset : talent.firePitsSpawnOffsets) {
            this.firePits.add(new FirePit(talent, player, location.clone().add(offset[0], 0, offset[1])));
        }

        runTaskTimer(0, 1);
    }

    private void forEach(@Nonnull Consumer<FirePit> consumer) {
        this.firePits.forEach(consumer);
    }

    @Override
    public void run(int tick) {
        final int stage = tick / delayPerStage;

        if (player.isDeadOrRespawning()) {
            extinguish();
            return;
        }

        if (tick % delayPerStage == 0 && stage < totalStages) {
            forEach(pit -> {
                pit.transform(talent.firePitsMaterials[stage]);

                // Fx
                final Location centre = pit.locations().getFirst();

                player.playWorldSound(centre, Sound.ITEM_FLINTANDSTEEL_USE, 0.75f + (0.75f * stage / totalStages));
            });
        }

        // Light pillars
        if (tick == talent.transformDelay) {
            forEach(pit -> {
                pit.lightTheFire();

                final Location centre = pit.fireLocations().getFirst();

                // Fx
                player.playWorldSound(centre, Sound.ITEM_FIRECHARGE_USE, 1.0f);
            });
        }

        // Damage detection
        if (tick > talent.transformDelay) {
            firePits.forEach(pit -> {
                final Location centre = pit.locations().getFirst();

                Collect.nearbyEntities(centre, 3d, player::isNotSelfOrTeammate)
                       .forEach(entity -> {
                           if (!pit.isInFire(entity)) {
                               return;
                           }

                           entity.damage(entity.getMaxHealth() * talent.damage, player, DamageCause.FIRE_PIT);
                       });
            });
        }

        // Extinguish
        if (tick >= talent.getDuration() + talent.transformDelay) {
            extinguish();
        }
    }

    public void extinguish() {
        if (isCancelled()) {
            return;
        }

        firePits.forEach(FirePit::remove);
        this.cancel();
    }
}
