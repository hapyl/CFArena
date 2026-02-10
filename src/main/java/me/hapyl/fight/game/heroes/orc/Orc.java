package me.hapyl.fight.game.heroes.orc;

import me.hapyl.eterna.module.math.Tick;
import me.hapyl.eterna.module.registry.Key;
import me.hapyl.fight.event.DamageInstance;
import me.hapyl.fight.game.Disabled;
import me.hapyl.fight.game.GameInstance;
import me.hapyl.fight.game.Named;
import me.hapyl.fight.game.attribute.*;
import me.hapyl.fight.game.damage.DamageCause;
import me.hapyl.fight.game.entity.GamePlayer;
import me.hapyl.fight.game.heroes.Archetype;
import me.hapyl.fight.game.heroes.Gender;
import me.hapyl.fight.game.heroes.Hero;
import me.hapyl.fight.game.heroes.HeroProfile;
import me.hapyl.fight.game.heroes.equipment.HeroEquipment;
import me.hapyl.fight.game.heroes.ultimate.UltimateInstance;
import me.hapyl.fight.game.heroes.ultimate.UltimateTalent;
import me.hapyl.fight.game.talents.TalentRegistry;
import me.hapyl.fight.game.talents.TalentType;
import me.hapyl.fight.game.talents.orc.DontAngerMePassive;
import me.hapyl.fight.game.talents.orc.OrcAxe;
import me.hapyl.fight.game.talents.orc.OrcGrowl;
import me.hapyl.fight.game.task.player.PlayerGameTask;
import me.hapyl.fight.util.collection.player.PlayerMap;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.event.Listener;
import org.bukkit.inventory.meta.trim.TrimMaterial;
import org.bukkit.inventory.meta.trim.TrimPattern;

import javax.annotation.Nonnull;

public class Orc extends Hero implements Listener, Disabled {
    
    private final PlayerMap<DamageData> damageMap = PlayerMap.newMap();
    private final ModifierSource modifierSource = new ModifierSource(Key.ofString("berserk"));
    
    public Orc(@Nonnull Key key) {
        super(key, "Pakarat Rakab");
        
        setDescription("""
                       Half-orc half-dwarf loner.
                       """);
        
        final HeroProfile profile = getProfile();
        profile.setArchetypes(Archetype.DAMAGE);
        profile.setGender(Gender.MALE);
        
        final HeroAttributes attributes = getAttributes();
        attributes.setMaxHealth(120);
        attributes.setDefense(60);
        attributes.setSpeed(110);
        attributes.setCritChance(15);
        attributes.setEffectResistance(35);
        attributes.setAttackSpeed(60);
        
        setWeapon(new OrcWeapon());
        
        setItem("a06220fdfef4d53da8bcef8cbef9a8a3add3d776de43a3781b2f58869ce3d738");
        
        final HeroEquipment equipment = getEquipment();
        equipment.setChestPlate(170, 173, 164, TrimPattern.RIB, TrimMaterial.QUARTZ);
        equipment.setLeggings(39, 45, 61, TrimPattern.DUNE, TrimMaterial.COPPER);
        equipment.setBoots(Material.NETHERITE_BOOTS, TrimPattern.SILENCE, TrimMaterial.NETHERITE);
        
        setUltimate(new OrcUltimate());
    }
    
    @Override
    public void processDamageAsVictim(@Nonnull DamageInstance instance) {
        final GamePlayer player = instance.getEntityAsPlayer();
        final DamageCause cause = instance.getCause();
        
        if (!cause.isDirectDamage()) {
            return;
        }
        
        if (damageMap.computeIfAbsent(player, DamageData::new).addHitAndCheck()) {
            enterBerserk(player, Tick.fromSeconds(3));
        }
    }
    
    @Override
    public void onDeath(@Nonnull GamePlayer player) {
        if (!(getWeapon() instanceof OrcWeapon orcWeapon)) {
            return;
        }
        
        damageMap.remove(player);
        orcWeapon.remove(player);
    }
    
    public void enterBerserk(@Nonnull GamePlayer player, int duration) {
        final DontAngerMePassive passiveTalent = getPassiveTalent();
        final EntityAttributes attributes = player.getAttributes();
        final boolean isNewTemper = attributes.hasModifier(modifierSource);
        
        attributes.addModifier(
                modifierSource, duration, modifier -> modifier
                        .of(AttributeType.ATTACK, ModifierType.MULTIPLICATIVE, passiveTalent.attackIncrease)
                        .of(AttributeType.SPEED, ModifierType.FLAT, passiveTalent.speedIncrease)
                        .of(AttributeType.CRIT_CHANCE, ModifierType.FLAT, passiveTalent.critChanceIncrease)
                        .of(AttributeType.DEFENSE, ModifierType.FLAT, passiveTalent.defenseDecrease)
        );
        
        if (isNewTemper) {
            player.sendMessage(Named.BERSERK.getPrefix() + "&aYou're berserk!");
        }
        
        // Fx
        new PlayerGameTask(player, Orc.class) {
            private int tick = 0;
            
            @Override
            public void run() {
                if (tick++ >= duration) {
                    player.sendMessage(Named.BERSERK + " &ais over!");
                    cancel();
                    return;
                }
                
                // Sound FX
                if (tick % 20 == 0) {
                    player.playWorldSound(Sound.ENTITY_PIGLIN_AMBIENT, 0.75f);
                    player.playWorldSound(Sound.ENTITY_PIGLIN_ANGRY, 1.25f);
                }
                
                // Particle FX
                if (tick % 5 == 0) {
                    player.spawnWorldParticle(Particle.LAVA, 2, 0.1, 0.2, 0.1, 0.1f);
                }
            }
            
        }.runTaskTimer(0, 1);
    }
    
    @Override
    public void onStop(@Nonnull GameInstance instance) {
        if (getWeapon() instanceof OrcWeapon weapon) {
            weapon.removeAll();
        }
        damageMap.clear();
    }
    
    @Override
    public OrcGrowl getFirstTalent() {
        return TalentRegistry.ORC_GROWN;
    }
    
    @Override
    public OrcAxe getSecondTalent() {
        return TalentRegistry.ORC_AXE;
    }
    
    @Override
    public DontAngerMePassive getPassiveTalent() {
        return TalentRegistry.ORC_PASSIVE;
    }
    
    private class OrcUltimate extends UltimateTalent {
        public OrcUltimate() {
            super(Orc.this, "Berserk", 70);
            
            setDescription("""
                           Enter %s for {duration}.
                           
                           While active, gain:
                           • &aIncreased %s.
                           • &aIncreased %s.
                           • &aIncreased %s.
                           • &cDecreased %s.
                           """.formatted(
                    Named.BERSERK,
                    AttributeType.ATTACK,
                    AttributeType.SPEED,
                    AttributeType.CRIT_CHANCE,
                    AttributeType.DEFENSE
            ));
            
            setType(TalentType.ENHANCE);
            setMaterial(Material.NETHER_WART);
            setDurationSec(10);
            setCooldownSec(20);
        }
        
        @Nonnull
        @Override
        public UltimateInstance newInstance(@Nonnull GamePlayer player, boolean isFullyCharged) {
            return execute(() -> {
                enterBerserk(player, getDuration());
            });
        }
    }
}
