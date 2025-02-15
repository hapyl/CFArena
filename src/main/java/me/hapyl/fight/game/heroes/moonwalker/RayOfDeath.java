package me.hapyl.fight.game.heroes.moonwalker;

import me.hapyl.fight.game.damage.DamageCause;
import me.hapyl.fight.game.entity.GamePlayer;
import me.hapyl.fight.game.entity.LivingGameEntity;
import me.hapyl.fight.game.task.player.PlayerTickingGameTask;
import me.hapyl.fight.util.Collect;
import me.hapyl.fight.util.DirectionalMatrix;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Sound;

public class RayOfDeath extends PlayerTickingGameTask {

    private final GamePlayer player;
    private final MoonwalkerData data;
    private final MoonwalkerWeapon.RayOfDeathAbility ability;

    private final int slot;

    public RayOfDeath(GamePlayer player, MoonwalkerData data, MoonwalkerWeapon.RayOfDeathAbility ability) {
        super(player);

        this.player = player;
        this.slot = player.getHeldSlotRaw();
        this.data = data;
        this.ability = ability;

        runTaskTimer(ability.firstShotDelay, 1);
    }

    @Override
    public void onFirstTick() {
        // Fx
        player.playWorldSound(Sound.ENTITY_ZOMBIE_VILLAGER_CONVERTED, 2.0f);
    }

    public void onStep(Location location) {
        // Fx
        player.spawnWorldParticle(location, Particle.WITCH, 1);
    }

    public void onHit(LivingGameEntity entity) {
        entity.setLastDamager(player);
        entity.damage(ability.damage, DamageCause.RAY_OF_DEATH);
    }

    @Override
    public void run(int tick) {
        // Make sure still holding a weapon
        final int heldSlot = player.getHeldSlotRaw();

        if (heldSlot != slot) {
            endRay();
            return;
        }

        data.weaponEnergy -= Math.min(data.weaponEnergy, ability.energyDrainPerTick);

        if (data.weaponEnergy <= 0) {
            endRay();
            return;
        }

        // Ray the death
        final Location location = player.getEyeLocation();
        final DirectionalMatrix matrix = player.getLookAlongMatrix();

        for (double d = 0.0d; d < ability.maxDistance; d += 0.5) {
            final double x = Math.sin(d) * 0.5d;
            final double y = Math.cos(d) * 0.5d;

            // Only damage the first ray
            matrix.transformLocation(location, x, y, d, then -> {
                Collect.nearbyEntities(location, 1.0d).forEach(entity -> {
                    if (player.isSelfOrTeammate(entity)) {
                        return;
                    }

                    onHit(entity);
                });

                onStep(location);
            });
            matrix.transformLocation(location, y, x, d, then -> onStep(location));
        }

        // Fx
        if (modulo(3)) {
            player.playWorldSound(Sound.ENTITY_ENDERMAN_AMBIENT, 1.75f);
            player.playWorldSound(Sound.ENTITY_ENDERMAN_HURT, 1.75f);
        }
    }

    private void endRay() {
        ability.weapon.rayOfDeathMap.remove(player, this);
        cancel();

        final int cooldown = (getTick() + 1) * ability.cdPerSecondActive;
        ability.startCooldown(player, cooldown);
    }

}
