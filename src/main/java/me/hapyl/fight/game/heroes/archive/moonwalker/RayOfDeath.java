package me.hapyl.fight.game.heroes.archive.moonwalker;

import me.hapyl.fight.game.damage.EnumDamageCause;
import me.hapyl.fight.game.entity.GamePlayer;
import me.hapyl.fight.game.task.TickingGameTask;
import me.hapyl.fight.util.Collect;
import me.hapyl.fight.util.DirectionalMatrix;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Sound;

public class RayOfDeath extends TickingGameTask {

    private final GamePlayer player;
    private final int slot;
    private final MoonwalkerWeapon.ChargeAbility ability;
    private final int duration;

    public RayOfDeath(GamePlayer player, MoonwalkerWeapon.ChargeAbility ability) {
        this.player = player;
        this.slot = player.getHeldSlotRaw();
        this.ability = ability;
        this.duration = ability.getDuration();

        runTaskTimer(0, 1);
    }

    @Override
    public void onFirstTick() {
        // Fx
        player.playWorldSound(Sound.ENTITY_ZOMBIE_VILLAGER_CONVERTED, 2.0f);
    }

    public void onStep(Location location) {
        Collect.nearbyEntities(location, 1.0d).forEach(entity -> {
            if (player.isSelfOrTeammate(entity)) {
                return;
            }

            entity.setLastDamager(player);
            entity.damageTick(ability.damage, EnumDamageCause.RAY_OF_DEATH, ability.damagePeriod);
        });

        // Fx
        player.spawnWorldParticle(location, Particle.SPELL_WITCH, 1);
    }

    @Override
    public void run(int tick) {
        // Make sure still holding a weapon
        final int heldSlot = player.getHeldSlotRaw();

        if (tick > duration || heldSlot != slot) {
            ability.startCooldown(player);
            cancel();
            return;
        }

        // Ray the death
        final Location location = player.getEyeLocation();
        final DirectionalMatrix matrix = player.getLookAlongMatrix();

        for (double d = 0.0d; d < ability.maxRange; d += ability.step) {
            final double x = Math.sin(d) * 0.5d;
            final double y = Math.cos(d) * 0.5d;

            matrix.transformLocation(location, x, y, d, then -> onStep(location));
            matrix.transformLocation(location, y, x, d, then -> onStep(location));
        }

        // Fx
        if (modulo(3)) {
            player.playWorldSound(Sound.ENTITY_ENDERMAN_AMBIENT, 1.75f);
            player.playWorldSound(Sound.ENTITY_ENDERMAN_HURT, 1.75f);
        }
    }
}
