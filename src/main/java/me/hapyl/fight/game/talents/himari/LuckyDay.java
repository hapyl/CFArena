package me.hapyl.fight.game.talents.himari;

import me.hapyl.eterna.module.registry.Key;
import me.hapyl.fight.game.Response;
import me.hapyl.fight.game.attribute.AttributeType;
import me.hapyl.fight.game.attribute.EntityAttributes;
import me.hapyl.fight.game.attribute.ModifierSource;
import me.hapyl.fight.game.attribute.ModifierType;
import me.hapyl.fight.game.effect.EffectType;
import me.hapyl.fight.game.entity.GamePlayer;
import me.hapyl.fight.game.heroes.HeroRegistry;
import me.hapyl.fight.game.heroes.himari.Himari;
import me.hapyl.fight.game.heroes.himari.HimariData;
import me.hapyl.fight.game.heroes.himari.HimariDiceAnimation;
import me.hapyl.fight.game.heroes.ultimate.EnumResource;
import me.hapyl.fight.game.talents.Talent;
import me.hapyl.fight.game.talents.TalentRegistry;
import me.hapyl.fight.util.displayfield.DisplayField;
import org.bukkit.Material;
import org.bukkit.Sound;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class LuckyDay extends Talent {
    
    public static final ModifierSource modifierSource = new ModifierSource(Key.ofString("luckiness"));
    
    @DisplayField private final double damageBuff = 4.5d;
    @DisplayField private final int damageBuffDuration = 120;
    
    @DisplayField(percentage = true) private final double healing = 0.8;
    @DisplayField private final int healthBoostDuration = 130;
    
    public final HimariActionList actionList = new HimariActionList();
    
    public LuckyDay(@Nonnull Key key) {
        super(key, "Lucky Day");
        
        setDescription("""
                       Roll a lucky dice to gain a random effect.
                       
                       Effects include:
                       &8- %s, %s or %s boost.
                       &8- &b&l%s &bUltimate&7 charge.
                       &8- &7One of two talents unlock.
                       &8- &7Or &4pain&7.
                       
                       &8&o;;When a talent is rolled, it first must be used before Lucky Day can be used again.
                       """.formatted(AttributeType.DEFENSE, AttributeType.MAX_HEALTH, AttributeType.ATTACK, EnumResource.ENERGY.getPrefix()));
        //shortly: you do gambling, you win or die
        
        setMaterial(Material.BOOK);
        setDurationSec(1.5f);
        setCooldownSec(18);
        
        actionList.append(player -> {
            player.addEffect(EffectType.WITHER, 4, 115);
            player.addEffect(EffectType.BLINDNESS, 2, 100);
            
            player.sendSubtitle("&4Feel the cost!", 2, 110, 6);
            player.playSound(Sound.ENTITY_WITHER_SPAWN, 2.0f);
        });
        
        actionList.append(GamePlayer::chargeUltimate);
        
        actionList.append(player -> {
            //Healing / Increasing Max HP
            player.sendSubtitle("&aYou feel easier on your soul!", 2, 90, 6);
            
            // If not full health then heal player
            if (player.getHealth() < player.getMaxHealth()) {
                player.healRelativeToMaxHealth(healing);
            }
            // If full health, increase max health by the healing amount to give "extra" health
            else {
                player.getAttributes().addModifier(
                        modifierSource, healthBoostDuration, modifier -> {
                            modifier.of(AttributeType.MAX_HEALTH, ModifierType.ADDITIVE, healing);
                        }
                );
                player.healRelativeToMaxHealth(healing);
            }
        });
        
        actionList.append(player -> {
            final EntityAttributes attributes = player.getAttributes();
            
            //damage buff
            attributes.addModifier(
                    modifierSource, damageBuffDuration, modifier -> {
                        modifier.of(AttributeType.SPEED, ModifierType.ADDITIVE, damageBuff);
                    }
            );
            player.sendSubtitle("&aYou feel stronger right away!", 2, 100, 6);
        });
        
        actionList.append(player -> unlockTalent(player, TalentRegistry.DEAD_EYE));
        actionList.append(player -> unlockTalent(player, TalentRegistry.SPIKE_BARRIER));
    }
    
    private void unlockTalent(GamePlayer player, HimariTalent talent) {
        final HimariData data = getHero().getPlayerData(player);
        data.setTalent(talent);
        
        // Fx
        player.sendSubtitle("&a%s is now available!".formatted(talent.getName()), 5, 70, 5);
        
        player.playSound(Sound.ENTITY_PLAYER_LEVELUP, 2.0f);
        player.playSound(Sound.ENTITY_PLAYER_LEVELUP, 1.75f);
    }
    
    @Override
    public @Nullable Response execute(@Nonnull GamePlayer player) {
        HimariData data = player.getPlayerData(HeroRegistry.HIMARI);
        
        // 1 > Check if Himari talent is set
        HimariTalent currentTalent = data.getTalent();
        
        if (currentTalent != null) {
            return Response.error("Cannot use until %s is used!".formatted(currentTalent.getName()));
        }
        
        // The dice-rolling and effect application logic will run in the task
        new HimariDiceAnimation(player, actionList).play(getDuration());
        return Response.ok();
    }
    
    public Himari getHero() {
        return HeroRegistry.HIMARI;
    }
}
