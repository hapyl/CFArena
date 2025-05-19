package me.hapyl.fight.game.heroes.bounty_hunter;

import me.hapyl.eterna.module.math.Tick;
import me.hapyl.eterna.module.registry.Key;
import me.hapyl.fight.Message;
import me.hapyl.fight.event.DamageInstance;
import me.hapyl.fight.event.custom.GameDamageEvent;
import me.hapyl.fight.event.custom.GameDeathEvent;
import me.hapyl.fight.game.attribute.AttributeType;
import me.hapyl.fight.game.attribute.ModifierSource;
import me.hapyl.fight.game.attribute.ModifierType;
import me.hapyl.fight.game.attribute.SnapshotAttributes;
import me.hapyl.fight.game.damage.DamageCause;
import me.hapyl.fight.game.entity.GameEntity;
import me.hapyl.fight.game.entity.GamePlayer;
import me.hapyl.fight.game.entity.LivingGameEntity;
import me.hapyl.fight.game.heroes.*;
import me.hapyl.fight.game.heroes.equipment.HeroEquipment;
import me.hapyl.fight.game.heroes.ultimate.UltimateInstance;
import me.hapyl.fight.game.heroes.ultimate.UltimateTalent;
import me.hapyl.fight.game.talents.TalentRegistry;
import me.hapyl.fight.game.talents.TalentType;
import me.hapyl.fight.game.talents.bounty_hunter.BountyHunterPassive;
import me.hapyl.fight.game.talents.bounty_hunter.GrappleHookTalent;
import me.hapyl.fight.game.talents.bounty_hunter.ShortyShotgun;
import me.hapyl.fight.game.talents.bounty_hunter.SmokeBombTalent;
import me.hapyl.fight.util.Collect;
import me.hapyl.fight.util.collection.player.PlayerDataMap;
import me.hapyl.fight.util.collection.player.PlayerMap;
import me.hapyl.fight.util.displayfield.DisplayField;
import me.hapyl.fight.util.displayfield.DisplayFieldProvider;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.inventory.meta.trim.TrimMaterial;
import org.bukkit.inventory.meta.trim.TrimPattern;
import org.bukkit.util.Vector;

import javax.annotation.Nonnull;

public class BountyHunter extends Hero implements DisplayFieldProvider, PlayerDataHandler<BountyHunterData>, Listener {
    
    private final PlayerDataMap<BountyHunterData> playerMap = PlayerMap.newDataMap(player -> new BountyHunterData(this, player));
    
    public BountyHunter(@Nonnull Key key) {
        super(key, "Bounty Hunter");
        
        final HeroProfile profile = getProfile();
        profile.setArchetypes(Archetype.MOBILITY, Archetype.DAMAGE, Archetype.TALENT_DAMAGE, Archetype.POWERFUL_ULTIMATE);
        profile.setAffiliation(Affiliation.MERCENARY);
        profile.setGender(Gender.FEMALE);
        
        setDescription("""
                       She is a skilled bounty hunter.
                       
                       `Jackpot! Everyone here's got a bounty on their head.`
                       """);
        setItem("cf4f866f1432f324e31b0a502e6e9ebccd7a66f474f1ca9cb0cfab879ea22ce0");
        
        setWeapon(new BountyHunterWeapon());
        
        final HeroEquipment equipment = getEquipment();
        equipment.setChestPlate(50, 54, 57, TrimPattern.SILENCE, TrimMaterial.NETHERITE);
        equipment.setLeggings(80, 97, 68);
        equipment.setBoots(160, 101, 64, TrimPattern.SILENCE, TrimMaterial.IRON);
        
        setUltimate(new BountyHunterUltimate());
        
        talkBehaviour.sound(Sound.ENTITY_VILLAGER_NO, 1.25f);
    }
    
    @Nonnull
    @Override
    public BountyHunterWeapon getWeapon() {
        return (BountyHunterWeapon) super.getWeapon();
    }
    
    @EventHandler
    public void handleDamagePreProcessEvent(GameDamageEvent.PreProcess ev) {
        final SnapshotAttributes damager = ev.damager();
        
        if (damager == null || !(damager.entity() instanceof GamePlayer player) || !validatePlayer(player)) {
            return;
        }
        
        final LivingGameEntity entity = ev.entity().entity();
        final BountyHunterPassive passive = getPassiveTalent();
        final BountyHunterWeapon weapon = getWeapon();
        
        // Bounty
        final BountyHunterData data = getPlayerData(player);
        double defenseIgnore = 0;
        
        if (data.bounty != null) {
            final LivingGameEntity bountyEntity = data.bounty.entity();
            
            if (!entity.equals(bountyEntity)) {
                player.sendMessage(Message.ERROR, "Cannot damage non-bounty enemy!");
                player.playSound(Sound.ENTITY_GHAST_SCREAM, 1.0f);
                
                ev.setCancelled(true);
                return;
            }
            
            final int hits = data.bounty.incrementHit();
            
            // Ignore def%
            ev.entity().multiply(AttributeType.DEFENSE, -weapon.defenseIgnore);
            
            // Apply bleeding & start cooldown
            if (hits >= weapon.hitsForBleed) {
                weapon.affect(player, bountyEntity);
                
                data.bounty.remove(BloodBounty.RemoveCause.BOUNTY_COMPLETE);
                data.bounty = null;
            }
        }
        
        // Ultimate buff
        final BountyHunterUltimate ultimate = getUltimate();
        
        if (player.getAttributes().hasModifier(ultimate.modifierSource)) {
            ev.entity().multiply(AttributeType.DEFENSE, -ultimate.defenseIgnore);
        }
        
        // Backstab
        final Vector vector = player.getLocation().toVector().subtract(entity.getLocation().toVector()).setY(0).normalize();
        final double dot = vector.dot(entity.getLocation().getDirection().setY(0).normalize());
        
        if (dot <= passive.backstabDotThreshold) {
            damager.add(AttributeType.CRIT_CHANCE, passive.critChanceIncrease);
            
            ev.initialDamage(ev.initialDamage() * passive.backstabDamageMultiplier);
        }
    }
    
