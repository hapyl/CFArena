package me.hapyl.fight.game.talents.knight;


import me.hapyl.eterna.module.registry.Key;
import me.hapyl.fight.event.custom.GameDamageEvent;
import me.hapyl.fight.game.GameInstance;
import me.hapyl.fight.game.Response;
import me.hapyl.fight.game.attribute.AttributeType;
import me.hapyl.fight.game.attribute.ModifierSource;
import me.hapyl.fight.game.attribute.ModifierType;
import me.hapyl.fight.game.damage.DamageCause;
import me.hapyl.fight.game.entity.GamePlayer;
import me.hapyl.fight.game.entity.LivingGameEntity;
import me.hapyl.fight.game.talents.Talent;
import me.hapyl.fight.game.talents.TalentType;
import me.hapyl.fight.game.team.GameTeam;
import me.hapyl.fight.util.collection.player.PlayerMap;
import me.hapyl.fight.util.displayfield.DisplayField;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class StoneCastle extends Talent implements Listener {
    
    public final ModifierSource modifierSource = new ModifierSource(Key.ofString("stone_castle"));
    
    @DisplayField(percentage = true) protected final double defenseIncrease = 1.5d;
    @DisplayField(percentage = true) protected final double kbResistanceIncrease = 50;
    @DisplayField(percentage = true) protected final double ccResistanceIncrease = 50;
    @DisplayField protected final double distance = 8.0d;
    
    private final double damageSplitSelf = 0.7d;
    private final double damageSplitOther = 1 - damageSplitSelf;
    
    @DisplayField private final String damageSplit = "%.0f%%/%.0f%%".formatted(damageSplitSelf * 100, damageSplitOther * 100);
    
    private final PlayerMap<Castle> castleMap = PlayerMap.newConcurrentMap();
    
    public StoneCastle(@Nonnull Key key) {
        super(key, "Castle of Stone");
        
        setDescription("""
                       Erect a castle of stone at your current location.
                       
                       When a &ateammate&7 &b&nwithin&7 the castle takes &cdamage&7, the damage is &asplit&7 between you.
                       
                       You also receive a %s, %s and %s increase.
                       """.formatted(AttributeType.DEFENSE, AttributeType.KNOCKBACK_RESISTANCE, AttributeType.EFFECT_RESISTANCE)
        );
        
        setType(TalentType.DEFENSE);
        setMaterial(Material.PURPUR_PILLAR);
        setDurationSec(12);
        setCooldownSec(10);
    }
    
    @Override
    public void onDeath(@Nonnull GamePlayer player) {
        castleMap.removeAnd(player, Castle::remove);
    }
    
    @Override
    public void onStop(@Nonnull GameInstance instance) {
        castleMap.forEachAndClear(Castle::remove);
    }
    
    @EventHandler()
    public void handleDamageEvent(GameDamageEvent.Process ev) {
        final LivingGameEntity entity = ev.getEntity();
        
        if (!(entity instanceof GamePlayer player)) {
            return;
        }
        
        final GameTeam team = player.getTeam();
        for (GamePlayer teammate : team.getPlayers()) {
            final Castle castle = castleMap.get(teammate);
            
            // Don't split self-damage
            if (castle == null || teammate.equals(player)) {
                continue;
            }
            
            if (!castle.isEntityWithin(player) || !castle.isEntityWithin(castle.getPlayer())) {
                return;
            }
            
            final double damage = ev.getDamage();
            final double splitDamage = damage * damageSplitSelf;
            
            ev.multiplyDamage(damageSplitOther);
            teammate.damage(splitDamage, DamageCause.STONE_CASTLE);
            break;
        }
    }
    
    @Override
    public @Nullable Response execute(@Nonnull GamePlayer player) {
        final Castle oldCastle = castleMap.remove(player);
        
        if (oldCastle != null) {
            oldCastle.remove();
        }
        
        player.getAttributes().addModifier(
                modifierSource, this, player, modifier -> modifier
                        .of(AttributeType.DEFENSE, ModifierType.ADDITIVE, defenseIncrease)
                        .of(AttributeType.KNOCKBACK_RESISTANCE, ModifierType.FLAT, kbResistanceIncrease)
                        .of(AttributeType.EFFECT_RESISTANCE, ModifierType.FLAT, ccResistanceIncrease)
        );
        
        castleMap.put(
                player, new Castle(this, player) {
                    @Override
                    public void onLastTick() {
                        remove();
                        castleMap.remove(player, this);
                    }
                }
        );
        
        return Response.OK;
    }
}
