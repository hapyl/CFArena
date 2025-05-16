package me.hapyl.fight.game.heroes.nightmare;

import me.hapyl.eterna.module.locaiton.LocationHelper;
import me.hapyl.eterna.module.registry.Key;
import me.hapyl.fight.event.DamageInstance;
import me.hapyl.fight.game.GameInstance;
import me.hapyl.fight.game.Named;
import me.hapyl.fight.game.attribute.AttributeType;
import me.hapyl.fight.game.attribute.ModifierSource;
import me.hapyl.fight.game.attribute.ModifierType;
import me.hapyl.fight.game.entity.GamePlayer;
import me.hapyl.fight.game.entity.LivingGameEntity;
import me.hapyl.fight.game.heroes.*;
import me.hapyl.fight.game.heroes.equipment.HeroEquipment;
import me.hapyl.fight.game.heroes.ultimate.UltimateInstance;
import me.hapyl.fight.game.heroes.ultimate.UltimateTalent;
import me.hapyl.fight.game.talents.TalentRegistry;
import me.hapyl.fight.game.talents.TalentType;
import me.hapyl.fight.game.talents.nightmare.InTheShadowsPassive;
import me.hapyl.fight.game.talents.nightmare.Paranoia;
import me.hapyl.fight.game.talents.nightmare.ShadowShift;
import me.hapyl.fight.game.task.TickingGameTask;
import me.hapyl.fight.game.weapons.Weapon;
import me.hapyl.fight.util.Collect;
import me.hapyl.fight.util.collection.player.PlayerDataMap;
import me.hapyl.fight.util.collection.player.PlayerMap;
import me.hapyl.fight.util.displayfield.DisplayField;
import me.hapyl.fight.util.displayfield.DisplayFieldProvider;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.util.Vector;

import javax.annotation.Nonnull;
import java.util.List;

public class Nightmare extends Hero implements DisplayFieldProvider, PlayerDataHandler<NightmareData> {
    
    @DisplayField
    private final double omenDamageMultiplier = 1.5d;
    
    private final ModifierSource inTheShadows = new ModifierSource(Key.ofString("in_the_shadows"));
    private final PlayerDataMap<NightmareData> playerMap = PlayerMap.newDataMap(NightmareData::new);
    
    public Nightmare(@Nonnull Key key) {
        super(key, "Nightmare");
        
        final HeroProfile profile = getProfile();
        profile.setArchetypes(Archetype.DAMAGE, Archetype.MELEE, Archetype.HEXBANE);
        profile.setGender(Gender.UNKNOWN);
        
        setDescription("A spirit from the worst nightmares, blinds enemies and strikes from behind!");
        setItem("79c55e0e4af71824e8da68cde87de717b214f92e9949c4b16da22b357f97b1fc");
        
        setWeapon(Weapon.createBuilder(Material.NETHERITE_SWORD, Key.ofString("oathbreaker"))
                        .name("Oathbreaker")
                        .description("A sword that is capable of splitting dreams in half.")
                        .damage(7.0d)
        );
        
        final HeroEquipment equipment = getEquipment();
        equipment.setChestPlate(50, 0, 153);
        equipment.setLeggings(40, 0, 153);
        equipment.setBoots(30, 0, 153);
        
        setUltimate(new NightmareUltimate());
    }
    
    @Override
    public void onStart(@Nonnull GameInstance instance) {
        final InTheShadowsPassive passive = getPassiveTalent();
        
        new TickingGameTask() {
            @Override
            public void run(int tick) {
                // Tick data
                playerMap.values().forEach(NightmareData::tick);
                
                // Tick buff
                if (tick % 20 != 0) {
                    return;
                }
                
                for (GamePlayer player : getAlivePlayers()) {
                    if (player.getBlockLight() > passive.moodyLight) {
                        continue;
                    }
                    
                    player.getAttributes().addModifier(
                            inTheShadows, passive.buffDuration, player, modifier -> modifier
                                    .of(AttributeType.ATTACK, ModifierType.MULTIPLICATIVE, passive.attackIncrease)
                                    .of(AttributeType.SPEED, ModifierType.FLAT, passive.speedIncrease)
                    );
                    
                    player.spawnWorldParticle(Particle.LAVA, 5, 0.15d, 0.15d, 0.15d, 0.01f);
                    player.spawnWorldParticle(Particle.LARGE_SMOKE, 5, 0.15d, 0.15d, 0.15d, 0.01f);
                }
            }
        }.runTaskTimer(0, 1);
    }
    
