package me.hapyl.fight.game.heroes.shaman;

import me.hapyl.eterna.module.math.Tick;
import me.hapyl.eterna.module.registry.Key;
import me.hapyl.fight.event.custom.GameDamageEvent;
import me.hapyl.fight.event.custom.GameEntityHealEvent;
import me.hapyl.fight.game.Named;
import me.hapyl.fight.game.attribute.AttributeType;
import me.hapyl.fight.game.attribute.HeroAttributes;
import me.hapyl.fight.game.attribute.ModifierSource;
import me.hapyl.fight.game.attribute.ModifierType;
import me.hapyl.fight.game.effect.Type;
import me.hapyl.fight.game.entity.GamePlayer;
import me.hapyl.fight.game.entity.LivingGameEntity;
import me.hapyl.fight.game.heroes.*;
import me.hapyl.fight.game.heroes.equipment.HeroEquipment;
import me.hapyl.fight.game.heroes.ultimate.UltimateInstance;
import me.hapyl.fight.game.heroes.ultimate.UltimateTalent;
import me.hapyl.fight.game.talents.TalentRegistry;
import me.hapyl.fight.game.talents.TalentType;
import me.hapyl.fight.game.talents.shaman.OverhealPassive;
import me.hapyl.fight.game.talents.shaman.ShamanMarkTalent;
import me.hapyl.fight.game.talents.shaman.TotemImprisonment;
import me.hapyl.fight.game.talents.shaman.TotemTalent;
import me.hapyl.fight.game.team.GameTeam;
import me.hapyl.fight.game.ui.UIComponent;
import me.hapyl.fight.util.Collect;
import me.hapyl.fight.util.MathBoldFont;
import me.hapyl.fight.util.collection.player.PlayerDataMap;
import me.hapyl.fight.util.collection.player.PlayerMap;
import me.hapyl.fight.util.displayfield.DisplayField;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import javax.annotation.Nonnull;

public class Shaman extends Hero implements PlayerDataHandler<ShamanData>, UIComponent, Listener {
    
    private final PlayerDataMap<ShamanData> shamanData = PlayerMap.newDataMap(ShamanData::new);
    
    public Shaman(@Nonnull Key key) {
        super(key, "Shaman");
        
        final HeroProfile profile = getProfile();
        profile.setAffiliation(Affiliation.THE_JUNGLE);
        profile.setArchetypes(Archetype.SUPPORT);
        profile.setGender(Gender.MALE);
        
        setDescription("""
                       An orc from the Jungle.
                       
                       Always rumbles about something.
                       """
        );
        
        setWeapon(new ShamanWeapon());
        setItem("a90515c41b3e131b623cc04978f101aab2e5b82c892890df991b7c079f91d2bd");
        
        final HeroAttributes attributes = getAttributes();
        attributes.setMaxHealth(75);
        attributes.setAttack(50);
        attributes.setDefense(75);
        attributes.setVitality(50); // to balance self-healing
        attributes.setMending(200);
        attributes.setEffectResistance(30);
        
        final HeroEquipment equipment = getEquipment();
        equipment.setChestPlate(110, 94, 74);
        equipment.setLeggings(57, 40, 90);
        
        setUltimate(new ShamanUltimate());
    }
    
    @EventHandler()
    public void handleOverhealGain(GameEntityHealEvent ev) {
        if (!(ev.getHealer() instanceof GamePlayer player)) {
            return;
        }
        
        if (!validatePlayer(player)) {
            return;
        }
        
        final double excessHealing = ev.getExcessHealing();
        
        if (excessHealing <= 0) {
            return;
        }
        
        final ShamanData data = getPlayerData(player);
        data.increaseOverheal(excessHealing);
    }
    
