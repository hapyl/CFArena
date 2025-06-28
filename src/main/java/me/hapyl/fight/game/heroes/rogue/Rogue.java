package me.hapyl.fight.game.heroes.rogue;

import me.hapyl.eterna.module.math.Tick;
import me.hapyl.eterna.module.registry.Key;
import me.hapyl.eterna.module.util.SmallCaps;
import me.hapyl.fight.event.custom.GameDeathEvent;
import me.hapyl.fight.game.Named;
import me.hapyl.fight.game.attribute.HeroAttributes;
import me.hapyl.fight.game.damage.DamageCause;
import me.hapyl.fight.game.dot.DotType;
import me.hapyl.fight.game.entity.GamePlayer;
import me.hapyl.fight.game.entity.LivingGameEntity;
import me.hapyl.fight.game.heroes.*;
import me.hapyl.fight.game.heroes.equipment.HeroEquipment;
import me.hapyl.fight.game.heroes.ultimate.UltimateInstance;
import me.hapyl.fight.game.heroes.ultimate.UltimateTalent;
import me.hapyl.fight.game.talents.TalentRegistry;
import me.hapyl.fight.game.talents.rogue.ExtraCut;
import me.hapyl.fight.game.talents.rogue.SecondWind;
import me.hapyl.fight.game.talents.rogue.Swayblade;
import me.hapyl.fight.game.task.TickingGameTask;
import me.hapyl.fight.game.ui.UIComponent;
import me.hapyl.fight.game.weapons.Weapon;
import me.hapyl.fight.util.Collect;
import me.hapyl.fight.util.ItemStackRandomizedData;
import me.hapyl.fight.util.collection.player.PlayerDataMap;
import me.hapyl.fight.util.collection.player.PlayerMap;
import me.hapyl.fight.util.displayfield.DisplayField;
import me.hapyl.fight.util.displayfield.DisplayFieldProvider;
import org.bukkit.*;
import org.bukkit.entity.Item;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.inventory.meta.trim.TrimMaterial;
import org.bukkit.inventory.meta.trim.TrimPattern;
import org.bukkit.potion.PotionEffectType;

import javax.annotation.Nonnull;

public class Rogue extends Hero implements PlayerDataHandler<RogueData>, UIComponent, DisplayFieldProvider, Listener {
    
    private final PlayerDataMap<RogueData> rogueData = PlayerMap.newDataMap(RogueData::new);
    private final String secondWindSmallCaps = "%s %s".formatted(Named.SECOND_WIND.getPrefix(), SmallCaps.format(Named.SECOND_WIND.getName()));
    
    public Rogue(@Nonnull Key key) {
        super(key, "Rogue");
        
        final HeroProfile profile = getProfile();
        profile.setArchetypes(Archetype.DAMAGE, Archetype.MELEE);
        profile.setAffiliation(Affiliation.MERCENARY);
        profile.setGender(Gender.MALE);
        
        setDescription("""
                       The most selfish member of the mercenaries.
                       """);
        
        setItem("73abc6192f1a559ed566e50fddf6a7b50c42cb0a15862091411487ace1d60ab8");
        
        final HeroAttributes attributes = getAttributes();
        attributes.setMaxHealth(60);
        attributes.setSpeed(130);
        attributes.setAttackSpeed(150);
        
        final HeroEquipment equipment = getEquipment();
        
        equipment.setChestPlate(Material.NETHERITE_CHESTPLATE, TrimPattern.WARD, TrimMaterial.NETHERITE);
        equipment.setLeggings(36, 14, 4, TrimPattern.DUNE, TrimMaterial.NETHERITE);
        equipment.setBoots(23, 7, 0, TrimPattern.SILENCE, TrimMaterial.NETHERITE);
        
        setWeapon(Weapon.builder(Material.GOLDEN_SWORD, Key.ofString("scarificial_dagger"))
                        .name("Sacrificial Dagger")
                        .description("""
                                     An ornate ceremonial dagger.
                                     
                                     Its small size allows for fast swings.
                                     """)
                        .damage(3.0d)
                        .damageCause(DamageCause.ROGUE_ATTACK)
        );
        
        setUltimate(new RogueUltimate());
    }
    
    @EventHandler(ignoreCancelled = true)
    public void handleDeath(GameDeathEvent ev) {
        if (!(ev.getEntity() instanceof GamePlayer player)) {
            return;
        }
        
        if (player.isDeadOrRespawning() || !validatePlayer(player)) {
            return;
        }
        
        final RogueData playerData = getPlayerData(player);
        
        if (!playerData.secondWindAvailable) {
            return;
        }
        
        playerData.secondWindAvailable = false;
        ev.setCancelled(true);
        
        player.setHealth(1); // Force set health to 1 because I SAID SO
        getPassiveTalent().enter(player);
    }
    
    @Override
    public ExtraCut getFirstTalent() {
        return TalentRegistry.EXTRA_CUT;
    }
    
