package me.hapyl.fight.game.talents.archive.nightmare;

import me.hapyl.fight.game.Manager;
import me.hapyl.fight.game.Response;
import me.hapyl.fight.game.talents.Talent;
import me.hapyl.fight.util.Utils;
import me.hapyl.fight.util.displayfield.DisplayField;
import me.hapyl.spigotutils.module.player.PlayerLib;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerLeashEntityEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import javax.annotation.Nonnull;

public class ShadowShift extends Talent implements Listener {

    @DisplayField private final int immobilizationDuration = 20;

    public ShadowShift() {
        super("Shadow Shift", """
                Instantly teleport behind player you're looking at to strike from behind.
                                        
                You will lose ability to move for a short duration.
                """, Type.COMBAT);

        setItem(Material.LEAD);
        setCooldown(200);
    }

    @Override
    public Response execute(Player player) {
        final TargetLocation targetLocation = getLocationAndCheck0(player, 50.0d, 0.95d);

        if (targetLocation.getError() != ErrorCode.OK) {
            PlayerLib.playSound(player, Sound.ENTITY_ENDERMAN_TELEPORT, 0.0f);
            return Response.error(targetLocation.getError().getErrorMessage());
        }

        player.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, immobilizationDuration, 20));
        player.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, immobilizationDuration, 20));
        player.addPotionEffect(new PotionEffect(PotionEffectType.JUMP, immobilizationDuration, 250));

        final Location location = targetLocation.getLocation();
        player.teleport(location);
        PlayerLib.playSound(location, Sound.ENTITY_ENDERMAN_SCREAM, 1.0f);
        PlayerLib.spawnParticle(location, Particle.EXPLOSION_NORMAL, 3, 0.1d, 0.1d, 0.1d, 0.04f);

        return Response.OK;
    }

    public TargetLocation getLocationAndCheck0(Player player, double maxDistance, double dot) {
        final LivingEntity target = Utils.getTargetEntity(player, maxDistance, dot, player::hasLineOfSight);

        if (target == null) {
            return new TargetLocation(null, null, ErrorCode.NO_TARGET);
        }

        final Location behind = target.getLocation().add(target.getLocation().getDirection().multiply(-1).setY(0.0d));
        behind.setYaw(behind.getYaw());
        behind.setPitch(behind.getPitch());

        if (behind.getBlock().getType().isOccluding()) {
            return new TargetLocation(null, null, ErrorCode.OCCLUDING);
        }
        else {
            return new TargetLocation(target, behind, ErrorCode.OK);
        }
    }

    @EventHandler()
    public void handleEntityLeash(PlayerLeashEntityEvent ev) {
        if (Manager.current().isGameInProgress()) {
            ev.setCancelled(true);
        }
    }

    public static class TargetLocation {

        private final LivingEntity entity;
        private final Location location;
        private final ErrorCode error;

        TargetLocation(LivingEntity entity, Location location, ErrorCode error) {
            this.entity = entity;
            this.location = location;
            this.error = error;
        }

        @Nonnull
        public LivingEntity getEntity() {
            if (error != ErrorCode.OK) {
                throw new IllegalStateException("check for error before getting entity!");
            }

            return entity;
        }

        @Nonnull
        public Location getLocation() {
            if (error != ErrorCode.OK) {
                throw new IllegalStateException("check for error before getting location!");
            }

            return location;
        }

        @Nonnull
        public ErrorCode getError() {
            return error;
        }
    }

    public enum ErrorCode {

        NO_TARGET("No valid target!"),
        NO_LOS("No line of sight with target!"),
        OCCLUDING("Location is not safe!"),
        OK("");

        private final String errorMessage;

        ErrorCode(String s) {
            this.errorMessage = s;
        }

        public String getErrorMessage() {
            return errorMessage;
        }
    }


}
