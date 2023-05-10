package me.hapyl.fight.game.maps.healthpack;

import me.hapyl.fight.game.GamePlayer;
import me.hapyl.fight.game.IGamePlayer;
import me.hapyl.spigotutils.module.locaiton.LocationHelper;
import me.hapyl.spigotutils.module.math.Tick;
import me.hapyl.spigotutils.module.player.PlayerLib;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

public class HealthPack extends GamePack {

    private final double HEALTH_POINTS = 20;

    public HealthPack() {
        super(Tick.fromMinute(3), "466a5f7bcd3c1c225a9366eee6dfab1cc66a6bf7363fed087512e6ef47a1d");
    }

    @Override
    public void onPickup(Player player) {
        final IGamePlayer gamePlayer = GamePlayer.getPlayer(player);

        gamePlayer.heal(HEALTH_POINTS);
        gamePlayer.sendMessage("&4&lHEALTH PACK &7⁑ &c&l+20 &c❤", HEALTH_POINTS);

        PlayerLib.playSound(player.getLocation(), Sound.ENTITY_CHICKEN_EGG, 1.0f);
    }

    @Override
    public void displayParticle(Location location) {
        PlayerLib.spawnParticle(LocationHelper.getInFront(location, 0.5d), Particle.FIREWORKS_SPARK, 1);
    }
}
