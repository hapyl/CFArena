package me.hapyl.fight.game.heroes.taker;

import me.hapyl.eterna.module.inventory.ItemBuilder;
import me.hapyl.eterna.module.locaiton.LocationHelper;
import me.hapyl.eterna.module.registry.Key;
import me.hapyl.fight.event.DamageInstance;
import me.hapyl.fight.game.GameInstance;
import me.hapyl.fight.game.Named;
import me.hapyl.fight.game.damage.DamageCause;
import me.hapyl.fight.game.effect.EffectType;
import me.hapyl.fight.game.entity.GamePlayer;
import me.hapyl.fight.game.heroes.*;
import me.hapyl.fight.game.heroes.equipment.HeroEquipment;
import me.hapyl.fight.game.heroes.ultimate.UltimateInstance;
import me.hapyl.fight.game.heroes.ultimate.UltimateTalent;
import me.hapyl.fight.game.talents.TalentRegistry;
import me.hapyl.fight.game.talents.taker.DeathSwap;
import me.hapyl.fight.game.talents.taker.FatalReap;
import me.hapyl.fight.game.talents.taker.Shadowfall;
import me.hapyl.fight.game.talents.taker.SpiritualBonesPassive;
import me.hapyl.fight.game.task.GameTask;
import me.hapyl.fight.game.ui.UIComponent;
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
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.trim.TrimMaterial;
import org.bukkit.inventory.meta.trim.TrimPattern;

import javax.annotation.Nonnull;

public class Taker extends Hero implements UIComponent, DisplayFieldProvider, PlayerDataHandler<TakerData> {
    
    private final PlayerDataMap<TakerData> playerData = PlayerMap.newDataMap(player -> new TakerData(getPassiveTalent(), player));
    private final ItemStack rageHeadItem = ItemBuilder.playerHeadUrl("8d59abfa35a91034dd3a95e4dd4e6997b0117224939dc9b1609ba952e61f9bc5").asIcon();
    
    public Taker(@Nonnull Key key) {
        super(key, "Taker");
        
        final HeroProfile profile = getProfile();
        profile.setArchetypes(Archetype.DAMAGE, Archetype.MELEE, Archetype.POWERFUL_ULTIMATE);
        profile.setGender(Gender.UNKNOWN);
        
        setDescription("""
                       Will take your life away!
                       """);
        setItem("ff1e554161bd4b2ce4cad18349fd756994f74cabf1fd1dacdf91b6d05dffaf");
        
        final HeroEquipment equipment = getEquipment();
        equipment.setChestPlate(28, 28, 28);
        equipment.setLeggings(0, 0, 0, TrimPattern.SILENCE, TrimMaterial.QUARTZ);
        equipment.setBoots(28, 28, 28, TrimPattern.SILENCE, TrimMaterial.QUARTZ);
        
        setWeapon(Weapon.builder(Material.IRON_HOE, Key.ofString("reaper_scythe"))
                        .name("Reaper Scythe")
                        .description("""
                                     The sharpest of them all!
                                     """)
                        .damage(6.66d)
        );
        
        setUltimate(new TakerUltimate());
    }
    
    @Override
    public void onPlayersRevealed(@Nonnull GameInstance instance) {
        new GameTask() {
            @Override
            public void run() {
                playerData.values().forEach(TakerData::tick);
            }
        }.runTaskTimer(0, 1);
    }
    
    @Override
    public void processDamageAsDamager(@Nonnull DamageInstance instance) {
        final GamePlayer player = instance.getDamagerAsPlayer();
        
        if (player == null) {
            return;
        }
        
        final TakerData data = getPlayerData(player);
        
        if (data.getBones() == 0) {
            return;
        }
        
        final double healing = data.getHealing();
        final double damage = instance.getDamage();
        
        final double healingScaled = damage * healing;
        player.heal(healingScaled);
        
        final double multiplier = 1 + data.getDamageMultiplier();
        instance.multiplyDamage(multiplier);
    }
    
    @Override
    public void processDamageAsVictim(@Nonnull DamageInstance instance) {
        final GamePlayer player = instance.getEntityAsPlayer();
        final TakerData data = getPlayerData(player);
        
        if (data.getBones() == 0) {
            return;
        }
        
        final double multiplier = 1 - data.getDamageReduction();
        instance.multiplyDamage(multiplier);
    }
    
    @Override
    public FatalReap getFirstTalent() {
        return TalentRegistry.FATAL_REAP;
    }
    
    @Override
    public DeathSwap getSecondTalent() {
        return TalentRegistry.DEATH_SWAP;
    }
    
    @Override
    public Shadowfall getThirdTalent() {
        return TalentRegistry.SHADOWFALL;
    }
    
    @Override
    public SpiritualBonesPassive getPassiveTalent() {
        return TalentRegistry.SPIRITUAL_BONES;
    }
    
    @Nonnull
    @Override
    public String getString(@Nonnull GamePlayer player) {
        return "&f\uD83E\uDDB4 &l" + getPlayerData(player).getBones();
    }
    
    @Nonnull
    @Override
    public PlayerDataMap<TakerData> getDataMap() {
        return playerData;
    }
    
    private class TakerUltimate extends UltimateTalent {
        
        @DisplayField private final double baseDamage = 10;
        @DisplayField private final double bonusDamagePerBone = 1;
        
