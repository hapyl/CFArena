package me.hapyl.fight.game.talents.storage.shadowassassin;

import me.hapyl.fight.game.Response;
import me.hapyl.fight.game.talents.Talent;
import me.hapyl.fight.game.task.GameTask;
import me.hapyl.fight.util.Nulls;
import me.hapyl.spigotutils.module.entity.Entities;
import me.hapyl.spigotutils.module.player.PlayerLib;
import me.hapyl.spigotutils.module.reflect.visibility.Visibility;
import me.hapyl.spigotutils.module.util.BukkitUtils;
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

    private final int deployCd = 20;
    private final int teleportCd = 400;
    private final double prismTravelSpeed = 0.4d;
    private final Map<Player, ArmorStand> playerPrism = new HashMap<>();

    public ShadowPrism() {
        super("Shadow Prism");
        this.setDescription(
                "Deploy a teleportation orb that travels in straight line. Use again to teleport to the orb.__&e&lLOOK AT BLOCK &7to place it at fixed block and prevent it from travelling.____This ability is invisible to your opponents!");
        this.setItem(
                "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvODNlZDRjZTIzOTMzZTY2ZTA0ZGYxNjA3MDY0NGY3NTk5ZWViNTUzMDdmN2VhZmU4ZDkyZjQwZmIzNTIwODYzYyJ9fX0=");

        this.addExtraInfo("Cooldown on Deploy: &l%ss", BukkitUtils.roundTick(deployCd));
        this.addExtraInfo("Cooldown on Teleport: &l%ss", BukkitUtils.roundTick(teleportCd));
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
        if (prism == null) {
            final Block targetBlock = player.getTargetBlockExact(5);
            final boolean isStill = targetBlock != null;
            Location spawnLocation = player.getLocation();
            if (targetBlock != null) {
                spawnLocation = targetBlock.getRelative(BlockFace.UP).getLocation().add(0.5d, 0.0d, 0.5d);
            }

            final ArmorStand entity = Entities.ARMOR_STAND.spawn(spawnLocation, me -> {
                me.setVisible(false);
                me.setSilent(true);
                me.setInvulnerable(true);
                me.setSmall(true);
                me.getLocation().setYaw(player.getLocation().getYaw());
                if (me.getEquipment() != null) {
                    me.getEquipment().setHelmet(this.getItem());
                }
            });

            playerPrism.put(player, entity);
            startCd(player, deployCd); // fix instant use

            // Hide prism for everyone but player
            Visibility.of(entity, player);
            //            Reflect.hideEntity(
            //                    entity,
            //                    Manager
            //                            .current()
            //                            .getCurrentGame()
            //                            .getAlivePlayersAsPlayers(gp -> !gp.compare(player))
            //                            .toArray(new Player[] {})
            //            );

            // add glowing
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
        startCd(player, teleportCd);
        final Location location = prism.getLocation().add(0.5d, 0.0d, 0.5d);
        location.setYaw(player.getLocation().getYaw());
        location.setPitch(player.getLocation().getPitch());

        player.teleport(location);

        prism.remove();
        playerPrism.remove(player);

        // fx
        PlayerLib.addEffect(player, PotionEffectType.BLINDNESS, 20, 1);
        PlayerLib.playSound(player, Sound.ENTITY_ENDERMAN_TELEPORT, 0.75f);

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
