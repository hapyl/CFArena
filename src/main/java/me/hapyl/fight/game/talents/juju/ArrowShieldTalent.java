package me.hapyl.fight.game.talents.juju;


import com.google.common.collect.Lists;
import me.hapyl.eterna.module.block.display.BDEngine;
import me.hapyl.eterna.module.block.display.DisplayData;
import me.hapyl.eterna.module.locaiton.LocationHelper;
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
import me.hapyl.fight.game.entity.Shield;
import me.hapyl.fight.game.talents.Talent;
import me.hapyl.fight.game.talents.TalentType;
import me.hapyl.fight.util.Collect;
import me.hapyl.fight.util.displayfield.DisplayField;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.Display;
import org.bukkit.event.Listener;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.LinkedList;

public class ArrowShieldTalent extends Talent implements Listener {
    
    private final ModifierSource modifierSource = new ModifierSource(Key.ofString("arrow_shield"), true);
    
    @DisplayField private final double explosionRadius = 5.0;
    @DisplayField private final double explosionDamage = 5.0;
    
    @DisplayField private final int poisonDuration = Tick.fromSecond(3);
    
    @DisplayField private final short shieldCharges = 5;
    @DisplayField private final short poisonStrength = 2;
    
    @DisplayField(percentage = true) private final double defenseReduction = -0.2;
    