    @EventHandler()
    public void handleOverhealDamage(GameDamageEvent.Process ev) {
        final LivingGameEntity entity = ev.getEntity();
        final OverhealPassive overheal = getPassiveTalent();
        
        if (!(ev.getDamager() instanceof LivingGameEntity damager)) {
            return;
        }
        
        final GameTeam team = damager.getTeam();
        
        if (team == null) {
            return;
        }
        
        for (GamePlayer player : team.getPlayers()) {
            if (!validatePlayer(player)) {
                continue;
            }
            
            if (damager.getLocation().distance(player.getLocation()) >= overheal.maxOverhealDistance) {
                continue;
            }
            
            final ShamanData data = getPlayerData(player);
            final double overhealCapped = Math.min(data.getOverheal(), overheal.maxOverhealUse);
            
            if (overhealCapped <= 0) {
                continue;
            }
            
            final double damageMultiplier = 1 + overhealCapped * overheal.damageIncreasePerOverheal;
            
            final double originalDamage = ev.getDamage();
            final double damageIncrease = originalDamage * damageMultiplier - originalDamage;
            
            ev.multiplyDamage(damageMultiplier);
            ev.damageDisplaySuffix("&8+&a&l " + MathBoldFont.format("%,.1f".formatted(damageIncrease) + Named.OVERHEAL.getPrefixColored()));
            data.decreaseOverheal(overhealCapped);
            
            return;
        }
    }
    
    @Override
    @Nonnull
    public TotemTalent getFirstTalent() {
        return TalentRegistry.TOTEM;
    }
    
    @Override
    public TotemImprisonment getSecondTalent() {
        return TalentRegistry.TOTEM_IMPRISONMENT;
    }
    
    @Override
    public ShamanMarkTalent getThirdTalent() {
        return TalentRegistry.SHAMAN_MARK;
    }
    
    @Override
    public OverhealPassive getPassiveTalent() {
        return TalentRegistry.OVERHEAL;
    }
    
    @Nonnull
    @Override
    public String getString(@Nonnull GamePlayer player) {
        final ShamanData data = getPlayerData(player);
        
        return "%s &a%.0f".formatted(Named.OVERHEAL.getPrefix(), data.getOverheal()) + (data.isOverheadMaxed() ? " &lMAX!" : "");
    }
    
    @Nonnull
    @Override
    public PlayerDataMap<ShamanData> getDataMap() {
        return shamanData;
    }
    
    public class ShamanUltimate extends UltimateTalent {
        
        @DisplayField private final double increaseRadius = 7.5d;
        @DisplayField private final double effectResIncrease = 50;
        @DisplayField private final int effectResIncreaseDuration = Tick.fromSecond(12);
        
        private final ModifierSource modifierSource = new ModifierSource(Key.ofString("spiritual_cleansing"));
        
        public ShamanUltimate() {
            super(Shaman.this, "Spiritual Cleansing", 45);
            
            setDescription("""
                           Instantly cleanse all &cnegative&7 effects from nearby &aallies&7 and increase their %s for &b{effectResIncreaseDuration}&7.
                           """.formatted(AttributeType.EFFECT_RESISTANCE)
            );
            
            setType(TalentType.SUPPORT);
            setMaterial(Material.MILK_BUCKET);
            setSound(Sound.ENTITY_GOAT_SCREAMING_MILK, 0.0f);
            setCooldownSec(30);
        }
        
        @Nonnull
        @Override
        public UltimateInstance newInstance(@Nonnull GamePlayer player, boolean isFullyCharged) {
            return execute(() -> {
                Collect.nearbyEntities(player.getLocation(), increaseRadius).forEach(entity -> {
                    if (!player.isSelfOrTeammate(entity)) {
                        return;
                    }
                    
                    // Remove effects
                    entity.removeEffectsByType(Type.NEGATIVE);
                    entity.getAttributes().addModifier(
                            modifierSource,
                            effectResIncreaseDuration,
                            player,
                            modifier -> modifier.of(AttributeType.EFFECT_RESISTANCE, ModifierType.FLAT, effectResIncrease)
                    );
                    
                    // Fx
                    final Location location = entity.getLocation();
                    
                    entity.spawnWorldParticle(location, Particle.EFFECT, 20, 0.25d, 0.5d, 0.25d, 0.7f);
                    entity.playWorldSound(Sound.ENTITY_WITCH_DRINK, 0.0f);
                    
                    entity.sendMessage(
                            AttributeType.EFFECT_RESISTANCE.getCharacter() + (player.equals(entity)
                                                                              ? " You cleansed yourself!"
                                                                              : " &d%s cleansed you!".formatted(player.getName()))
                    );
                    
                });
                
                // Fx
                player.playWorldSound(Sound.ENTITY_ILLUSIONER_CAST_SPELL, 1.25f);
                player.playWorldSound(Sound.ENTITY_ILLUSIONER_PREPARE_MIRROR, 1.25f);
            });
        }
    }
}
