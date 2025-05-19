package me.hapyl.fight.game.heroes.knight;

import me.hapyl.eterna.module.registry.Key;
import me.hapyl.fight.event.custom.GamePlayerShieldEvent;
import me.hapyl.fight.game.attribute.HeroAttributes;
import me.hapyl.fight.game.entity.GamePlayer;
import me.hapyl.fight.game.entity.LivingGameEntity;
import me.hapyl.fight.game.entity.Shield;
import me.hapyl.fight.game.heroes.*;
import me.hapyl.fight.game.heroes.equipment.HeroEquipment;
import me.hapyl.fight.game.heroes.ultimate.UltimateInstance;
import me.hapyl.fight.game.heroes.ultimate.UltimateTalent;
import me.hapyl.fight.game.talents.Talent;
import me.hapyl.fight.game.talents.TalentRegistry;
import me.hapyl.fight.game.talents.TalentType;
import me.hapyl.fight.game.talents.knight.Discharge;
import me.hapyl.fight.game.talents.knight.StoneCastle;
import me.hapyl.fight.game.ui.UIComponent;
import me.hapyl.fight.game.weapons.Weapon;
import me.hapyl.fight.util.Collect;
import me.hapyl.fight.util.collection.player.PlayerDataMap;
import me.hapyl.fight.util.collection.player.PlayerMap;
import me.hapyl.fight.util.displayfield.DisplayField;
import me.hapyl.fight.util.displayfield.DisplayFieldProvider;
import me.hapyl.fight.util.shield.PatternTypes;
import me.hapyl.fight.util.shield.ShieldBuilder;
import org.bukkit.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import javax.annotation.Nonnull;
import java.util.Collection;

public class BlastKnight extends Hero implements UIComponent, PlayerDataHandler<BlastKnightData>, DisplayFieldProvider, Listener {
    
    public final ItemStack shieldItem = new ShieldBuilder(DyeColor.BLACK)
            .with(DyeColor.WHITE, PatternTypes.DLS)
            .with(DyeColor.PURPLE, PatternTypes.MR)
            .with(DyeColor.BLACK, PatternTypes.DLS)
            .with(DyeColor.PINK, PatternTypes.MC)
            .with(DyeColor.BLACK, PatternTypes.FLO)
            .build();
    
    private final PlayerDataMap<BlastKnightData> dataMap = PlayerMap.newDataMap(BlastKnightData::new);
    private final Key shieldRechargeCdKey = Key.ofString("shield_recharge_key");
    
    public BlastKnight(@Nonnull Key key) {
        super(key, "Blast Knight");
        
        final HeroProfile profile = getProfile();
        profile.setArchetypes(Archetype.SUPPORT, Archetype.DEFENSE);
        profile.setAffiliation(Affiliation.KINGDOM);
        profile.setGender(Gender.MALE);
        
        setDescription("""
                       A royal knight with high-end technology gadgets.
                       """);
        setItem("f6eaa1fd9d2d49d06a894798d3b145d3ae4dcca038b7da718c7b83a66ef264f0");
        
        final HeroAttributes attributes = getAttributes();
        attributes.setDefense(200);
        attributes.setSpeed(90);
        
        final HeroEquipment equipment = getEquipment();
        
        equipment.setName("Quantum Suit");
        equipment.setDescription("""
                                 A suit that is capable of channeling &dQuantum Energy&7.
                                 """);
        equipment.setFlavorText("""
                                A carefully crafted suit, made from unknown materials.
                                It emits a purplish glow, and very warm energy.
                                """);
        
        equipment.setChestPlate(20, 5, 43);
        equipment.setLeggings(170, 55, 204);
        equipment.setBoots(Material.NETHERITE_BOOTS);
        
        setWeapon(Weapon.builder(Material.IRON_SWORD, Key.ofString("royal_sword"))
                        .name("Royal Sword")
                        .description("""
                                     A royal sword, forget of the best quality ore possible.
                                     
                                     It has tiny golden ornate pieces on the edge of the handle.
                                     """
                        )
                        .damage(5.0d)
        );
        
        setUltimate(new BlastKnightUltimate());
    }
    
