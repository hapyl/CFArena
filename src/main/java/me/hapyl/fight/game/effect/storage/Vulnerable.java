package me.hapyl.fight.game.effect.storage;

import me.hapyl.fight.game.effect.GameEffect;
import me.hapyl.spigotutils.module.player.PlayerLib;
import org.bukkit.Particle;
import org.bukkit.entity.Player;

public class Vulnerable extends GameEffect {

    public Vulnerable() {
        super("Vulnerable");
        this.setDescription("Players affected by vulnerability take 50% more damage.");
        this.setPositive(false);
    }

    @Override
    public void onTick(Player player, int tick) {
        if (tick == 5) {
            PlayerLib.spawnParticle(player.getLocation().add(0.0d, 1.0d, 0.0d), Particle.VILLAGER_ANGRY, 1, 0.1d, 0.0d, 0.1d, 0.0f);
        }
    }

    @Override
    public void onStart(Player player) {

    }

    @Override
    public void onStop(Player player) {

    }
}