    private final DisplayData model = BDEngine.parse(
            "/summon block_display ~-0.5 ~-0.5 ~-0.5 {Passengers:[{id:\"minecraft:item_display\",item:{id:\"minecraft:feather\",Count:1},item_display:\"none\",transformation:[-0.0039f,-0.0039f,0.2228f,-0.0951f,0.0498f,-0.0985f,0f,0.6758f,0.0843f,0.0842f,0.0106f,0.0267f,0f,0f,0f,1f]},{id:\"minecraft:block_display\",block_state:{Name:\"minecraft:black_candle\",Properties:{candles:\"1\",lit:\"false\"}},transformation:[0.1876f,0f,0f,-0.1888f,0f,-0.4282f,0f,0.6804f,0f,0f,-0.1904f,0.1267f,0f,0f,0f,1f]},{id:\"minecraft:block_display\",block_state:{Name:\"minecraft:soul_torch\",Properties:{}},transformation:[0f,0f,0.1841f,-0.1869f,0f,0.4727f,0f,0.2368f,-0.1869f,0f,0f,0.125f,0f,0f,0f,1f]},{id:\"minecraft:block_display\",block_state:{Name:\"minecraft:black_candle\",Properties:{candles:\"1\",lit:\"false\"}},transformation:[0.1876f,0f,0f,-0.1888f,0f,-0.2397f,0f,0.2656f,0f,0f,-0.1904f,0.1267f,0f,0f,0f,1f]},{id:\"minecraft:item_display\",item:{id:\"minecraft:feather\",Count:1},item_display:\"none\",transformation:[-0.0039f,-0.0039f,-0.2228f,-0.0951f,0.0498f,-0.0985f,0f,0.6758f,-0.0843f,-0.0842f,0.0106f,0.0375f,0f,0f,0f,1f]},{id:\"minecraft:item_display\",item:{id:\"minecraft:feather\",Count:1},item_display:\"none\",transformation:[0.0835f,0.0834f,0.0105f,-0.1005f,0.0498f,-0.0985f,0f,0.6758f,0.004f,0.004f,-0.2249f,0.0321f,0f,0f,0f,1f]},{id:\"minecraft:item_display\",item:{id:\"minecraft:feather\",Count:1},item_display:\"none\",transformation:[-0.0836f,-0.0832f,0.0105f,-0.0897f,0.0497f,-0.0987f,0f,0.6758f,0.004f,0.004f,0.2249f,0.0321f,0f,0f,0f,1f]},{id:\"minecraft:block_display\",block_state:{Name:\"minecraft:green_wool\",Properties:{}},transformation:[0.0256f,0f,0f,-0.1079f,0f,-0.0478f,0f,0.5196f,0f,0f,-0.0259f,0.044f,0f,0f,0f,1f]},{id:\"minecraft:block_display\",block_state:{Name:\"minecraft:green_wool\",Properties:{}},transformation:[0.0237f,0f,0f,-0.1079f,0f,-0.02f,0f,0.5392f,0f,0f,-0.0181f,0.0438f,0f,0f,0f,1f]},{id:\"minecraft:block_display\",block_state:{Name:\"minecraft:green_concrete_powder\",Properties:{}},transformation:[0.0237f,0f,0f,-0.1084f,0f,-0.02f,0f,0.5097f,0f,0f,-0.021f,0.0443f,0f,0f,0f,1f]},{id:\"minecraft:block_display\",block_state:{Name:\"minecraft:green_wool\",Properties:{}},transformation:[0.0237f,0f,0f,-0.1061f,0f,-0.02f,0f,0.4745f,0f,0f,-0.0181f,0.0438f,0f,0f,0f,1f]},{id:\"minecraft:item_display\",item:{id:\"minecraft:ghast_tear\",Count:1},item_display:\"none\",transformation:[0.1379f,0f,0f,-0.0943f,0f,-0.2149f,0f,0.1399f,0f,0f,-0.1941f,0.0329f,0f,0f,0f,1f]},{id:\"minecraft:item_display\",item:{id:\"minecraft:ghast_tear\",Count:1},item_display:\"none\",transformation:[0f,0f,0.1923f,-0.0943f,0f,-0.2149f,0f,0.1399f,0.1392f,0f,0f,0.0329f,0f,0f,0f,1f]},{id:\"minecraft:item_display\",item:{id:\"minecraft:prismarine_shard\",Count:1},item_display:\"none\",transformation:[0f,0f,0.1339f,-0.0941f,-0.0758f,-0.071f,0f,0.2167f,0.0848f,-0.092f,0f,0.0308f,0f,0f,0f,1f]},{id:\"minecraft:item_display\",item:{id:\"minecraft:prismarine_shard\",Count:1},item_display:\"none\",transformation:[0f,0f,-0.1339f,-0.0941f,-0.0754f,-0.0714f,0f,0.2167f,-0.0852f,0.0916f,0f,0.0334f,0f,0f,0f,1f]},{id:\"minecraft:item_display\",item:{id:\"minecraft:prismarine_shard\",Count:1},item_display:\"none\",transformation:[-0.084f,0.0912f,0f,-0.0928f,-0.0758f,-0.071f,0f,0.2167f,0f,0f,0.1352f,0.0326f,0f,0f,0f,1f]},{id:\"minecraft:item_display\",item:{id:\"minecraft:prismarine_shard\",Count:1},item_display:\"none\",transformation:[0.0844f,-0.0907f,0f,-0.0949f,-0.0754f,-0.0714f,0f,0.2167f,0f,0f,-0.1352f,0.0326f,0f,0f,0f,1f]},{id:\"minecraft:block_display\",block_state:{Name:\"minecraft:green_wool\",Properties:{}},transformation:[0.0256f,0f,0f,-0.1079f,0f,-0.0179f,0f,0.2783f,0f,0f,-0.0259f,0.044f,0f,0f,0f,1f]},{id:\"minecraft:block_display\",block_state:{Name:\"minecraft:green_wool\",Properties:{}},transformation:[0.0119f,0f,0f,-0.1079f,0f,-0.0362f,0f,0.308f,0f,0f,-0.017f,0.044f,0f,0f,0f,1f]}]}"
    );
    
    public ArrowShieldTalent(@Nonnull Key key) {
        super(key, "Arrow Shield");
        
        setDescription("""
                       Creates an &eshield&7 of arrows for {duration} that blocks &nany&7 damage.
                       
                       When hit, an arrow triggers a rapid &4explosion&7 in small &cAoE&7, dealing &cdamage&7, applying &2poison&7, and reducing %s.
                       """.formatted(AttributeType.DEFENSE)
        );
        
        setType(TalentType.DEFENSE);
        setMaterial(Material.STRING);
        setDurationSec(15);
        setCooldownSec(40);
    }
    
