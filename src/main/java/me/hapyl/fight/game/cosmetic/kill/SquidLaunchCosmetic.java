package me.hapyl.fight.game.cosmetic.kill;

import me.hapyl.eterna.module.entity.Entities;
import me.hapyl.eterna.module.registry.Key;
import me.hapyl.eterna.module.util.ThreadRandom;
import me.hapyl.fight.game.cosmetic.Cosmetic;
import me.hapyl.fight.game.cosmetic.Display;
import me.hapyl.fight.game.cosmetic.Rarity;
import me.hapyl.fight.game.cosmetic.Type;
import me.hapyl.fight.game.task.GameTask;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.Squid;
import org.bukkit.util.Vector;

import javax.annotation.Nonnull;

public class SquidLaunchCosmetic extends Cosmetic {
    public SquidLaunchCosmetic(@Nonnull Key key) {
        super(key, "Squid Launch", Type.KILL);

        setDescription("""
                Launches a squid into the space!
                &8&oIs this the one?
                """
        );


        setRarity(Rarity.RARE);
        setIcon(Material.SQUID_SPAWN_EGG);
    }

    @Override
    public void onDisplay(@Nonnull Display display) {

        final Squid squid = (ThreadRandom.nextFloatAndCheckBetween(0.9f, 1.0f) ? Entities.GLOW_SQUID : Entities.SQUID).spawn(
                display.getLocation(),
                self -> {
                    self.setInvulnerable(true);
                    self.setSilent(true);
                    self.setAI(false);
                    self.setGravity(false);
                }
        );

        new GameTask() {
            private int tick = 0;

            @Override
            public void run() {
                if (tick++ >= 10) {
                    squid.remove();
                    this.cancel();
                    return;
                }

                squid.setVelocity(new Vector(0, 0.35, 0));
                display.particle(squid.getLocation(), Particle.POOF, 5, 0.1d, 0.1d, 0.1d, 0.1f);
                display.sound(Sound.ENTITY_CHICKEN_EGG, (0.0f + (tick * 0.1f)));
            }
        }.runTaskTimer(0, 2);
    }
}
