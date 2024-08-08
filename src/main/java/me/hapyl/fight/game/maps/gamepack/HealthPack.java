package me.hapyl.fight.game.maps.gamepack;

import me.hapyl.fight.game.entity.GamePlayer;
import me.hapyl.eterna.module.locaiton.LocationHelper;
import me.hapyl.eterna.module.math.Tick;
import me.hapyl.eterna.module.player.PlayerLib;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Sound;

import javax.annotation.Nonnull;

public class HealthPack extends GamePack {

    private final double HEALTH_POINTS = 20;

    public HealthPack() {
        super(Tick.fromMinute(3), "466a5f7bcd3c1c225a9366eee6dfab1cc66a6bf7363fed087512e6ef47a1d");
    }

    @Override
    public void onPickup(@Nonnull GamePlayer player) {
        player.heal(HEALTH_POINTS);

        player.sendTitle("&c♥&4❤&c♥", "&a+&l%s".formatted(HEALTH_POINTS), 0, 15, 5);

        player.playWorldSound(Sound.ENTITY_ILLUSIONER_CAST_SPELL, 1.25f);
        player.playWorldSound(Sound.ENTITY_PLAYER_LEVELUP, 2.0f);
    }

    @Override
    public void displayParticle(Location location) {
        PlayerLib.spawnParticle(LocationHelper.getInFront(location, 0.5d), Particle.FIREWORK, 1);
    }
}
