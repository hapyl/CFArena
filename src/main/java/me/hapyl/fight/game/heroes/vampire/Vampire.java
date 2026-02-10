package me.hapyl.fight.game.heroes.vampire;

import com.google.common.collect.Sets;
import me.hapyl.eterna.module.inventory.Equipment;
import me.hapyl.eterna.module.locaiton.LocationHelper;
import me.hapyl.eterna.module.math.Tick;
import me.hapyl.eterna.module.player.PlayerSkin;
import me.hapyl.eterna.module.registry.Key;
import me.hapyl.eterna.module.util.BukkitUtils;
import me.hapyl.eterna.module.util.CollectionUtils;
import me.hapyl.fight.event.custom.GameDamageEvent;
import me.hapyl.fight.game.Named;
import me.hapyl.fight.game.attribute.AttributeType;
import me.hapyl.fight.game.attribute.HeroAttributes;
import me.hapyl.fight.game.damage.DamageCause;
import me.hapyl.fight.game.entity.BloodDebt;
import me.hapyl.fight.game.entity.GameEntity;
import me.hapyl.fight.game.entity.GamePlayer;
import me.hapyl.fight.game.entity.LivingGameEntity;
import me.hapyl.fight.game.heroes.*;
import me.hapyl.fight.game.heroes.equipment.HeroEquipment;
import me.hapyl.fight.game.heroes.ultimate.UltimateInstance;
import me.hapyl.fight.game.heroes.ultimate.UltimateTalent;
import me.hapyl.fight.game.talents.TalentRegistry;
import me.hapyl.fight.game.talents.TalentType;
import me.hapyl.fight.game.talents.vampire.BatTransferTalent;
import me.hapyl.fight.game.talents.vampire.BloodDebtTalent;
import me.hapyl.fight.game.talents.vampire.VampirePassive;
import me.hapyl.fight.game.task.TickingGameTask;
import me.hapyl.fight.game.weapons.Weapon;
import me.hapyl.fight.util.Collect;
import me.hapyl.fight.util.displayfield.DisplayField;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.Bat;
import org.bukkit.entity.Entity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.meta.trim.TrimMaterial;
import org.bukkit.inventory.meta.trim.TrimPattern;
import org.bukkit.util.Vector;

import javax.annotation.Nonnull;
import java.util.Set;

public class Vampire extends Hero implements Listener {
    
    public Vampire(@Nonnull Key key) {
        super(key, "Vorath");
        
        setDescription("""
                       One of the royal guards at the %s, believes that with enough firepower, everything is possible.
                       
                       Prefers NoSunBurn™ sunscreen.
                       """.formatted(Affiliation.CHATEAU.getName()));
        
        final HeroProfile profile = getProfile();
        profile.setArchetypes(Archetype.DAMAGE, Archetype.SELF_SUSTAIN, Archetype.SELF_BUFF);
        profile.setAffiliation(Affiliation.CHATEAU);
        profile.setGender(Gender.MALE);
        profile.setRace(Race.VAMPIRE);
        
        setItem("25a7007007d5a396d6049c71ab6ff5fedb6ca3e1753b3fd6f13bb6946a7e0daf");
        
        final HeroAttributes attributes = getAttributes();
        attributes.setMaxHealth(90);
        
        final HeroEquipment equipment = getEquipment();
        equipment.setChestPlate(191, 57, 66, TrimPattern.COAST, TrimMaterial.NETHERITE);
        equipment.setLeggings(191, 57, 66, TrimPattern.SILENCE, TrimMaterial.NETHERITE);
        
        setWeapon(Weapon.builder(Material.GHAST_TEAR, Key.ofString("vampires_fang"))
                        .name("Vampire's Fang")
                        .description("""
                                     A very sharp fang.
                                     """)
                        .damage(5.0d)
                        .damageCause(DamageCause.VAMPIRE_BITE)
        );
        
        setUltimate(new VampireUltimate());
    }
    
    @EventHandler
    public void handleGameDamageEvent(GameDamageEvent.Process ev) {
        final GameEntity damager = ev.getDamager();
        
        if (!(damager instanceof GamePlayer player) || !validatePlayer(player)) {
            return;
        }
        
        final BloodDebt bloodDebt = player.bloodDebt();
        
        if (!bloodDebt.hasDebt()) {
            return;
        }
        
        final BloodDebtTalent talent = getFirstTalent();
        
        // Increase damage based on blood debt
        final double bloodDebtAmount = bloodDebt.amount();
        final double decrement = Math.min(bloodDebtAmount, player.getMaxHealth() * talent.maxBloodDebtDecrementOfMaxhealth);
        final double damageIncrease = 1 + bloodDebtAmount * talent.damageIncreaseMultiplier;
        
        bloodDebt.decrement(decrement);
        ev.multiplyDamage(damageIncrease);
    }
    
