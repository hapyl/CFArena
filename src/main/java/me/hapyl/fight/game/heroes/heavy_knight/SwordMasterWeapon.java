package me.hapyl.fight.game.heroes.heavy_knight;

import me.hapyl.eterna.module.registry.Key;
import me.hapyl.fight.game.Response;
import me.hapyl.fight.game.effect.EffectType;
import me.hapyl.fight.game.entity.GamePlayer;
import me.hapyl.fight.game.heroes.HeroRegistry;
import me.hapyl.fight.game.talents.TalentRegistry;
import me.hapyl.fight.game.task.TickingGameTask;
import me.hapyl.fight.game.weapons.Weapon;
import me.hapyl.fight.game.weapons.ability.Ability;
import me.hapyl.fight.game.weapons.ability.AbilityType;
import me.hapyl.fight.registry.Registries;
import me.hapyl.fight.util.Collect;
import me.hapyl.fight.util.displayfield.DisplayField;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.util.Vector;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class SwordMasterWeapon extends Weapon {
    
    public SwordMasterWeapon() {
        super(Material.NETHERITE_SWORD, Key.ofString("shaman_weapon"));
        
        setName("Basta");
        setDescription("""
                       A royal claymore.
                       &8&o;;This thing was too big to be called a sword.
                       """);
        
        setDamage(8.0d);
        
        setAbility(AbilityType.RIGHT_CLICK, new LeapAbility());
    }
    
    private class LeapAbility extends Ability {
        
        @DisplayField private final double leapMagnitudeY = 0.35d;
        @DisplayField private final double leapMagnitude = 1.125d;
        
        @DisplayField private final int maxAirTime = 100;
        
        @DisplayField private final double pullStrength = 0.25;
        @DisplayField private final double pullRadius = 8;
        @DisplayField private final int dazeDuration = 80;
        
        LeapAbility() {
            super(
                    "Leap", """
                            Leap forward.
                            
                            &8&o;;You won't take fall damage for a short duration after leaping.
                            """
            );
            
            setCooldownSec(6);
        }
        
        @Nullable
        @Override
        public Response execute(@Nonnull GamePlayer player) {
            final SwordMasterData data = HeroRegistry.SWORD_MASTER.getPlayerData(player);
            final Location location = player.getLocation();
            final Vector vector = location.getDirection().normalize().multiply(leapMagnitude).setY(leapMagnitudeY);
            
            player.setVelocity(vector);
            player.addEffect(EffectType.FALL_DAMAGE_RESISTANCE, maxAirTime);
            
            if (data.empowered != null) {
                data.empowered.cancel();
                data.empowered = null;
                
                new TickingGameTask() {
                    @Override
                    public void run(int tick) {
                        // Ignore first ticks where player is still on ground
                        if (tick < 5) {
                            return;
                        }
                        
                        if (player.isDeadOrRespawning() || tick > maxAirTime) {
                            cancel();
                            return;
                        }
                        
                        if (player.isOnGround()) {
                            cancel();
                            executeEmpowered(player);
                        }
                    }
                }.runTaskTimer(0, 1);
            }
            
            // Fx
            player.playWorldSound(Sound.ENTITY_CAMEL_DASH, 0.75f);
            player.playWorldSound(Sound.BLOCK_NETHERITE_BLOCK_BREAK, 0.75f);
            
            return Response.OK;
        }
        
        private void executeEmpowered(GamePlayer player) {
            SwordMasterWeapon.this.give(player);
            TalentRegistry.SLASH.stopCooldown(player);
            
            // Affect
            final Location location = player.getLocationInFront(1.5);
            final Location pullLocation = location.clone();
            
            Collect.nearbyEntities(location, pullRadius, player::isNotSelfOrTeammateOrHasEffectResistance)
                   .forEach(entity -> {
                       final Location entityLocation = entity.getLocation();
                       final double distance = pullLocation.distance(entityLocation) * pullStrength;
                       final Vector pullVector = pullLocation.toVector().subtract(entityLocation.toVector()).normalize().multiply(distance).setY(0.1);
                       
                       entity.setVelocity(pullVector);
                       entity.addEffect(EffectType.DAZE, dazeDuration, player);
                   });
            
            // Fx
            Registries.cosmetics().GROUND_PUNCH.playAnimation(location, 2);
        }
    }
}
