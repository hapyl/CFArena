package me.hapyl.fight.game.talents.storage.swooper;

import me.hapyl.fight.game.ChatGPT;
import me.hapyl.fight.game.Response;
import me.hapyl.fight.game.talents.ChargedTalent;
import me.hapyl.spigotutils.module.player.PlayerLib;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.util.BlockIterator;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.List;

public class Blink extends ChargedTalent implements ChatGPT {

    private final int MAX_DISTANCE = 6;
    private final int MIN_DISTANCE = 1;

    public Blink() {
        super(
                "Blink",
                "Teleport forward a short distance, passing through any non-solid blocks in the way. Cannot be used to teleport through walls.",
                2
        );

        this.setItem(Material.ENDER_PEARL);
        this.setRechargeTimeSec(5);
        this.setNoChargedMaterial(Material.ENDER_EYE);
    }

    @Override
    public Response execute(Player player) {
        // Check if the player has enough space to blink
        final Location start = player.getEyeLocation();
        final Vector direction = start.getDirection().normalize();
        final List<Block> blocks = new ArrayList<>();
        final BlockIterator iterator = new BlockIterator(player.getWorld(), start.toVector(), direction, 0, (int) Math.ceil(MAX_DISTANCE));

        double maxDistance = MAX_DISTANCE;

        while (iterator.hasNext()) {
            final Block block = iterator.next();
            blocks.add(block);
            if (block.getType() != Material.AIR && block.getType().isSolid()) {
                break;
            }
        }

        for (final Block block : blocks) {
            if (block.getType() != Material.AIR) {
                final double distance = start.distance(block.getLocation());
                if (distance < maxDistance) {
                    maxDistance = distance - MIN_DISTANCE;
                    if (maxDistance < 0) {
                        maxDistance = 0;
                    }
                }
            }
        }

        // Fx before TP
        PlayerLib.spawnParticle(player.getEyeLocation(), Particle.EXPLOSION_NORMAL, 20, 0.1, 0.3, 0.1, 0.1f);

        // Teleport the player to the destination location
        final Location dest = start.clone().add(direction.multiply(maxDistance)).add(0.0d, 0.5d, 0.0d);

        if (!dest.getBlock().getType().isAir()) {
            return Response.error("Cannot teleport inside a block!");
        }

        player.teleport(dest);

        // Fx
        PlayerLib.playSound(player, Sound.ENTITY_ENDERMAN_TELEPORT, 1.0f);

        return Response.OK;
    }
}
