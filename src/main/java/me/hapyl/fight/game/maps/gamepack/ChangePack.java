package me.hapyl.fight.game.maps.gamepack;

import me.hapyl.fight.CF;
import me.hapyl.fight.game.entity.GamePlayer;
import me.hapyl.fight.util.Utils;
import me.hapyl.spigotutils.module.locaiton.LocationHelper;
import me.hapyl.spigotutils.module.math.Tick;
import me.hapyl.spigotutils.module.player.PlayerLib;
import org.bukkit.*;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.Player;

public class ChangePack extends GamePack {

    private final BlockData BLOCK_DATA = Material.TUBE_CORAL_BLOCK.createBlockData();
    private final int CHARGE_POINTS = 10;

    public ChangePack() {
        super(Tick.fromMinute(5), "d884ed1950bb8b198ada8191684400bd6640e03710481c8122b780b9ed1bd98c");
    }

    @Override
    public void onPickup(Player player) {
        final GamePlayer gamePlayer = CF.getOrCreatePlayer(player);

        gamePlayer.addUltimatePoints(CHARGE_POINTS);
        //gamePlayer.sendMessage("&3&lCHARGE PACK &7⁑ &b&l+%s &b※ Ultimate Points", CHARGE_POINTS);
        gamePlayer.sendTitle("&b※&9&l※&b※", "&a+&l%s".formatted(CHARGE_POINTS), 0, 15, 5);

        PlayerLib.playSound(player.getLocation(), Sound.BLOCK_ENCHANTMENT_TABLE_USE, 0.5f);
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
