package me.hapyl.fight.game.talents.rogue;

import me.hapyl.eterna.module.block.display.BDEngine;
import me.hapyl.eterna.module.block.display.DisplayData;
import me.hapyl.eterna.module.block.display.DisplayEntity;
import me.hapyl.eterna.module.math.Tick;
import me.hapyl.eterna.module.registry.Key;
import me.hapyl.fight.game.Response;
import me.hapyl.fight.game.attribute.AttributeType;
import me.hapyl.fight.game.attribute.ModifierSource;
import me.hapyl.fight.game.attribute.ModifierType;
import me.hapyl.fight.game.damage.DamageCause;
import me.hapyl.fight.game.effect.EffectType;
import me.hapyl.fight.game.entity.GamePlayer;
import me.hapyl.fight.game.entity.LivingGameEntity;
import me.hapyl.fight.game.talents.Talent;
import me.hapyl.fight.game.talents.TalentType;
import me.hapyl.fight.game.task.TickingGameTask;
import me.hapyl.fight.util.Collect;
import me.hapyl.fight.util.displayfield.DisplayField;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class ExtraCut extends Talent {
    
    private final DisplayData displayData = BDEngine.parse(
            "{Passengers:[{id:\"minecraft:item_display\",item:{id:\"minecraft:iron_sword\",Count:1},item_display:\"none\",transformation:[0.3536f,0.3536f,0.0000f,0.0000f,0.0000f,0.0000f,-0.5000f,0.0000f,-0.3536f,0.3536f,0.0000f,0.0000f,0.0000f,0.0000f,0.0000f,1.0000f]},{id:\"minecraft:block_display\",block_state:{Name:\"minecraft:gold_block\",Properties:{}},transformation:[0.0354f,0.0000f,0.0354f,-0.0347f,0.0000f,0.0403f,0.0000f,-0.0217f,-0.0354f,0.0000f,0.0354f,-0.1774f,0.0000f,0.0000f,0.0000f,1.0000f]},{id:\"minecraft:block_display\",block_state:{Name:\"minecraft:gold_block\",Properties:{}},transformation:[0.0354f,0.0000f,0.0354f,-0.0347f,0.0000f,0.0403f,0.0000f,-0.0223f,-0.0354f,0.0000f,0.0354f,-0.2207f,0.0000f,0.0000f,0.0000f,1.0000f]}]}"
    );
    
    @DisplayField private final int castingTime = 7;
    @DisplayField private final double maxFlightDistance = 80;
    @DisplayField private final double step = 1.75d;
    @DisplayField private final double damage = 15.0d;
    @DisplayField private final double speedDecrease = -75;
    @DisplayField private final int impairDuration = Tick.fromSecond(2);
    
    private final ModifierSource modifierSource = new ModifierSource(Key.ofString("throwing_knife"), true);
    
    public ExtraCut(@Nonnull Key key) {
        super(key, "Throwing Knife");
        
        setDescription("""
                       Equip a &fthrowing knife&7.
                       
                       After a short casting time, throw it forward, &cdamaging&7 the first &cenemy&7 it hits and &eimpair&7 their movement.
                       """
        );
        
        setMaterial(Material.IRON_SWORD);
        setType(TalentType.DAMAGE);
        setCooldownSec(12);
    }
    
    @Override
    public @Nullable Response execute(@Nonnull GamePlayer player) {
        player.addPotionEffect(PotionEffectType.SLOWNESS, 5, castingTime + 5); // Slowness for FOV effect only
        player.addEffect(EffectType.MOVEMENT_CONTAINMENT, castingTime + 5);
        
        new TickingGameTask() {
            private Location location = getLocation();
            private final DisplayEntity entity = displayData.spawnInterpolated(location);
            
            private double distanceFlown = 0.0d;
            private Vector vector;
            
            @Override
            public void run(int tick) {
                // Fx
                if (tick < castingTime) {
                    entity.teleport(getLocation());
                    
                    player.playSound(Sound.BLOCK_LEVER_CLICK, 0.5f + (1.5f / castingTime * tick));
                    return;
                }
                
                // Launch
                if (vector == null) {
                    location = getLocation();
                    vector = location.getDirection().normalize().multiply(step);
                    
                    player.playWorldSound(Sound.BLOCK_ANVIL_LAND, 1.5f);
                }
                
                if ((distanceFlown += step) >= maxFlightDistance) {
                    cancel();
                    return;
                }
                
                location.add(vector);
                entity.teleport(location);
                
                // Block collision check
                if (location.getBlock().getType().isOccluding()) {
                    cancel();
                    return;
                }
                
                // Collision check
                final LivingGameEntity hitEntity = Collect.nearestEntity(location, 1.0d, entity -> !player.isSelfOrTeammate(entity));
                
                if (hitEntity != null) {
                    hitEntity.damage(damage, player, DamageCause.THROWING_KNIFE);
                    hitEntity.getAttributes().addModifier(modifierSource, impairDuration, player, modifier -> modifier.of(AttributeType.SPEED, ModifierType.FLAT, speedDecrease));
                    
                    cancel();
                }
            }
            
            @Override
            public void onTaskStop() {
                entity.remove();
            }
            
            private Location getLocation() {
                return player.getEyeLocationOffset(-0.2, 0.33d);
            }
        }.runTaskTimer(0, 1);
        
        // Fx
        
        return Response.OK;
    }
}