    @Override
    public BloodDebtTalent getFirstTalent() {
        return TalentRegistry.BLOOD_DEBT;
    }
    
    @Override
    public BatTransferTalent getSecondTalent() {
        return TalentRegistry.BAT_TRANSFER;
    }
    
    @Override
    public VampirePassive getPassiveTalent() {
        return TalentRegistry.VAMPIRE_PASSIVE;
    }
    
    private class VampireUltimate extends UltimateTalent {
        
        @DisplayField private final int batsDuration = Tick.fromSeconds(20);
        
        @DisplayField private final int armyCount = 11;
        @DisplayField private final double homingSpeed = 0.5d;
        @DisplayField(suffix = " blocks") private final double homingRadius = 5;
        @DisplayField private final double damage = 10;
        
        @DisplayField(percentage = true) private final double bloodDebtAmount = 0.2d;
        @DisplayField(percentage = true) private final double healingPercentOfBloodDebt = 0.35d;
        @DisplayField(percentage = true) private final double bloodDebtHealingThreshold = 0.2d;
        
        private final double biteThreshold = 1.0d;
        
        public VampireUltimate() {
            super(Vampire.this, "Legion", 75);
            
            setDescription("""
                           Blow into the war horn, summoning a vampire army behind you.
                           
                           After a short delay, the army &ntransforms&7 into &6bats&7 and rushes forward, dealing &cdamage&7 and applying %1$s to hit &cenemies&7.
                           
                           If your own %1$s if &f&ngreater&7 than &b{bloodDebtHealingThreshold}&7 of %2$s, clear it and:
                            &8├ &7Heal for &b{healingPercentOfBloodDebt}&7 of the cleared debt.
                            &8└ &7Refresh &a%3$s&7 cooldown.
                           """.formatted(Named.BLOOD_DEBT, AttributeType.MAX_HEALTH, getFirstTalent().getName()));
            
            setMaterial(Material.GOAT_HORN);
            setType(TalentType.DAMAGE);
            
            setCastDurationSec(2.5f);
            setCooldownSec(35);
        }
        
        @Nonnull
        @Override
        public UltimateInstance newInstance(@Nonnull GamePlayer player, boolean isFullyCharged) {
            return new VampireUltimateInstance(this, player);
        }
        
    }
    
    private class VampireUltimateInstance extends UltimateInstance {
        private static final PlayerSkin SOLDIER_SKIN = PlayerSkin.of(
                "ewogICJ0aW1lc3RhbXAiIDogMTY0NzMyMzUzNDEwOSwKICAicHJvZmlsZUlkIiA6ICI4YjgyM2E1YmU0Njk0YjhiOTE0NmE5MWRhMjk4ZTViNSIsCiAgInByb2ZpbGVOYW1lIiA6ICJTZXBoaXRpcyIsCiAgInNpZ25hdHVyZVJlcXVpcmVkIiA6IHRydWUsCiAgInRleHR1cmVzIiA6IHsKICAgICJTS0lOIiA6IHsKICAgICAgInVybCIgOiAiaHR0cDovL3RleHR1cmVzLm1pbmVjcmFmdC5uZXQvdGV4dHVyZS84OTY5ZmYxN2Q5ZmMzNDlkNGEzMDViMDY1NjM2MDI4ZGI5MDBlMDMyMjMyN2QyYzQ3ZDZmNjI2MmYyYjBkMGYxIgogICAgfQogIH0KfQ==",
                "ZCT1O4LhteyDt6H7YKr3zxx3ElcCb/Xf2A65UNgL++00b8I7dKeWGotUnyjfVQRALux7LSEFgctpIZjvnpZj3dVBvQkOoEmpbcqDkMEpMbYehCjmKgOuUmwjEHuNblU8pf30rsOSFPP+my/ojdzOuT7FPq8nv4VHq4PZk66DivNKOA84I6jj2vnzag0oEaiCSgJZTYW3kukFkTdWYOI8WOG+qsoRDFC6wXZ2gL6eQYt43lg6ozk6AORS3aMWo3Fa4XK6LikcV5BvpqW18Dsdf94v2AO5HTO0Lz7bTWPFF9+Pxru11LWAiARGsbQmsrCd4hKzkMhzZMVgmVJe5E9zF3ORheZcbtYX5mIwepO1MJiQ8U9R42g+p/z/xkP0sKEh7oj6k6HGjgtrJkqL/Co1RFmL6E97Q58H93PGOX55CMW6nPP+JevXOa20oyu/TwjQ15xVNSJuABCyUxLy56xqbQXZGp8KHaKJShpfMQYfEE0W5sktOZfJ/2R4uqfL/kkZ+lF9/luR3p5MaY87B9oi0U7K8JykbOz8sOeC6DIOc82mm9FX1cFmokCGJ4ucI6qAzkU7+pA53XeOw7hz+9trCIRMXAzU8bSUeJ6xS0bgs94eu5hzRUWLkRtYIROeMGEwMOe1JB3QjdHT8l/63BZqqHEmXpqAMxoygnMvbyodbsk="
        );
        
