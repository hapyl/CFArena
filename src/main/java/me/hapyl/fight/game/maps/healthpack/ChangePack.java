package me.hapyl.fight.game.maps.healthpack;

import me.hapyl.fight.game.GamePlayer;
import me.hapyl.fight.game.IGamePlayer;
import me.hapyl.fight.util.Utils;
import me.hapyl.spigotutils.module.locaiton.LocationHelper;
import me.hapyl.spigotutils.module.math.Tick;
import org.bukkit.*;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.Player;

public class ChangePack extends GamePack {

    private final BlockData BLOCK_DATA = Material.TUBE_CORAL_BLOCK.createBlockData();

    public ChangePack() {
        super(Tick.fromSecond(10), "d884ed1950bb8b198ada8191684400bd6640e03710481c8122b780b9ed1bd98c");
    }

    @Override
    public void onPickup(Player player) {
        final IGamePlayer gamePlayer = GamePlayer.getPlayer(player);

        gamePlayer.addUltimatePoints(10);
        gamePlayer.playSound(Sound.ENTITY_CHICKEN_EGG, 1.0f);
    }

    @Override
    public void displayParticle(Location location) {
        final World world = Utils.getWorld(location);

        final Location front = LocationHelper.getInFront(location, 0.5d);
        final Location back = LocationHelper.getBehind(location, 0.5d);

        world.spawnParticle(Particle.FALLING_DUST, front, 1, 0, 0, 0, 0, BLOCK_DATA);
        world.spawnParticle(Particle.FALLING_DUST, back, 1, 0, 0, 0, 0, BLOCK_DATA);
    }
}
