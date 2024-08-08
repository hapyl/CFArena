package me.hapyl.fight.game.talents.nightmare;

import me.hapyl.fight.game.Response;
import me.hapyl.fight.game.entity.GamePlayer;
import me.hapyl.fight.game.heroes.Heroes;
import me.hapyl.fight.game.heroes.nightmare.Nightmare;
import me.hapyl.fight.game.talents.TalentType;
import me.hapyl.fight.game.talents.Talent;
import me.hapyl.fight.game.task.GameTask;
import me.hapyl.fight.util.Collect;
import me.hapyl.eterna.module.entity.Entities;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.ArmorStand;

import javax.annotation.Nonnull;

public class Paranoia extends Talent {

    public Paranoia() {
        super(
                "Paranoia",
                """
                        Launch a cloud of darkness in front of you that travels forward, applying &cOmen&7 to whoever it touches for {duration}.
                                                
                        &4👻 &c&lOmen:
                        Enemies take more damage and suffer &e&lParanoia&7.
                        """
        );

        setType(TalentType.IMPAIR);
        setItem(Material.CHARCOAL);
        setDuration(100);
        setCooldown(360);
    }

    @Override
    public Response execute(@Nonnull GamePlayer player) {
        final Location location = player.getLocation();
        final Nightmare hero = Heroes.NIGHTMARE.getHero(Nightmare.class);

        final ArmorStand stand = Entities.ARMOR_STAND.spawn(location.add(0.0d, 1.0d, 0.0d), self -> {
            self.setInvulnerable(true);
            self.setVisible(false);
            self.setSmall(true);
            self.setMarker(true);
            self.getLocation().setYaw(location.getYaw());
            self.getLocation().setPitch(location.getPitch());
        });

        player.playWorldSound(Sound.AMBIENT_CAVE, 1.0f);

        new GameTask() {
            private double currentIteration = 2.5d;

            @Override
            public void run() {
                if ((currentIteration -= 0.1d) <= 0) {
                    stand.remove();
                    cancel();
                    return;
                }

                // Teleport forward
                final Location standLocation = stand.getLocation();
                stand.teleport(standLocation.add(standLocation.getDirection()).multiply(1));

                // Fx
                player.spawnParticle(standLocation, Particle.LARGE_SMOKE, 2, 0.175d, 0.175d, 0.175d, 0.02f);
                player.spawnParticle(standLocation, Particle.WITCH, 2, 0.175d, 0.175d, 0.175d, 0.02f);
                player.playWorldSound(standLocation, Sound.BLOCK_ANVIL_STEP, 1.5f);

                // Apply blindness
                Collect.nearbyEntities(standLocation, 2.0d).forEach(target -> {
                    if (target.equals(player)) {
                        return;
                    }

                    hero.getDebuff(player).setOmen(target, getDuration());
                });

            }
        }.runTaskTimer(0, 1);

        return Response.OK;
    }
}
