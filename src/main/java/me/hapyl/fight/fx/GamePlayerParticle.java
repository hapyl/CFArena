package me.hapyl.fight.fx;

import me.hapyl.fight.game.entity.GamePlayer;
import me.hapyl.eterna.module.math.geometry.Draw;
import org.bukkit.Location;
import org.bukkit.Particle;

public class GamePlayerParticle extends Draw {

    private final GamePlayer player;

    public GamePlayerParticle(Particle particle, GamePlayer player) {
        super(particle);
        this.player = player;
    }

    @Override
    public void draw(Location location) {
        this.player.spawnParticle(location, getParticle(), 1, 0, 0, 0, 0);
    }
}