    @Override
    public @Nullable Response execute(@Nonnull GamePlayer player) {
        player.setShield(new ArrowShield(player));
        
        // Fx
        player.playWorldSound(Sound.ITEM_CROSSBOW_SHOOT, 0.75f);
        player.playWorldSound(Sound.ITEM_SHIELD_BLOCK, 1.25f);
        
        return Response.OK;
    }
    
    private class ArrowShield extends Shield {
        
        private final LinkedList<Display> arrows;
        private double theta;
        
        public ArrowShield(@Nonnull LivingGameEntity entity) {
            super(entity, shieldCharges, builder -> builder.duration(ArrowShieldTalent.this));
            
            this.arrows = Lists.newLinkedList();
            
            // Create arrows
            final Location location = entity.getLocation();
            
            for (int i = 0; i < shieldCharges; i++) {
                arrows.add(model.spawnInterpolated(location));
            }
        }
        
        @Override
        public boolean canShield(@Nullable DamageCause cause) {
            // Juju shield can shield from anything, even piercing damage
            return true;
        }
        
        @Override
        public void onRemove(@Nonnull Cause cause) {
            this.arrows.forEach(Display::remove);
            this.arrows.clear();
            
            switch (cause) {
                case BROKEN -> this.entity.sendMessage("&6&l〰 &4Your shield has broke!");
                case EXPIRED -> this.entity.sendMessage("&6&l〰 &eYour shield has expired!");
            }
        }
        
        @Override
        public void takeDamage(double damage) {
            this.capacity--;
            
            final Display last = this.arrows.poll();
            
            if (last != null) {
                createExplosion(last.getLocation());
                last.remove();
            }
        }
        
        @Override
        public void tick() {
            super.tick();
            
            final Location location = entity.getMidpointLocation();
            location.setYaw(0.0f);
            location.setPitch(0.0f);
            
            final double offset = (Math.PI * 2 / Math.max(arrows.size(), 1));
            
            for (int index = 0; index < arrows.size(); index++) {
                final Display display = arrows.get(index);
                
                final double x = Math.sin(theta + offset * index);
                final double y = Math.cos(Math.toRadians(entity.ticker.aliveTicks.getTick() * index)) * 0.2;
                final double z = Math.cos(theta + offset * index);
                
                LocationHelper.offset(
                        location, x, y, z, () -> {
                            display.teleport(location);
                            
                            // Fx
                            entity.spawnWorldParticle(location, Particle.TOTEM_OF_UNDYING, 1, 0, 0, 0, 0.05f);
                        }
                );
            }
            
            theta += Math.PI / 20;
        }
        
        private void createExplosion(@Nonnull Location location) {
            Collect.nearbyEntities(location, explosionRadius, entity::isNotSelfOrTeammate)
                   .forEach(entity -> {
                       entity.getAttributes().addModifier(
                               modifierSource, poisonDuration, ArrowShield.this.entity, modifier -> modifier
                                       .of(AttributeType.DEFENSE, ModifierType.ADDITIVE, defenseReduction)
                       );
                       
                       entity.damage(explosionDamage, ArrowShield.this.entity, DamageCause.POISON_IVY);
                       entity.addEffect(EffectType.POISON, poisonStrength, poisonDuration);
                   });
            
            // Fx
            entity.spawnWorldParticle(location, Particle.TOTEM_OF_UNDYING, 25, 0, 0, 0, 0.75f);
            entity.spawnWorldParticle(location, Particle.HAPPY_VILLAGER, 5, 0.25d, 0.25d, 0.25d, 0.0f);
            
            entity.playWorldSound(location, Sound.ENCHANT_THORNS_HIT, 0.75f);
            entity.playWorldSound(location, Sound.ENCHANT_THORNS_HIT, 1.25f);
        }
    }
    
}
