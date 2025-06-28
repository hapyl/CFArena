package me.hapyl.fight.game.heroes.himari;

import me.hapyl.eterna.module.registry.Key;
import me.hapyl.fight.game.attribute.AttributeType;
import me.hapyl.fight.game.attribute.ModifierType;
import me.hapyl.fight.game.damage.DamageCause;
import me.hapyl.fight.game.entity.GameEntity;
import me.hapyl.fight.game.entity.GamePlayer;
import me.hapyl.fight.game.entity.LivingGameEntity;
import me.hapyl.fight.game.heroes.*;
import me.hapyl.fight.game.heroes.equipment.HeroEquipment;
import me.hapyl.fight.game.heroes.ultimate.UltimateInstance;
import me.hapyl.fight.game.heroes.ultimate.UltimateTalent;
import me.hapyl.fight.game.talents.Talent;
import me.hapyl.fight.game.talents.TalentRegistry;
import me.hapyl.fight.game.talents.himari.*;
import me.hapyl.fight.game.weapons.Weapon;
import me.hapyl.fight.util.collection.player.PlayerDataMap;
import me.hapyl.fight.util.collection.player.PlayerMap;
import me.hapyl.fight.util.displayfield.DisplayField;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.event.Listener;
import org.bukkit.inventory.meta.trim.TrimMaterial;
import org.bukkit.inventory.meta.trim.TrimPattern;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;

public class Himari extends Hero implements Listener, PlayerDataHandler<HimariData> {
    private final PlayerDataMap<HimariData> playerData = PlayerMap.newDataMap(player -> new HimariData(player, this));
    
    public Himari(@Nonnull Key key) {
        super(key, "Himari");
        
        final HeroProfile profile = getProfile();
        profile.setArchetypes(Archetype.DAMAGE);
        profile.setGender(Gender.FEMALE);
        
        setDescription("""
                       A girl who loves to gamble.
                       
                       No matter if it's for money, results, dares or even her life!
                       """);
        
        setItem("23172927c6518ee184a1466d5f1ea81b989ced61a5d5159e3643bb9caf9c189f");
        
        final HeroEquipment equipment = getEquipment();
        equipment.setChestPlate(128, 128, 128, TrimPattern.FLOW, TrimMaterial.NETHERITE);
        equipment.setLeggings(59, 59, 57, TrimPattern.FLOW, TrimMaterial.NETHERITE);
        equipment.setBoots(51, 49, 49, TrimPattern.FLOW, TrimMaterial.NETHERITE);
        
        setWeapon(Weapon.builder(Material.ENCHANTED_BOOK, Key.ofString("teachings_of_freedom"))
                        .name("Teachings of Freedom")
                        .description("""
                                     A book that contains a lot of teachings and theory.
                                     
                                     There are many pages, some of them &f&lglow&7 as you observe more.
                                     """
                                     //  (she skipped a lot of lessons btw, fuck dr.ed)
                        ).damage(5.0d));
        
        
        setUltimate(new HimariUltimate());
    }
    
    @Override
    public LuckyDay getFirstTalent() {
        return TalentRegistry.LUCKY_DAY;
    }
    
    @Override
    public DeadEye getSecondTalent() {
        return TalentRegistry.DEAD_EYE;
    }
    
    public SpikeBarrier getThirdTalent() {
        return TalentRegistry.SPIKE_BARRIER;
    }
    
    @Override
    public Talent getPassiveTalent() {
        return null;
    }
    
    @Override
    public @NotNull PlayerDataMap<HimariData> getDataMap() {
        return playerData;
    }
    
    private class HimariUltimate extends UltimateTalent {
        
        private final HimariActionList actionList = new HimariActionList();
        
        @DisplayField private final short witherAmplifier = 4;
        @DisplayField private final int witherDuration = 155;
        @DisplayField(percentage = true) private final double healingThreshold = 0.3d;
        
        @DisplayField private final double speedAmplify = 200;
        @DisplayField private final int speedDuration = 100;
        
        @DisplayField private final double selfDamage = 20;
        @DisplayField(percentage = true) private final double healing = 0.4;
        
        public HimariUltimate() {
            super(Himari.this, "All In", 60);
            setDescription("""
                           Throw a die and roll between &a1&7-&44&7 to gain a random effect:
                           
                           &a1 &8»&7 Gain %s increase.
                           &e2 &8»&7 Wither your last &cattacker&7.
                           &63 &8»&7 &aHeal&7 yourself.
                           &44 &8»&7 &4Suffer&7.
                           """.formatted(AttributeType.SPEED));
            
            setMaterial(Material.IRON_SWORD);
            setDurationSec(0.5f);
            setCooldownSec(30);
            
            actionList.append(player -> {
                //move speed
                player.getAttributes().addModifier(LuckyDay.modifierSource, speedDuration, modifier -> {
                    modifier.of(AttributeType.SPEED, ModifierType.FLAT, speedAmplify);
                });
                playRollFx(player, "Move...");
            });
            
            actionList.append(new HimariAction() {
                @Override
                public void execute(@Nonnull GamePlayer player) {
                    final GameEntity lastAttacker = player.lastDamager();
                    
                    if (lastAttacker instanceof LivingGameEntity livingAttacker) {
                        livingAttacker.addPotionEffect(PotionEffectType.WITHER, witherAmplifier, witherDuration); // kinda ass replace with wither DoT ig?
                        playRollFx(player, "They will regret...");
                    }
                }
                
                @Override
                public boolean canExecute(@Nonnull GamePlayer player) {
                    final GameEntity lastAttacker = player.lastDamager();
                    
                    return lastAttacker instanceof LivingGameEntity;
                }
            });
            
            actionList.append(new HimariAction() {
                @Override
                public void execute(@Nonnull GamePlayer player) {
                    player.healRelativeToMaxHealth(healing);
                    playRollFx(player, "Back in action...");
                }
                
                @Override
                public boolean canExecute(@Nonnull GamePlayer player) {
                    return player.getHealth() < player.getMaxHealth() * healingThreshold;
                }
            });
            
            actionList.append(player -> {
                //Self-damage (haram!!)
                player.damageNoKnockback(selfDamage, player, DamageCause.GAMBLE);
                playRollFx(player, "Judgement.");
            });
        }
        
        @Nonnull
        @Override
        public UltimateInstance newInstance(@Nonnull GamePlayer player, boolean isFullyCharged) {
            return execute(() -> {
                new HimariDiceAnimation(player, actionList).play(getDuration());
                // Fx
                
            });
        }
        
        private void playRollFx(GamePlayer player, String message) {
            player.sendTitle("&7You hear a whisper in your head:", "&0&o&l" + message, 10, 50, 5);
            
            player.playSound(Sound.PARTICLE_SOUL_ESCAPE, 0.0f);
        }
    }
    
}
