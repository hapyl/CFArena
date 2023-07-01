package me.hapyl.fight.game.cosmetic.storage;

import me.hapyl.fight.game.cosmetic.Cosmetic;
import me.hapyl.fight.game.cosmetic.Display;
import me.hapyl.fight.game.cosmetic.Type;
import me.hapyl.fight.game.cosmetic.Rarity;
import me.hapyl.fight.game.task.GameTask;
import me.hapyl.spigotutils.module.entity.Entities;
import me.hapyl.spigotutils.module.util.ThreadRandom;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.Squid;
import org.bukkit.util.Vector;

public class SquidLaunchCosmetic extends Cosmetic {
    public SquidLaunchCosmetic() {
        super(
                "Squid Launch",
                "Launches a squid into the space!__&8&oIs this the a one?",
                Type.KILL,
                Rarity.RARE,
                Material.SQUID_SPAWN_EGG
        );
    }

    @Override
    public void onDisplay(Display display) {

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
                display.particle(squid.getLocation(), Particle.EXPLOSION_NORMAL, 5, 0.1d, 0.1d, 0.1d, 0.1f);
                display.sound(Sound.ENTITY_CHICKEN_EGG, (0.0f + (tick * 0.1f)));
            }
        }.runTaskTimer(0, 2);
    }
}
