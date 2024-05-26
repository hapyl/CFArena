package me.hapyl.fight.game.talents.nightmare;

import me.hapyl.fight.game.Manager;
import me.hapyl.fight.game.Response;
import me.hapyl.fight.game.effect.Effects;
import me.hapyl.fight.game.entity.GamePlayer;
import me.hapyl.fight.game.entity.LivingGameEntity;
import me.hapyl.fight.game.heroes.Heroes;
import me.hapyl.fight.game.heroes.nightmare.Nightmare;
import me.hapyl.fight.game.heroes.nightmare.OmenDebuff;
import me.hapyl.fight.game.talents.TalentType;
import me.hapyl.fight.game.talents.Talent;
import me.hapyl.fight.util.Collect;
import me.hapyl.fight.util.displayfield.DisplayField;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerLeashEntityEvent;

import javax.annotation.Nonnull;

public class ShadowShift extends Talent implements Listener {

    @DisplayField private final int immobilizationDuration = 20;
    @DisplayField private final int omenDuration = 60;

    public ShadowShift() {
        super("Shadow Shift", """
                Instantly teleport behind your target entity to scare them from behind, applying &cOmen&7.
                                        
                You will lose the ability to move for a short duration.
                """);

        setType(TalentType.IMPAIR);
        setItem(Material.LEAD);
        setCooldown(200);
    }

    @Override
    public Response execute(@Nonnull GamePlayer player) {
        final TargetLocation targetLocation = getLocationAndCheck0(player, 50.0d, 0.95d);

        if (targetLocation.getError() != ErrorCode.OK) {
            player.playSound(Sound.ENTITY_ENDERMAN_TELEPORT, 0.0f);
            return Response.error(targetLocation.getError().getErrorMessage());
        }

        player.addEffect(Effects.BLINDNESS, 20, immobilizationDuration);
        player.addEffect(Effects.SLOW, 20, immobilizationDuration);
        player.addEffect(Effects.JUMP_BOOST, 250, immobilizationDuration);

        final Location location = targetLocation.getLocation();
        final LivingGameEntity entity = targetLocation.getEntity();
        final OmenDebuff debuff = Heroes.NIGHTMARE.getHero(Nightmare.class).getDebuff(player);

        player.teleport(location);
        debuff.setOmen(entity, omenDuration);

        // Fx
        player.playWorldSound(location, Sound.ENTITY_ENDERMAN_SCREAM, 1.0f);
        player.spawnParticle(location, Particle.POOF, 3, 0.1d, 0.1d, 0.1d, 0.04f);

        return Response.OK;
    }

    @Nonnull
    public TargetLocation getLocationAndCheck0(GamePlayer player, double maxDistance, double dot) {
        final LivingGameEntity target = Collect.targetEntityRayCast(player, maxDistance, dot, entity -> {
            return !player.isSelfOrTeammate(entity) && player.hasLineOfSight(entity);
        });

        if (target == null) {
            return new TargetLocation(null, null, ErrorCode.NO_TARGET);
        }

        final Location behind = target.getLocation().add(target.getLocation().getDirection().multiply(-1).setY(0.0d));
        behind.setYaw(behind.getYaw());
        behind.setPitch(behind.getPitch());

        if (behind.getBlock().getType().isOccluding()) {
            return new TargetLocation(null, null, ErrorCode.OCCLUDING);
        }

        return new TargetLocation(target, behind, ErrorCode.OK);
    }

    @EventHandler()
    public void handleEntityLeash(PlayerLeashEntityEvent ev) {
        if (Manager.current().isGameInProgress()) {
            ev.setCancelled(true);
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

    public static class TargetLocation {

        private final LivingGameEntity entity;
        private final Location location;
        private final ErrorCode error;

        TargetLocation(LivingGameEntity entity, Location location, ErrorCode error) {
            this.entity = entity;
            this.location = location;
            this.error = error;
        }

        @Nonnull
        public LivingGameEntity getEntity() {
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

            location.setPitch(0.0f);
            return location;
        }

        @Nonnull
        public ErrorCode getError() {
            return error;
        }
    }


}
