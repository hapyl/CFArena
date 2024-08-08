package me.hapyl.fight.game.talents.shaman;

import me.hapyl.fight.game.achievement.Achievements;
import me.hapyl.fight.game.damage.EnumDamageCause;
import me.hapyl.fight.game.entity.GamePlayer;
import me.hapyl.fight.game.talents.shaman.resonance.TotemResonance;
import me.hapyl.fight.game.task.TickingGameTask;
import me.hapyl.fight.util.Collect;
import me.hapyl.eterna.module.block.display.DisplayEntity;
import me.hapyl.eterna.module.entity.Entities;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.ArmorStand;
import org.bukkit.util.Vector;

import javax.annotation.Nonnull;

public class Totem extends TickingGameTask {

    private final TotemTalent talent;
    private final GamePlayer player;
    private final TotemResonance resonance;

    private final ArmorStand stand;
    private final DisplayEntity displayEntity;

    private boolean landed;

    public Totem(TotemTalent talent, GamePlayer player, TotemResonance resonance) {
        this.talent = talent;
        this.player = player;
        this.resonance = resonance;

        final Location location = player.getMidpointLocation();
        location.setPitch(0.0f);

        this.displayEntity = resonance.getDisplayData().spawnInterpolated(location);

        player.getTeam().glowEntity(displayEntity);

        stand = Entities.ARMOR_STAND.spawn(location, self -> {
            self.setInvisible(true);
            self.setSmall(true);
            self.setGravity(true);
            self.setInvulnerable(true);
            self.setSilent(true);

            self.addPassenger(displayEntity.getHead());
        });

        final Vector vector = player.getDirection()
                .add(player.getVelocity().normalize().setY(talent.verticalVelocity))
                .multiply(talent.velocity);

        stand.setVelocity(vector);
        runTaskTimer(0, 1);
    }

    @Nonnull
    public Location getLocation() {
        return stand.getLocation().add(0, 1, 0);
    }

    @Nonnull
    public GamePlayer getPlayer() {
        return player;
    }

    @Override
    public void onTaskStop() {
        stand.remove();

        if (displayEntity != null) {
            displayEntity.remove();
        }

        // Fx
        final Location location = getLocation();

        player.playWorldSound(location, Sound.ENTITY_IRON_GOLEM_DAMAGE, 0.25f);
        player.spawnWorldParticle(location, Particle.CRIT, 25, 0.25d, 0.5d, 0.25d, 0.5f);

        // Explode
        if (player.random.nextDouble() < talent.chanceToExplode) {
            Collect.nearbyEntities(location, 2.5d).forEach(entity -> {
                if (player.isSelfOrTeammate(entity)) {
                    return;
                }

                entity.damageNoKnockback(talent.explodeDamage, player, EnumDamageCause.TOTEM);
            });

            player.playWorldSound(location, Sound.ENTITY_GENERIC_EXPLODE, 1);
        }
    }

    public void onLand() {
        Collect.nearbyEntities(getLocation(), 2).forEach(entity -> {
            if (player.isSelfOrTeammate(entity)) {
                return;
            }

            entity.damage(1, player, EnumDamageCause.TOTEM);
        });

        // Fx
        player.playWorldSound(getLocation(), Sound.ENTITY_IRON_GOLEM_REPAIR, 1);
    }

    @Override
    public void run(int tick) {
        if (!stand.isOnGround()) {
            return;
        }

        if (!landed) {
            landed = true;
            onLand();
        }

        // Remove
        if (shouldRemove()) {
            talent.getTotems(player).remove(this);
            cancel();
            return;
        }

        if (tick > 0 && modulo(talent.interval)) {
            resonance.resonate(this);
        }
    }

    public boolean shouldRemove() {
        final double y = getLocation().getY();

        if (y <= 1) {
            Achievements.TOTEM_OUT_OF_WORLD.complete(player);
            return true;
        }

        return getTick() > talent.getDuration();
    }

    @Override
    public String toString() {
        return "Totem@" + Integer.toHexString(hashCode());
    }

    public boolean isOnGround() {
        return stand.isOnGround();
    }
}