    @EventHandler
    public void handleGamePlayerShieldEvent(GamePlayerShieldEvent ev) {
        final GamePlayer player = ev.getPlayer();
        final LivingGameEntity damager = ev.damager();
        
        // Don't allow charging shield from non-entity damage, e.g.: fall, fire, etc.
        if (damager == null) {
            return;
        }
        
        final double dot = player.dot(damager.getLocation());
        
        if (dot <= 0.6d) {
            return;
        }
        
        final BlastKnightData data = getPlayerData(player);
        
        if (data.isShieldOnCooldown()) {
            return;
        }
        
        data.incrementShieldCharge();
        
        // Interrupt shield
        final PlayerInventory inventory = player.getInventory();
        final ItemStack offhandItem = inventory.getItem(org.bukkit.inventory.EquipmentSlot.OFF_HAND);
        
        inventory.setItem(org.bukkit.inventory.EquipmentSlot.OFF_HAND, null);
        player.schedule(() -> inventory.setItem(org.bukkit.inventory.EquipmentSlot.OFF_HAND, offhandItem), 3);
        
        // Fx
        player.playSound(Sound.ITEM_SHIELD_BREAK, 1.0f);
        
        ev.setCancelled(true);
    }
    
    @Nonnull
    @Override
    public PlayerDataMap<BlastKnightData> getDataMap() {
        return dataMap;
    }
    
    public int getShieldCharge(GamePlayer player) {
        return getPlayerData(player).getShieldCharge();
    }
    
    @Override
    public void onStart(@Nonnull GamePlayer player) {
        player.setItem(EquipmentSlot.OFF_HAND, shieldItem);
    }
    
    @Override
    public StoneCastle getFirstTalent() {
        return TalentRegistry.STONE_CASTLE;
    }
    
    @Override
    public Discharge getSecondTalent() {
        return TalentRegistry.DISCHARGE;
    }
    
    @Override
    public Talent getPassiveTalent() {
        return TalentRegistry.SHIELDED;
    }
    
    @Nonnull
    @Override
    public String getString(@Nonnull GamePlayer player) {
        if (player.cooldownManager.hasCooldown(shieldRechargeCdKey)) {
            return "&7🛡 &l" + player.cooldownManager.getCooldownFormatted(shieldRechargeCdKey);
        }
        
        return "&5&l✨ &l" + getShieldCharge(player);
    }
    
    private class BlastKnightUltimate extends UltimateTalent {
        
        @DisplayField private final double ultimateRadius = 7.0d;
        @DisplayField private final double initialShieldCapacity = 10;
        @DisplayField private final double shieldCapacity = 50;
        
        public BlastKnightUltimate() {
            super(BlastKnight.this, "Nanite Rush", 60);
            
            setDescription("""
                           Instantly release a &dNanite Swarm&7 that &brushes&7 upwards, creating a &eStone Shield&7 and rapidly &aregenerates&7 all existing shields.
                           """
            );
            
            setType(TalentType.SUPPORT);
            setMaterial(Material.PURPLE_DYE);
            setCooldownSec(30);
            setDuration(30);
        }
        
        @Nonnull
        @Override
        public UltimateInstance newInstance(@Nonnull GamePlayer player, boolean isFullyCharged) {
            final double shieldPerTick = (shieldCapacity - initialShieldCapacity) / (getDuration() - 1);
            final Location location = player.getLocation();
            
            return new UltimateInstance() {
                @Override
                public void onExecute() {
                    nearbyEntities(player).forEach(entity -> {
                        entity.setShield(new StoneShield(entity, shieldCapacity, initialShieldCapacity));
                    });
                }
                
                @Override
                public void onTick(int tick) {
                    nearbyEntities(player).forEach(entity -> {
                        final Shield shield = entity.getShield();
                        
                        if (shield instanceof StoneShield) {
                            shield.regenerate(shieldPerTick);
                        }
                    });
                    
                    // Fx
                    final float pitch = 0.5f + (1.5f / getDuration() * tick);
                    
                    player.spawnWorldParticle(location, Particle.WITCH, 50, ultimateRadius / 4, 0.1d, ultimateRadius / 4, 1f);
                    
                    player.playWorldSound(location, Sound.ITEM_FLINTANDSTEEL_USE, pitch);
                    player.playWorldSound(location, Sound.ENTITY_ENDER_DRAGON_FLAP, pitch);
                }
                
                private Collection<LivingGameEntity> nearbyEntities(GamePlayer player) {
                    return Collect.nearbyEntities(player.getLocation(), ultimateRadius, player::isSelfOrTeammate);
                }
                
            };
        }
    }
    
    private static class StoneShield extends Shield {
        public StoneShield(@Nonnull LivingGameEntity entity, double maxCapacity, double initialCapacity) {
            super(entity, maxCapacity, builder -> builder.initialCapacity(initialCapacity));
        }
    }
    
}
