package me.hapyl.fight.game.talents.archive.freazly;

import me.hapyl.fight.game.entity.GamePlayer;
import me.hapyl.fight.game.Response;
import me.hapyl.fight.game.effect.GameEffectType;
import me.hapyl.fight.game.talents.Talent;
import me.hapyl.fight.game.task.GameTask;
import me.hapyl.fight.util.Collect;
import me.hapyl.spigotutils.module.chat.Chat;
import me.hapyl.spigotutils.module.math.Geometry;
import me.hapyl.spigotutils.module.math.geometry.Quality;
import me.hapyl.spigotutils.module.math.geometry.WorldParticle;
import me.hapyl.spigotutils.module.player.PlayerLib;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Snowball;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import javax.annotation.Nonnull;
import java.util.*;

public class IceCone extends Talent implements Listener {

    private final Set<Location> locationSet = new HashSet<>();
    private final Map<Player, Snowball> snowballMap = new HashMap<>();

    public IceCone() {
        super("Ice Cone", """
                Launch a snowball in front of you.
                                        
                Upon hitting an opponent, cages them into an ice cone.
                Upon hitting a block, it creates a slowing aura for short duration.
                """);
        setItem(Material.SNOWBALL);
        setCooldown(400);
    }

    @EventHandler()
    public void handleSnowballHit(ProjectileHitEvent ev) {
        if (ev.getEntity() instanceof Snowball snowball && snowball.getShooter() instanceof Player player &&
                snowballMap.get(player) == snowball) {

            final Entity entity = ev.getHitEntity();
            final Block hitBlock = ev.getHitBlock();

            if (entity instanceof Player victim) {
                createBlob(victim);

                Chat.sendMessage(victim, "&aYou got hit by %s's snowball! &e&lPUNCH &aIce to remove it!", player.getName());
                Chat.sendMessage(player, "&aYour snowball hit %s!", victim.getName());
                PlayerLib.playSound(victim.getLocation(), Sound.ENTITY_ZOMBIE_VILLAGER_CURE, 2.0f);
                PlayerLib.addEffect(victim, PotionEffectType.SLOW, 40, 2);
                PlayerLib.addEffect(victim, PotionEffectType.BLINDNESS, 30, 2);

                return;
            }

            if (hitBlock != null) {
                final Location location = hitBlock.getRelative(BlockFace.UP).getLocation().add(0.5d, 0.0d, 0.5);

                new GameTask() {
                    private int tick = 60;

                    @Override
                    public void run() {
                        if (tick < 0) {
                            this.cancel();
                            return;
                        }

                        Collect.nearbyEntities(location, 4.0d).forEach(entity -> {
                            entity.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 10, 3));

                            if (entity instanceof Player player) {
                                GamePlayer.getPlayer(player).addEffect(GameEffectType.SLOWING_AURA, 60, true);
                            }
                        });

                        Geometry.drawCircle(location, 4.0d, Quality.HIGH, new WorldParticle(Particle.BLOCK_CRACK) {
                            @Override
                            public void draw(@Nonnull Location location) {
                                Objects.requireNonNull(location.getWorld())
                                        .spawnParticle(
                                                this.getParticle(),
                                                location,
                                                1,
                                                0.0d,
                                                0.0d,
                                                0.0d,
                                                0.0f,
                                                Material.ICE.createBlockData()
                                        );
                            }
                        });

                        --tick;
                    }
                }.runTaskTimer(0, 1);
            }

            Chat.sendMessage(player, "&aYour snowball hit a block and created slowing aura!");
        }
    }

    private void createBlob(Player player) {
        final BlockData data = Material.ICE.createBlockData();
        final Location location = player.getLocation();

        player.sendBlockChange(cloneAndSaveLoc(location, 1, 0, 0), data);
        player.sendBlockChange(cloneAndSaveLoc(location, -1, 0, 0), data);
        player.sendBlockChange(cloneAndSaveLoc(location, 1, 1, 0), data);
        player.sendBlockChange(cloneAndSaveLoc(location, -1, 1, 0), data);

        player.sendBlockChange(cloneAndSaveLoc(location, 0, 0, 1), data);
        player.sendBlockChange(cloneAndSaveLoc(location, 0, 0, -1), data);
        player.sendBlockChange(cloneAndSaveLoc(location, 0, 1, 1), data);
        player.sendBlockChange(cloneAndSaveLoc(location, 0, 1, -1), data);

        player.sendBlockChange(cloneAndSaveLoc(location, 0, 2, 0), data);

        // fix player position
        player.teleport(location.clone().add(0.5d, 0.0d, 0.5d));
    }

    @Override
    public void onStop() {
        locationSet.forEach(location -> {
            location.getBlock().getState().update(true, false);
        });
        locationSet.clear();
    }


    private Location cloneAndSaveLoc(Location location, double x, double y, double z) {
        final Location loc = location.clone().add(x, y, z);
        locationSet.add(loc);
        return loc;
    }

    @Override
    public Response execute(Player player) {
        final Snowball snowball = player.launchProjectile(Snowball.class);

        snowball.setShooter(player);
        snowballMap.put(player, snowball);

        PlayerLib.playSound(player, Sound.ENTITY_SNOWBALL_THROW, 1.0f);

        return Response.OK;
    }
}
