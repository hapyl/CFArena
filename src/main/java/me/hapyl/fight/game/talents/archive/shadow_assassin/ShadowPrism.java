package me.hapyl.fight.game.talents.archive.shadow_assassin;

import me.hapyl.fight.game.Response;
import me.hapyl.fight.game.entity.GamePlayer;
import me.hapyl.fight.game.talents.Talent;
import me.hapyl.fight.game.task.GameTask;
import me.hapyl.fight.util.Nulls;
import me.hapyl.fight.util.collection.player.PlayerMap;
import me.hapyl.fight.util.displayfield.DisplayField;
import me.hapyl.spigotutils.module.entity.Entities;
import me.hapyl.spigotutils.module.player.PlayerLib;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.ArmorStand;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scoreboard.Team;
import org.bukkit.util.Vector;

import javax.annotation.Nonnull;

public class ShadowPrism extends Talent {

    @DisplayField(name = "Cooldown on Deploy") private final int deployCd = 20;
    @DisplayField(name = "Cooldown on Teleport") private final int teleportCd = 400;
    @DisplayField private final int windupTime = 20;
    @DisplayField private final double prismTravelSpeed = 0.4d;

    private final PlayerMap<ArmorStand> playerPrism = PlayerMap.newMap();

    public ShadowPrism() {
        super("Shadow Prism", """
                Deploy a teleportation orb that travels in straight line.
                                
                &e&lLOOK AT BLOCK &7to place it at fixed block and prevent it from travelling.
                                
                Use again to teleport to the orb after a short windup.
                                
                &b;;This ability is invisible to your opponents!
                """);

        setTexture("83ed4ce23933e66e04df16070644f7599eeb55307f7eafe8d92f40fb3520863c");
    }

    @Override
    public void onStop() {
        playerPrism.clear();
    }

    @Override
    public void onDeath(@Nonnull GamePlayer player) {
        Nulls.runIfNotNull(getPrism(player), ArmorStand::remove);
        playerPrism.remove(player);
    }

    public ArmorStand getPrism(GamePlayer player) {
        return playerPrism.get(player);
    }

    public boolean hasPrism(GamePlayer player) {
        return playerPrism.containsKey(player);
    }

    @Override
    public Response execute(@Nonnull GamePlayer player) {
        final ArmorStand prism = getPrism(player);

        // Deploy Prism
        final Location playerLocation = player.getLocation();
        if (prism == null) {
            final Block targetBlock = player.getTargetBlockExact(5);
            final boolean isStill = targetBlock != null;
            Location spawnLocation = playerLocation;

            if (targetBlock != null) {
                spawnLocation = targetBlock.getRelative(BlockFace.UP).getLocation().add(0.5d, 0.0d, 0.5d);
            }

            if (!spawnLocation.getBlock().getType().isAir()) {
                return Response.error("Invalid block.");
            }

            final ArmorStand entity = Entities.ARMOR_STAND.spawn(spawnLocation, self -> {
                self.setVisible(false);
                self.setSilent(true);
                self.setInvulnerable(true);
                self.setSmall(true);
                self.getLocation().setYaw(playerLocation.getYaw());

                if (self.getEquipment() != null) {
                    self.getEquipment().setHelmet(this.getItem());
                }

                self.setVisibleByDefault(false);
            });

            playerPrism.put(player, entity);
            startCd(player, deployCd); // fix instant use

            // Hide prism for everyone but player
            //Visibility.of(entity, player);
            player.showEntity(entity);

            // Add glowing
            handleGlowing(entity, player);

            // Move task
            new GameTask() {
                private int tick = 100;

                @Override
                public void run() {
                    if (tick < 0 || entity.isDead()) {
                        this.cancel();
                        return;
                    }

                    if (!isStill) {
                        final Vector direction = entity.getLocation().getDirection();
                        entity.setVelocity(new Vector(direction.getX(), -1, direction.getZ()).normalize().multiply(prismTravelSpeed));
                    }

                    --tick;

                    // Fx
                    player.spawnParticle(entity.getLocation(), Particle.PORTAL, 10, 0.5d, 0.5d, 0.5d, 0.05f);
                }
            }.runTaskTimer(0, 2);

            // Fx
            player.playSound(Sound.ENTITY_SHULKER_AMBIENT, 1.75f);

            return Response.OK;
        }

        // Teleport to Prism
        startCd(player, 9999);
        final float pitchPerTick = 2.0f / windupTime;

        player.addPotionEffect(PotionEffectType.SLOW, windupTime, 100);
        player.addPotionEffect(PotionEffectType.SLOW_FALLING, windupTime, 0);

        GameTask.runTaskTimerTimes((task, i) -> {
            final Location eyeLocation = player.getEyeLocation();
            if (i != 0) {
                PlayerLib.spawnParticle(eyeLocation, Particle.CRIT_MAGIC, 10, 0.5d, 0.5d, 0.5d, 0.0f);
                PlayerLib.spawnParticle(eyeLocation, Particle.PORTAL, 10, 0.5d, 0.5d, 0.5d, 0.0f);
                PlayerLib.playSound(eyeLocation, Sound.ENTITY_SHULKER_AMBIENT, 2.0f - (i * pitchPerTick));
                return;
            }

            startCd(player, teleportCd);

            final Location prismLocation = prism.getLocation();
            final Location location = new Location(
                    prismLocation.getWorld(),
                    prismLocation.getBlockX() + 0.5d,
                    prismLocation.getBlockY(),
                    prismLocation.getBlockZ() + 0.5d,
                    playerLocation.getYaw(),
                    playerLocation.getPitch()
            );

            player.teleport(location);
            prism.remove();
            playerPrism.remove(player);

            // Fx
            player.addPotionEffect(PotionEffectType.BLINDNESS, 20, 1);
            player.playSound(Sound.ENTITY_ENDERMAN_TELEPORT, 0.75f);
        }, 1, windupTime);

        return Response.OK;
    }

    private void handleGlowing(ArmorStand prism, GamePlayer player) {
        final Team team = getGlowingTeam(player);
        team.addEntry(prism.getUniqueId().toString());
        prism.setGlowing(true);
    }

    private Team getGlowingTeam(GamePlayer player) {
        final Team team = player.getOrCreateScoreboardTeam("ShadowPrismGlowing");
        team.setColor(ChatColor.DARK_PURPLE);

        return team;
    }

}