    @Override
    public void processDamageAsVictim(@Nonnull DamageInstance instance) {
        final GamePlayer player = instance.getEntityAsPlayer();
        final SmokeBombTalent smokeBomb = getThirdTalent();
        
        final double health = player.getHealth();
        final double maxHealth = player.getMaxHealth();
        final double threshold = maxHealth * smokeBomb.healthThreshold;
        
        if (health > threshold && health - instance.getDamage() <= threshold) {
            smokeBomb.trigger(player);
        }
    }
    
    @Override
    public ShortyShotgun getFirstTalent() {
        return TalentRegistry.SHORTY;
    }
    
    @Override
    public GrappleHookTalent getSecondTalent() {
        return TalentRegistry.GRAPPLE;
    }
    
    @Override
    public SmokeBombTalent getThirdTalent() {
        return TalentRegistry.SMOKE_BOMB;
    }
    
    @Override
    public BountyHunterPassive getPassiveTalent() {
        return TalentRegistry.BOUNTY_HUNTER_PASSIVE;
    }
    
    @Nonnull
    @Override
    public PlayerDataMap<BountyHunterData> getDataMap() {
        return playerMap;
    }
    
    @EventHandler
    public void handleGameDeathEvent(GameDeathEvent ev) {
        final LivingGameEntity entity = ev.getEntity();
        
        // Bounty
        playerMap.values()
                 .stream()
                 .filter(data -> data.bounty != null && data.bounty.entity().equals(entity))
                 .forEach(data -> {
                     data.bounty.remove(BloodBounty.RemoveCause.ENTITY_DIED);
                     data.bounty = null;
                 });
        
        // Ultimate buff
        final GameEntity killer = ev.getKiller();
        
        if (!(killer instanceof GamePlayer player) || !validatePlayer(player) || ev.getCause() != DamageCause.BACKSTAB) {
            return;
        }
        
        final BountyHunterUltimate ultimate = getUltimate();
        
        // Buff bounty hunter
        player.getAttributes().addModifier(
                ultimate.modifierSource, ultimate.buffDuration, modifier -> modifier
                        .of(AttributeType.SPEED, ModifierType.FLAT, ultimate.speedIncrease)
        );
        
        player.playSound(Sound.ENTITY_DONKEY_HURT, 0.0f);
        player.playSound(Sound.ENTITY_BLAZE_HURT, 1.25f);
    }
    
    @Nonnull
    @Override
    public BountyHunterUltimate getUltimate() {
        return (BountyHunterUltimate) super.getUltimate();
    }
    
    public class BountyHunterUltimate extends UltimateTalent {
        
        @DisplayField private final double damage = 35;
        @DisplayField private final double maxDistance = 25;
        
        @DisplayField private final double speedIncrease = 30;
        @DisplayField(percentage = true) private final double defenseIgnore = 0.25;
        
        @DisplayField private final int buffDuration = Tick.fromSeconds(15);
        
        private final ModifierSource modifierSource = new ModifierSource(Key.ofString("blood_bounty"));
        
        BountyHunterUltimate() {
            super(BountyHunter.this, "Severance", 70);
            
            setDescription("""
                           Instantly teleport behind the &etarget&7 &cenemy&7, stabbing them from behind.
                           
                           If you &4kill&7 the enemy you stabbed, gain the following &abuffs&7 for &b{buffDuration}&7:
                            &8├&7 Increased %s.
                            &8└&7 Your hits ignore &2%.0f%%&7 of victim's %s.
                           """.formatted(AttributeType.SPEED, defenseIgnore * 100, AttributeType.DEFENSE));
            
            setType(TalentType.DAMAGE);
            setMaterial(Material.FLOW_BANNER_PATTERN);
            
            setDurationSec(1);
        }
        
        @Nonnull
        @Override
        public UltimateInstance newInstance(@Nonnull GamePlayer player, boolean isFullyCharged) {
            final LivingGameEntity target = Collect.targetEntityRayCast(player, maxDistance, 1.25, player::isNotSelfOrTeammate);
            
            if (target == null) {
                return error("No valid target!");
            }
            
            final Location location = target.getLocation();
            final Vector behind = location.getDirection().normalize().multiply(-1).setY(0);
            
            location.add(behind);
            
            final Block block = location.getBlock();
            
            if (!block.isPassable() || !block.getRelative(BlockFace.UP).isPassable()) {
                return error("The target location is not safe!");
            }
            
            return execute(() -> {
                final Location playerLocation = player.getLocation();
                
                player.teleport(location);
                target.damage(damage, player, DamageCause.BACKSTAB);
                
                player.swingMainHand();
                
                // Fx
                player.playWorldSound(location, Sound.ENTITY_ENDER_DRAGON_FLAP, 0.0f);
                player.playWorldSound(location, Sound.ENTITY_IRON_GOLEM_REPAIR, 1.25f);
                
                player.spawnWorldParticle(playerLocation, Particle.LARGE_SMOKE, 20, 0.1, 0.5, 0.1, 0.25f);
                player.spawnWorldParticle(location, Particle.LARGE_SMOKE, 20, 0.1, 0.5, 0.1, 0.25f);
            });
        }
        
    }
}