    @Override
    public Swayblade getSecondTalent() {
        return TalentRegistry.SWAYBLADE;
    }
    
    @Override
    public SecondWind getPassiveTalent() {
        return TalentRegistry.SECOND_WIND;
    }
    
    @Nonnull
    @Override
    public PlayerDataMap<RogueData> getDataMap() {
        return rogueData;
    }
    
    @Nonnull
    @Override
    public String getString(@Nonnull GamePlayer player) {
        final boolean secondWindAvailable = getPlayerData(player).secondWindAvailable;
        
        return (secondWindAvailable ? "&f&l" : "&8") + secondWindSmallCaps;
    }
    
    private class RogueUltimate extends UltimateTalent {
        
        @DisplayField private final double explosionRadius = 4.0d;
        @DisplayField private final double explosionDamage = 30.0d;
        @DisplayField private final int maxExplosionDelay = Tick.fromSeconds(4);
        @DisplayField private final double magnitude = 1.3d;
        @DisplayField private final int bleedDuration = 60;
        @DisplayField private final short bleedStacks = 4;
        
        public RogueUltimate() {
            super(Rogue.this, "Pipe Bomb", 70);
            
            setDescription("""
                           Equip a hand-made Pipe Bomb and light the fuse, then throw it.
                           
                           The bomb &4explodes&7 upon contact with an &cenemy&7 or the &bground&7, dealing &cAoE &cdamage&7 and applies &b{bleedStacks}&7 stacks of %s.
                           &8&o;;The explosion can damage yourself.
                           
                           If at least &none&7 enemy was &chit&7, &nrefresh&7 %s charges.
                           """.formatted(DotType.BLEED, Named.SECOND_WIND)
            );
            
            setMaterial(Material.LIGHTNING_ROD);
            
            setCastDurationSec(0.75f);
        }
        
        @Nonnull
        @Override
        public UltimateInstance newInstance(@Nonnull GamePlayer player, boolean isFullyCharged) {
            return new UltimateInstance() {
                @Override
                public void onCastStart() {
                    player.playWorldSound(Sound.ITEM_FLINTANDSTEEL_USE, 1.25f);
                    player.addPotionEffect(PotionEffectType.SLOWNESS, 2, getCastDuration());
                }
                
                @Override
                public void onCastEnd() {
                    player.swingOffHand();
                    player.playWorldSound(Sound.ENTITY_CREEPER_PRIMED, 1.25f);
                }
                
                @Override
                public void onExecute() {
                    final World world = player.getWorld();
                    final Location location = player.getEyeLocation();
                    final Item item = world.dropItem(location, ItemStackRandomizedData.of(Material.LIGHTNING_ROD));
                    
                    item.setPickupDelay(10000);
                    item.setUnlimitedLifetime(true);
                    item.setVelocity(location.getDirection().normalize().multiply(magnitude));
                    
                    // Explode
                    new TickingGameTask() {
                        @Override
                        public void run(int tick) {
                            final LivingGameEntity targetEntity = Collect.nearestEntity(item.getLocation(), 1d, player::isNotSelfOrTeammate);
                            
                            if (targetEntity != null || item.isOnGround() || tick > maxExplosionDelay) {
                                explode();
                                return;
                            }
                            
                            // Fx
                            final Location location = item.getLocation();
                            
                            final int mod = tick % 3;
                            
                            player.playWorldSound(
                                    location, Sound.BLOCK_NOTE_BLOCK_HAT,
                                    mod == 0
                                    ? 0.75f
                                    : mod == 1 ? 1.0f
                                               : mod == 2 ? 1.25f
                                                          : 1.5f
                            );
                            player.spawnWorldParticle(location, Particle.CRIT, 1);
                        }
                        
                        private void explode() {
                            final Location location = item.getLocation();
                            boolean hitEnemy = false;
                            
                            for (LivingGameEntity entity : Collect.nearbyEntities(location, explosionRadius)) {
                                if (player.isTeammate(entity)) {
                                    continue;
                                }
                                
                                final boolean isSelf = player.equals(entity);
                                
                                if (!hitEnemy && !isSelf) {
                                    hitEnemy = true;
                                }
                                
                                entity.damageNoKnockback(explosionDamage, player, DamageCause.PIPE_BOMB);
                                entity.addDotStacks(DotType.BLEED, bleedStacks, player);
                            }
                            
                            // Refresh passive
                            if (hitEnemy) {
                                getPlayerData(player).refreshSecondWind();
                            }
                            
                            // Fx
                            player.playWorldSound(location, Sound.ENTITY_GENERIC_EXPLODE, 1.25f);
                            player.spawnWorldParticle(location, Particle.EXPLOSION_EMITTER, 1);
                            
                            item.remove();
                            cancel();
                        }
                    }.runTaskTimer(0, 1);
                }
            };
        }
    }
}
