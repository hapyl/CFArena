package me.hapyl.fight.game.talents.storage.shadowassassin;

import me.hapyl.fight.Main;
import me.hapyl.fight.game.Response;
import me.hapyl.fight.game.talents.Talent;
import me.hapyl.fight.game.task.GameTask;
import me.hapyl.fight.util.Nulls;
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
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;
import org.bukkit.util.Vector;

import java.util.HashMap;
import java.util.Map;

public class ShadowPrism extends Talent {

    @DisplayField(name = "Cooldown on Deploy") private final int deployCd = 20;
    @DisplayField(name = "Cooldown on Teleport") private final int teleportCd = 400;
    @DisplayField private final int windupTime = 20;
    private final double prismTravelSpeed = 0.4d;
    private final Map<Player, ArmorStand> playerPrism = new HashMap<>();

    public ShadowPrism() {
        super("Shadow Prism");

        addDescription("Deploy a teleportation orb that travels in straight line.");
        addNlDescription("&e&lLOOK AT BLOCK &7to place it at fixed block and prevent it from travelling.");
        addNlDescription("Use again to teleport to the orb after a short windup.");
        addNlDescription("&bThis ability is invisible to your opponents!");

        setItem(
                "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvODNlZDRjZTIzOTMzZTY2ZTA0ZGYxNjA3MDY0NGY3NTk5ZWViNTUzMDdmN2VhZmU4ZDkyZjQwZmIzNTIwODYzYyJ9fX0="
        );
    }

    @Override
    public void onStop() {
        playerPrism.clear();
    }

    @Override
    public void onDeath(Player player) {
        Nulls.runIfNotNull(getPrism(player), ArmorStand::remove);
        playerPrism.remove(player);
    }

    public ArmorStand getPrism(Player player) {
        return playerPrism.get(player);
    }

    public boolean hasPrism(Player player) {
        return playerPrism.containsKey(player);
    }

    @Override
    public Response execute(Player player) {
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
            player.showEntity(Main.getPlugin(), entity);

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

                    // fx
                    PlayerLib.spawnParticle(player, entity.getLocation(), Particle.PORTAL, 10, 0.5d, 0.5d, 0.5d, 0.05f);

                }
            }.runTaskTimer(0, 2);

            // fx
            PlayerLib.playSound(player, Sound.ENTITY_SHULKER_AMBIENT, 1.75f);

            return Response.OK;
        }

        // Teleport to Prism
        startCd(player, 9999);
        final float pitchPerTick = 2.0f / windupTime;

        PlayerLib.addEffect(player, PotionEffectType.SLOW, windupTime, 100);
        PlayerLib.addEffect(player, PotionEffectType.SLOW_FALLING, windupTime, 0);

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
            PlayerLib.addEffect(player, PotionEffectType.BLINDNESS, 20, 1);
            PlayerLib.playSound(player, Sound.ENTITY_ENDERMAN_TELEPORT, 0.75f);
        }, 1, windupTime);

        return Response.OK;
    }

    private void handleGlowing(ArmorStand prism, Player player) {
        final Team team = getGlowingTeam(player);
        team.addEntry(prism.getUniqueId().toString());
        prism.setGlowing(true);
    }

    private Team getGlowingTeam(Player player) {
        final Scoreboard scoreboard = player.getScoreboard();
        Team team = scoreboard.getTeam("ShadowPrismGlowing");

        if (team == null) {
            team = scoreboard.registerNewTeam("ShadowPrismGlowing");
            team.setColor(ChatColor.DARK_PURPLE);
        }

        return team;
    }

}