        @DisplayField(percentage = true) private final double baseHealing = 0.02;
        @DisplayField(percentage = true) private final double bonusHealingPerBone = 0.005;
        
        @DisplayField private final double ultimateSpeed = 0.25d;
        @DisplayField private final int hitDelay = 10;
        
        public TakerUltimate() {
            super(Taker.this, "Embodiment of Death", 70);
            
            setDescription("""
                           Instantly consume all %s to cloak yourself in the &8darkness&7 for {duration}.
                           
                           After a short delay, embrace the death and become &binvulnerable&7.
                           
                           The &8darkness&7 force will constantly &brush forward&7, dealing &cAoE damage&7 and &eimpairing&7 anyone who dares to stay in the way.
                           &8&o;;Also recover health every time an enemy is hit.
                           
                           Hold &6&lSNEAK&7 to rush faster.
                           
                           &8&o;;The damage and healing is scaled with each %s consumed.
                           """.formatted(Named.SPIRITUAL_BONES, Named.SPIRITUAL_BONES.getName())
            );
            
            setSound(Sound.ENTITY_HORSE_DEATH, 0.0f);
            setMaterial(Material.WITHER_SKELETON_SKULL);
            setDurationSec(4);
            setCastDuration(20);
            setCdFromCost(2);
        }
        
        @Nonnull
        @Override
        public UltimateInstance newInstance(@Nonnull GamePlayer player, boolean isFullyCharged) {
            final TakerData data = getPlayerData(player);
            
            if (data.getBones() <= 0) {
                return error("Not enough Spiritual Bones!");
            }
            
            final Location location = player.getLocation();
            final int castDuration = getUltimate().getCastDuration();
            
            final int bonesAmount = data.getBones();
            
            final double damage = baseDamage + (bonusDamagePerBone * bonesAmount);
            final double healing = baseHealing + (bonusHealingPerBone * bonesAmount);
            
            data.remove();
            
            return builder()
                    .onCastTick(tick -> {
                        final double distance = Math.sin(Math.PI * ((double) tick / castDuration));
                        
                        for (double d = 0.0d; d < Math.PI * 2; d += Math.PI / 16) {
                            final double x = Math.sin(d) * distance;
                            final double y = player.getEyeHeight() - (2.0d / castDuration * tick);
                            final double z = Math.cos(d) * distance;
                            
                            location.add(x, y, z);
                            
                            player.spawnWorldParticle(location, Particle.LARGE_SMOKE, 1);
                            player.spawnWorldParticle(location, Particle.SMOKE, 1);
                            
                            location.subtract(x, y, z);
                        }
                        
                        // SFX
                        if (tick % 2 == 0) {
                            player.playWorldSound(Sound.ENTITY_ENDER_DRAGON_FLAP, 2.0f / castDuration * tick);
                        }
                    })
                    .onCastEnd(() -> {
                        player.setInvisible(true);
                        player.setInvulnerable(true);
                        
                        player.setItem(EquipmentSlot.HEAD, rageHeadItem);
                        player.setItem(EquipmentSlot.LEGS, null);
                        player.setItem(EquipmentSlot.FEET, null);
                    })
                    .onTick(tick -> {
                        player.setVelocity(
                                player.getLocation()
                                      .getDirection()
                                      .normalize()
                                      .multiply(player.isSneaking() ? ultimateSpeed * 2 : ultimateSpeed)
                        );
                        
                        // Damage
                        if (tick % hitDelay == 0) {
                            final Location hitLocation = player.getLocationInFrontFromEyes(1.5d);
                            
                            Collect.nearbyEntities(hitLocation, 2.0d, player::isNotSelfOrTeammate)
                                   .forEach(entity -> {
                                       entity.damage(damage, player, DamageCause.EMBODIMENT_OF_DEATH);
                                       player.healRelativeToMaxHealth(healing);
                                   });
                            
                            // Hit Fx
                            player.spawnWorldParticle(hitLocation, Particle.SWEEP_ATTACK, 20, 1, 1, 1, 0.0f);
                            
                            player.playWorldSound(hitLocation, Sound.ITEM_TRIDENT_THROW, 0.0f);
                            player.playWorldSound(hitLocation, Sound.ENTITY_WITHER_HURT, 0.75f);
                        }
                        
                        // Instant Fx
                        final Location playerLocation = player.getEyeLocation().subtract(0, 0.5, 0);
                        
                        for (int i = 0; i < 10; i++) {
                            LocationHelper.offset(
                                    playerLocation, player.random.nextDoubleBool(0.5), 0, player.random.nextDoubleBool(0.5), () -> {
                                        player.spawnWorldParticle(playerLocation, Particle.LARGE_SMOKE, 0, 0, -0.75, 0, 0.35f);
                                    }
                            );
                        }
                        
                        player.spawnWorldParticle(player.getEyeLocation(), Particle.LAVA, 1, 0.03125d, 0.6d, 0.03125d, 0.01f);
                    })
                    .onEnd(() -> {
                        Taker.this.getEquipment().equip(player);
                        
                        player.setInvisible(false);
                        player.setInvulnerable(false);
                        
                        player.addEffect(EffectType.FALL_DAMAGE_RESISTANCE, 100);
                    });
        }
    }
}
