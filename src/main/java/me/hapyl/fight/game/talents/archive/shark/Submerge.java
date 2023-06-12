package me.hapyl.fight.game.talents.archive.shark;

import me.hapyl.fight.game.EnumDamageCause;
import me.hapyl.fight.game.GamePlayer;
import me.hapyl.fight.game.Response;
import me.hapyl.fight.game.talents.Talent;
import me.hapyl.fight.game.task.GameTask;
import me.hapyl.fight.util.Collect;
import me.hapyl.fight.util.Nulls;
import me.hapyl.fight.util.displayfield.DisplayField;
import me.hapyl.spigotutils.module.entity.Entities;
import me.hapyl.spigotutils.module.player.PlayerLib;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.EulerAngle;
import org.bukkit.util.Vector;

import java.util.Locale;

public class Submerge extends Talent {

    @DisplayField private final double damage = 10.0d;

    private final float[] FIN_OFFSET = { 0.2f, 1.8f, 0.2f };

    public Submerge() {
        super(
                "Submerge",
                "Swiftly submerge underground and dash forward revealing a hidden shark fin that deals damage and knocks back nearby enemies.",
                Material.PRISMARINE_SHARD
        );

        setDuration(20);
        setCooldownSec(5);
    }

    @Override
    public Response execute(Player player) {
        player.setGameMode(GameMode.SPECTATOR);

        final Location location = player.getLocation();
        location.setPitch(0.0f);

        final ArmorStand marker = Entities.ARMOR_STAND.spawn(location.subtract(FIN_OFFSET[0], FIN_OFFSET[1], FIN_OFFSET[2]), me -> {
            me.setSilent(true);
            me.setMarker(true);
            me.setInvisible(true);
            me.setInvulnerable(true);
            me.setHeadPose(new EulerAngle(Math.toRadians(90), Math.toRadians(45), Math.toRadians(90)));

            Nulls.runIfNotNull(me.getEquipment(), eq -> eq.setHelmet(new ItemStack(getMaterial())));
        });

        player.setInvulnerable(true);

        // Runnable
        new GameTask() {
            //private final Set<Block> affectedBlocks = Sets.newHashSet();
            private int maxTick = getDuration();

            private void raise(Location exitLocation) {
                exitLocation.add(0.0d, 1.5d, 0.0d);
                marker.remove();

                // Clear effect
                //affectedBlocks.forEach(block -> block.getState().update(true, false));
                //affectedBlocks.clear();

                player.setGameMode(GameMode.SURVIVAL);
                player.setInvulnerable(false);
                player.setFlying(false);
                player.setAllowFlight(false);

                player.teleport(exitLocation);
                player.setVelocity(exitLocation.getDirection().setY(0.5d));

                // Fx
                PlayerLib.playSound(exitLocation, Sound.AMBIENT_UNDERWATER_EXIT, 1.25f);
            }

            @Override
            public void run() {
                final Location entityLocation = marker.getLocation();
                final Location fixedLocation = entityLocation.clone().add(FIN_OFFSET[0], FIN_OFFSET[1], FIN_OFFSET[2]);
                final Vector vector = location.getDirection().setY(0.0d).multiply(0.5);
                final Location nextLocation = entityLocation.add(vector);

                // Next location (Kinda weird I know)
                final Location nextCheckLocation = nextLocation.clone().add(0.0, 2.0d, 0.0d);

                // if block is not passable then go back one step and raise
                if (!isPassable(nextCheckLocation.getBlock())) {
                    raise(entityLocation.subtract(vector));
                    cancel();
                    return;
                }

                // Ability always drops down
                if (nextCheckLocation.clone().getBlock().getRelative(BlockFace.DOWN).getType().isAir()) {
                    nextLocation.subtract(0.0d, 1.0d, 0.0d);
                }

                // Sync player
                player.teleport(nextLocation.clone().add(0.0d, 0.75d, 0.0d));
                marker.teleport(nextLocation);

                //final Block block = player.getEyeLocation().getBlock();
                //player.sendBlockChange(block.getLocation(), Material.WATER.createBlockData());
                //affectedBlocks.add(block);

                PlayerLib.spawnParticle(fixedLocation, Particle.BUBBLE_COLUMN_UP, 3, 0.2, 0.1, 0.2, 0.1f);
                PlayerLib.spawnParticle(fixedLocation, Particle.WATER_SPLASH, 1, 0.2, 0.1, 0.2, 0.1f);

                // Hit detection
                Collect.nearbyLivingEntities(fixedLocation, 1.0d).forEach(victim -> {
                    if (victim == player) {
                        return;
                    }

                    GamePlayer.damageEntity(victim, damage, player, EnumDamageCause.SUBMERGE);
                    victim.setVelocity(victim.getLocation().getDirection().multiply(-1.0d).setY(0.5d));
                });

                if (maxTick < 0) {
                    raise(nextLocation);
                    this.cancel();
                    return;
                }

                --maxTick;
            }

            private boolean isPassable(Block block) {
                final String name = block.getType().name().toLowerCase(Locale.ROOT);
                if (block.getType().isAir()) {
                    return true;
                }

                return stringHasAny(name, "carpet", "rail", "trapdoor", "button", "door");
            }

            private boolean stringHasAny(String string, String... any) {
                string = string.toLowerCase(Locale.ROOT);
                for (String str : any) {
                    if (string.contains(str.toLowerCase(Locale.ROOT))) {
                        return true;
                    }
                }
                return false;
            }

        }.runTaskTimer(0, 1);

        // Fx
        PlayerLib.playSound(location, Sound.WEATHER_RAIN_ABOVE, 1.75f);
        PlayerLib.playSound(location, Sound.AMBIENT_UNDERWATER_ENTER, 1.75f);

        return Response.OK;
    }

}
