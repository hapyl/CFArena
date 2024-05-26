package me.hapyl.fight.game.maps.gamepack;

import me.hapyl.fight.game.entity.GamePlayer;
import me.hapyl.fight.util.CFUtils;
import me.hapyl.spigotutils.module.locaiton.LocationHelper;
import me.hapyl.spigotutils.module.math.Tick;
import org.bukkit.*;

public class ChangePack extends GamePack {

    public static final Particle.DustTransition DUST_TRANSITION = new Particle.DustTransition(
            Color.fromRGB(2, 32, 79),
            Color.fromRGB(33, 55, 89),
            1
    );
    private final int CHARGE_POINTS = 10;

    public ChangePack() {
        super(Tick.fromMinute(5), "d884ed1950bb8b198ada8191684400bd6640e03710481c8122b780b9ed1bd98c");
    }

    @Override
    public void onPickup(GamePlayer player) {
        player.addEnergy(CHARGE_POINTS);

        player.sendTitle("&b※&9&l※&b※", "&a+&l%s".formatted(CHARGE_POINTS), 0, 15, 5);
        player.playWorldSound(Sound.BLOCK_ENCHANTMENT_TABLE_USE, 0.5f);
    }

    @Override
    public void displayParticle(Location location) {
        final World world = CFUtils.getWorld(location);

        final Location front = LocationHelper.getInFront(location, 0.5d);
        final Location back = LocationHelper.getBehind(location, 0.5d);

        world.spawnParticle(Particle.DUST_COLOR_TRANSITION, front, 1, 0, 0, 0, 0, DUST_TRANSITION);
        world.spawnParticle(Particle.DUST_COLOR_TRANSITION, back, 1, 0, 0, 0, 0, DUST_TRANSITION);
    }
}
