package me.hapyl.fight.game.talents.dylan;

import me.hapyl.eterna.module.inventory.ItemBuilder;
import me.hapyl.eterna.module.registry.Key;
import me.hapyl.fight.game.Named;
import me.hapyl.fight.game.Response;
import me.hapyl.fight.game.damage.DamageCause;
import me.hapyl.fight.game.entity.GamePlayer;
import me.hapyl.fight.game.entity.LivingGameEntity;
import me.hapyl.fight.game.heroes.dylan.Dylan;
import me.hapyl.fight.game.heroes.dylan.DylanFamiliar;
import me.hapyl.fight.game.heroes.dylan.FamiliarAction;
import me.hapyl.fight.game.talents.TalentType;
import me.hapyl.fight.util.Collect;
import me.hapyl.fight.util.displayfield.DisplayField;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.util.Vector;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.function.Supplier;

public class WhelpAttack extends WhelpTalent {
    
    @DisplayField private final double maxDistance = 16;
    @DisplayField private final double damage = 3;
    
    @DisplayField private final int attackPeriod = 10;
    @DisplayField private final double attackRange = 1.5;
    
    public WhelpAttack(@Nonnull Key key) {
        super(key, "Whelp Attack!");
        
        setDescription("""
                       With your command, &3%1$s&7 rushes forwards, dealing &cdamage&7 in small AoE in front of it.
                       &8&o;;Being a nether-born, %1$s attacks naturally set enemies on fire.
                       
                       After &b{duration}&7, &3%1$s&7 returns back and gains one stack of %2$s.
                       """.formatted(Dylan.familiarName, Named.SCORCH));
        
        setTexture("6b0a6a5f6d0073185c950a1b57444ba6a87f4361cb221d87b0637903dc1c6e52");
        setType(TalentType.DAMAGE);
        
        setCooldownSec(6);
        setDurationSec(3);
    }
    
    @Nonnull
    @Override
    public ItemBuilder makeUnavailableBuilder(@Nonnull ItemBuilder builder) {
        return builder.setHeadTextureUrl("2d1d1da22f674b04facacc18d8471cb674d1a5b6421efc75584f1c47e41999ce");
    }
    
    @Nonnull
    @Override
    public Response execute(@Nonnull GamePlayer player, @Nonnull DylanFamiliar familiar) {
        // Find target
        final Location location = player.getEyeLocation();
        final Vector vector = location.getDirection().normalize().multiply(0.5);
        
        @Nonnull FamiliarAction target = destination(() -> location);
        
        for (double d = 0; d < maxDistance; d += 0.5) {
            // Check for block collision for previous location
            if (!location.getBlock().isPassable()) {
                break;
            }
            
            location.add(vector);
            
            // Check for entity collision first
            final LivingGameEntity nearestEntity = Collect.nearestEntity(location, 1.5, player::isNotSelfOrTeammate);
            
            if (nearestEntity != null) {
                target = destination(nearestEntity::getMidpointLocation);
                break;
            }
            
            target = destination(() -> location);
        }
        
        // Go towards target
        familiar.action(target, duration);
        
        // Fx
        player.playWorldSound(Sound.ENTITY_VEX_CHARGE, 0.75f);
        player.playWorldSound(Sound.ENTITY_VEX_DEATH, 1.25f);
        
        return Response.OK;
    }
    
    private FamiliarAction destination(Supplier<Location> supplier) {
        return new FamiliarAction() {
            @Nonnull
            @Override
            public Location destination() {
                return supplier.get();
            }
            
            @Override
            public void tick(@Nonnull GamePlayer player, @Nonnull DylanFamiliar familiar) {
                final DylanFamiliar.FamiliarEntity ezel = familiar.entity();
                final int attackPeriod = ezel.getAttributes().calculate().attackCooldown(WhelpAttack.this.attackPeriod);
                
                if (ezel.aliveTicks() % attackPeriod != 0) {
                    return;
                }
                
                final List<LivingGameEntity> entities = Collect.nearbyEntities(ezel.getLocation(), attackRange, player::isNotSelfOrTeammate);
                
                entities.forEach(entity -> {
                    entity.damageNoKnockback(damage, ezel, DamageCause.WHELP_ATTACK);
                    entity.setFireTicks(15);
                });
                
                // If ezel didn't damage anything, then don't spawn fx
                if (entities.isEmpty()) {
                    return;
                }
                
                // Fx
                ezel.spawnWorldParticle(Particle.SWEEP_ATTACK, 1, 0.3, 0.3, 0.3, 0.25f);
                ezel.spawnWorldParticle(Particle.LAVA, 1, 0.3, 0.3, 0.3, 0.25f);
                
                ezel.playWorldSound(Sound.ENTITY_VEX_AMBIENT, 2.0f);
                ezel.swingMainHand();
            }
        };
    }
}
