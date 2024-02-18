package me.hapyl.fight.game.heroes.archive.ender;

import me.hapyl.fight.event.custom.EnderPearlTeleportEvent;
import me.hapyl.fight.game.Response;
import me.hapyl.fight.game.entity.GamePlayer;
import me.hapyl.fight.game.task.GameTask;
import me.hapyl.fight.game.weapons.Weapon;
import me.hapyl.fight.game.weapons.ability.Ability;
import me.hapyl.fight.game.weapons.ability.AbilityType;
import me.hapyl.fight.util.Blocks;
import me.hapyl.fight.util.collection.player.PlayerMap;
import me.hapyl.spigotutils.module.math.Geometry;
import me.hapyl.spigotutils.module.math.geometry.WorldParticle;
import me.hapyl.spigotutils.module.util.BukkitUtils;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.inventory.ItemStack;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

public class EnderWeapon extends Weapon {

    public EnderWeapon() {
        super(Material.NETHER_BRICK);

        setName("Fist");
        setId("ender_weapon");
        setDescription("Just a normal-sized fist.");
        setDamage(7.0);

        setAbility(AbilityType.RIGHT_CLICK, new Transmission());
    }

    private static class Transmission extends Ability {

        private final PlayerMap<Location> targetLocation = PlayerMap.newMap();

        public Transmission() {
            super("Transition", """
                    Initiate transition to a target block, after a short casting time, teleport to the location.
                                    
                    Right Click again to cancel.
                    """);

            setDuration(20);
            setCooldown(160);
        }

        @Nullable
        @Override
        public Response execute(@Nonnull GamePlayer player, @Nonnull ItemStack item) {
            // Cancel
            if (hasLocation(player)) {
                targetLocation.remove(player);

                player.sendSubtitle("&c&lCANCELLED", 0, 10, 10);
                player.playSound(Sound.BLOCK_LEVER_CLICK, 0.0f);
                return Response.AWAIT;
            }

            // Check for los block
            final Block targetBlock = player.getTargetBlockExact(25);

            if (!Blocks.isValid(targetBlock)) {
                return Response.error("Not a valid block!");
            }

            // Initiate
            final List<Block> lastTwoTargetBlocks = player.getLastTwoTargetBlocks(25);

            final Block preLastBlock = lastTwoTargetBlocks.get(0);
            final Block lastBlock = lastTwoTargetBlocks.get(1);

            // Not a valid block
            if (preLastBlock == null || lastBlock == null) {
                player.playSound(Sound.ENTITY_ENDERMAN_TELEPORT, 0.0f);
                return Response.error("Not a valid block!");
            }

            // From below a block
            if (preLastBlock.getType().isAir()) {
                final Block downBlock = lastBlock.getRelative(BlockFace.DOWN);

                if (downBlock.getLocation().equals(preLastBlock.getLocation())) {
                    return Response.error("Cannot teleport from below a block!");
                }
            }

            final Location location = lastBlock.getRelative(BlockFace.UP).getLocation().add(0.5d, 0.0d, 0.5d);

            if (!location.getBlock().getType().isAir() || !location.getBlock().getRelative(BlockFace.UP).getType().isAir()) {
                player.playSound(Sound.ENTITY_ENDERMAN_TELEPORT, 0.0f);
                return Response.error("Target location is not safe!");
            }

            targetLocation.put(player, location);

            new GameTask() {
                private final int maxWindupTime = getDuration();
                private int windupTime = maxWindupTime;

                @Override
                public void run() {
                    if (!hasLocation(player)) {
                        cancel();
                        return;
                    }

                    if (windupTime > 0) {
                        final StringBuilder builder = new StringBuilder();
                        final int percentOfEight = (windupTime * 8 / maxWindupTime);

                        for (int i = 0; i < 8; i++) {
                            builder.append(i >= percentOfEight ? "&8" : "&a");
                            builder.append("-");
                        }

                        final double distance = 0.8d / maxWindupTime * windupTime;

                        // Display fx at target block
                        final double x = Math.sin(windupTime) * distance;
                        final double y = (double) windupTime / maxWindupTime;
                        final double z = Math.cos(windupTime) * distance;

                        location.add(x, y, z);
                        player.spawnParticle(location, Particle.SPELL_WITCH, 3, 0, 0, 0, 0.1f);
                        location.subtract(x, y, z);

                        // Sound Fx
                        final float pitch = 2.0f - (1.5f / maxWindupTime * windupTime);

                        player.playSound(Sound.BLOCK_LEVER_CLICK, pitch);
                        player.playSound(Sound.ENTITY_ENDER_DRAGON_FLAP, pitch);
                        player.playSound(Sound.ENTITY_ENDER_DRAGON_HURT, pitch);

                        player.sendSubtitle(builder.toString(), 0, 10, 0);

                        --windupTime;
                        return;
                    }

                    cancel();
                    player.sendSubtitle("&aTeleporting...", 0, 20, 10);
                    final Location playerLocation = player.getLocation();

                    BukkitUtils.mergePitchYaw(playerLocation, location);
                    Geometry.drawLine(playerLocation, location, 0.25f, new WorldParticle(Particle.PORTAL));

                    startCooldown(player);

                    player.teleport(location);
                    player.playWorldSound(Sound.ENTITY_ENDERMAN_TELEPORT, 1.0f);
                    targetLocation.remove(player);

                    new EnderPearlTeleportEvent(player, location).call();
                }
            }.runTaskTimer(0, 1);

            return Response.AWAIT;
        }

        private boolean hasLocation(GamePlayer player) {
            return targetLocation.containsKey(player);
        }

    }

}