        private static final EntityEquipment[] EQUIPMENT = {
                Equipment.builder().mainHand(Material.IRON_SWORD).build(),
                Equipment.builder().mainHand(Material.BOW).build(),
                Equipment.builder().mainHand(Material.NETHERITE_SWORD).build(),
                Equipment.builder().mainHand(Material.MACE).build(),
                };
        private static final int SNEAK_TICK_THRESHOLD = 5;
        
        private final VampireUltimate ultimate;
        private final GamePlayer player;
        
        private VampireUltimateInstance(VampireUltimate ultimate, GamePlayer player) {
            this.ultimate = ultimate;
            this.player = player;
        }
        
        @Override
        public void onCastStart() {
            // Summon army
            summonArmy();
            
            // Fx
            player.playWorldSound(Sound.ITEM_GOAT_HORN_SOUND_2, 1.25f);
        }
        
        @Override
        public void onCastTick(int tick) {
            final int castDuration = ultimate.getCastDuration() - SNEAK_TICK_THRESHOLD;
            
        }
        
        @Override
        public void onExecute() {
            final Set<Bat> bats = Sets.newHashSet();
            
            // Transform into bats
            
            // Bat task
            new TickingGameTask() {
                @Override
                public void onTaskStop() {
                    CollectionUtils.forEachAndClear(bats, Entity::remove);
                }
                
                private void doDamage(Bat bat, LivingGameEntity entity) {
                    bat.remove();
                    
                    entity.damage(ultimate.damage, player, DamageCause.BAT_BITE_NO_TICK);
                    entity.bloodDebt().incrementOfMaxHealth(ultimate.bloodDebtAmount);
                    
                    final Location location = bat.getLocation();
                    
                    player.playWorldSound(location, Sound.ENTITY_FOX_BITE, 0.0f);
                }
                
                @Override
                public void run(int tick) {
                    bats.removeIf(Bat::isDead);
                    
                    if (tick >= ultimate.batsDuration || bats.isEmpty()) {
                        cancel();
                        return;
                    }
                    
                    // Push bats
                    bats.forEach(bat -> {
                        final Location location = bat.getLocation();
                        
                        // Bats home towards closest enemies because it would be impossible to hit otherwise
                        final LivingGameEntity nearestEntity = Collect.nearestEntity(location, ultimate.homingRadius, player::isNotSelfOrTeammate);
                        final Vector direction;
                        
                        if (nearestEntity != null) {
                            direction = nearestEntity.getMidpointLocation().toVector().subtract(location.toVector()).normalize();
                            
                            final double distance = nearestEntity.getEyeLocation().distanceSquared(location);
                            
                            if (distance <= ultimate.biteThreshold) {
                                doDamage(bat, nearestEntity);
                                return;
                            }
                        }
                        else {
                            direction = location.getDirection();
                        }
                        
                        // Transfer
                        location.add(direction.multiply(ultimate.homingSpeed));
                        
                        // Collision detection
                        if (!location.getBlock().isEmpty()) {
                            bat.remove();
                            player.spawnWorldParticle(location, Particle.LARGE_SMOKE, 3, 0.1, 0.2, 0.1, 0.05f);
                            return;
                        }
                        
                        bat.teleport(location);
                    });
                }
            }.runTaskTimer(0, 1);
            
            // Apply effects
            final BloodDebt bloodDebt = player.bloodDebt();
            final double bloodDebtAmount = bloodDebt.amount();
            
            if (bloodDebtAmount >= (player.getMaxHealth() * ultimate.bloodDebtHealingThreshold)) {
                bloodDebt.reset();
                
                player.heal(bloodDebtAmount * ultimate.healingPercentOfBloodDebt);
                Vampire.this.getFirstTalent().stopCooldown(player);
            }
            
            // Fx
            player.playWorldSound(Sound.ENTITY_SNIFFER_DEATH, 0.0f);
        }
        
        private void summonArmy() {
        }
        
        private Location pickRandomLocationBehindPlayer() {
            final double zOffset = player.random.nextDouble(3, 6);
            final double xOffsetPositive = player.random.nextDoubleBool(3);
            final double xOffsetNegative = player.random.nextDoubleBool(3);
            
            Location location = player.getLocationBehindFromEyes(zOffset);
            location.setPitch(0.0f);
            
            location = LocationHelper.getToTheLeft(location, xOffsetPositive);
            location = LocationHelper.getToTheRight(location, xOffsetNegative);
            
            return BukkitUtils.anchorLocation(location);
        }
    }
}