    @Override
    public void processDamageAsDamager(@Nonnull DamageInstance instance) {
        final GamePlayer damager = instance.getDamagerAsPlayer();
        final LivingGameEntity entity = instance.getEntity();
        
        if (damager == null || !instance.isDirectDamage()) {
            return;
        }
        
        final NightmareData data = getPlayerData(damager);
        
        if (!data.isAffected(entity)) {
            return;
        }
        
        instance.multiplyDamage(omenDamageMultiplier);
    }
    
    @Override
    public Paranoia getFirstTalent() {
        return TalentRegistry.PARANOIA;
    }
    
    @Override
    public ShadowShift getSecondTalent() {
        return TalentRegistry.SHADOW_SHIFT;
    }
    
    @Override
    public InTheShadowsPassive getPassiveTalent() {
        return TalentRegistry.IN_THE_SHADOWS;
    }
    
    @Nonnull
    @Override
    public PlayerDataMap<NightmareData> getDataMap() {
        return playerMap;
    }
    
    private class NightmareUltimate extends UltimateTalent {
        
        @DisplayField(suffix = " blocks") private final double radius = 64;
        
        public NightmareUltimate() {
            super(Nightmare.this, "Your Worst Nightmare", 55);
            
            setDescription("""
                           Release the &8darkness&7 within you that rushes outwards, applying %s to all &cenemies&7 in a large area.
                           """.formatted(Named.OMEN)
            );
            
            setType(TalentType.IMPAIR);
            setMaterial(Material.BLACK_DYE);
            setSound(Sound.ENTITY_WITCH_CELEBRATE, 0.0f);
            
            setDurationSec(12);
            setCooldownSec(30);
        }
        
        @Nonnull
        @Override
        public UltimateInstance newInstance(@Nonnull GamePlayer player, boolean isFullyCharged) {
            return execute(() -> {
                final NightmareData data = getPlayerData(player);
                final Location location = player.getLocation();
                
                final List<LivingGameEntity> entities = Collect.nearbyEntities(location, radius, player::isNotSelfOrTeammate);
                final int enemiesSize = entities.size();
                
                entities.forEach(entity -> data.affect(entity, getUltimate().getDuration()));
                
                player.sendMessage(enemiesSize > 0 ? "&4👻 &cOmen affected %s enemies!".formatted(enemiesSize) : "&4👻 &cOmen didn't affect anything!");
                
                // Fx
                drawParticleCircle(player, 0.5d);
                drawParticleCircle(player, 0.45d);
                
                player.playWorldSound(Sound.ENTITY_WITHER_HURT, 0.75f);
            });
        }
        
        private void drawParticleCircle(GamePlayer player, double speed) {
            final Location location = player.getLocation();
            final Location staticLocation = location.clone();
            
            for (double d = 0; d <= Math.PI * 2; d += Math.PI / 64) {
                final double x = Math.sin(d) * 0.5d;
                final double z = Math.cos(d) * 0.5d;
                
                LocationHelper.offset(
                        location, x, 0, z, () -> {
                            final Vector vector = staticLocation.toVector().subtract(location.toVector()).normalize();
                            
                            player.spawnWorldParticle(
                                    staticLocation,
                                    Particle.LARGE_SMOKE, 0,
                                    vector.getX() * speed,
                                    0.1,
                                    vector.getZ() * speed,
                                    1
                            );
                        }
                );
            }
        }
    }
}
