package me.hapyl.fight.game.heroes.shadow_assassin;

import me.hapyl.eterna.module.math.Tick;
import me.hapyl.eterna.module.registry.Key;
import me.hapyl.fight.game.attribute.AttributeType;
import me.hapyl.fight.game.attribute.ModifierSource;
import me.hapyl.fight.game.attribute.ModifierType;
import me.hapyl.fight.game.effect.EffectType;
import me.hapyl.fight.game.entity.GamePlayer;
import me.hapyl.fight.game.entity.LivingGameEntity;
import me.hapyl.fight.game.weapons.Weapon;
import me.hapyl.fight.game.weapons.ability.AbilityType;
import me.hapyl.fight.game.weapons.ability.DummyAbility;
import me.hapyl.fight.util.displayfield.DisplayField;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.util.Vector;

import javax.annotation.Nonnull;

public class ShadowAssassinWeapon extends Weapon {
    
    @DisplayField private final int cooldown = Tick.fromSeconds(20);
    @DisplayField private final double defenseReduction = -0.1d;
    @DisplayField private final double speedReduction = -50;
    @DisplayField private final int defenseReductionDuration = 60;
    
    private final ModifierSource modifierSource = new ModifierSource(Key.ofString("backstab"));
    
    public ShadowAssassinWeapon(ShadowAssassin hero) {
        super(Material.IRON_SWORD, Key.ofString("livid_dagger"));
        
        setName("Livid Dagger");
        setDescription("""
                       A dagger made of bad memories.
                       """);
        
        setDamage(8.0d);
        setAbility(AbilityType.BACK_STAB, new Backstab());
    }
    
    private class Backstab extends DummyAbility {
        
        public Backstab() {
            super(
                    "Shadow Stab", """
                                   Hit an enemy from behind to perform a shadow stab attack, reducing their %s and stunning them for a short time.
                                   """.formatted(AttributeType.DEFENSE)
            );
            
            setCooldown(cooldown);
        }
        
    }
    
    public void performBackStab(@Nonnull GamePlayer player, @Nonnull LivingGameEntity entity) {
        final Location location = entity.getLocation();
        final Vector vector = location.getDirection();
        
        entity.setVelocity(new Vector(vector.getX(), 0.1d, vector.getZ()).multiply(2.13f));
        entity.addEffect(EffectType.NAUSEA, 5, 40);
        
        entity.getAttributes().addModifier(
                modifierSource, defenseReductionDuration, player, modifier -> modifier
                        .of(AttributeType.DEFENSE, ModifierType.ADDITIVE, defenseReduction)
                        .of(AttributeType.SPEED, ModifierType.FLAT, speedReduction)
        );
        
        entity.sendMessage("&a%s stabbed you!".formatted(player.getName()));
        player.cooldownManager.setCooldown(this, cooldown);
        
        // Fx
        entity.playWorldSound(Sound.ENTITY_ZOMBIE_BREAK_WOODEN_DOOR, 0.65f);
        entity.spawnWorldParticle(Particle.CRIT, 10, 0.25d, 0.0d, 0.25d, 0.076f);
    }
    
}
