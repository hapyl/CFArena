package me.hapyl.fight.game.heroes.juju;

import com.google.common.collect.Maps;
import me.hapyl.eterna.module.registry.Key;
import me.hapyl.fight.CF;
import me.hapyl.fight.event.custom.ProjectilePostLaunchEvent;
import me.hapyl.fight.game.GameInstance;
import me.hapyl.fight.game.effect.EffectType;
import me.hapyl.fight.game.entity.GamePlayer;
import me.hapyl.fight.game.heroes.*;
import me.hapyl.fight.game.heroes.equipment.HeroEquipment;
import me.hapyl.fight.game.heroes.ultimate.UltimateInstance;
import me.hapyl.fight.game.heroes.ultimate.UltimateTalent;
import me.hapyl.fight.game.talents.TalentRegistry;
import me.hapyl.fight.game.talents.TalentType;
import me.hapyl.fight.game.talents.juju.ArrowShieldTalent;
import me.hapyl.fight.game.talents.juju.ClimbPassive;
import me.hapyl.fight.game.talents.juju.TricksOfTheJungle;
import me.hapyl.fight.game.task.GameTask;
import me.hapyl.fight.game.ui.UIComplexComponent;
import me.hapyl.fight.game.weapons.BowWeapon;
import me.hapyl.fight.util.CFUtils;
import me.hapyl.fight.util.collection.player.PlayerDataMap;
import me.hapyl.fight.util.collection.player.PlayerMap;
import org.bukkit.*;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.player.PlayerInputEvent;
import org.bukkit.inventory.meta.trim.TrimMaterial;
import org.bukkit.inventory.meta.trim.TrimPattern;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.projectiles.ProjectileSource;
import org.bukkit.util.Vector;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;
import java.util.Map;

public class JuJu extends Hero implements Listener, UIComplexComponent, PlayerDataHandler<JujuData> {
    
    private final PlayerDataMap<JujuData> playerData = PlayerMap.newDataMap(JujuData::new);
    private final Map<Arrow, ArrowType> arrowData = Maps.newHashMap();
    
    private final BlockData climbingBlockData = Material.GRAY_CONCRETE.createBlockData();
    
    public JuJu(@Nonnull Key key) {
        super(key, "Juju");
        
        final HeroProfile profile = getProfile();
        profile.setArchetypes(Archetype.HEXBANE, Archetype.DAMAGE, Archetype.POWERFUL_ULTIMATE, Archetype.DEFENSE);
        profile.setAffiliation(Affiliation.THE_JUNGLE);
        profile.setGender(Gender.MALE);
        
        setMinimumLevel(5);
        
        setDescription("""
                       A bandit from the depths of the jungle. Highly skilled in range combat.
                       """);
        setItem("eeddf1784a05c0dcb9d81cce65a864243909a878c3b4d0c8ff72f7c5a647d3aa");
        
        final HeroEquipment equipment = getEquipment();
        equipment.setChestPlate(99, 49, 49, TrimPattern.WAYFINDER, TrimMaterial.NETHERITE);
        equipment.setLeggings(62, 51, 40, TrimPattern.WAYFINDER, TrimMaterial.NETHERITE);
        equipment.setBoots(36, 22, 17, TrimPattern.EYE, TrimMaterial.NETHERITE);
        
        setWeapon(BowWeapon.of(
                Key.ofString("twisted"),
                "Twisted",
                "A bow made of anything you can find in the middle of the jungle.",
                4.0
        ));
        
        setUltimate(new JujuUltimate());
    }
    
    @SuppressWarnings("UnstableApiUsage")
    @EventHandler
    public void handleClimbing(PlayerInputEvent ev) {
        final GamePlayer player = CF.getPlayer(ev);
        
        if (!validatePlayer(player)) {
            return;
        }
        
        final Input input = ev.getInput();
        final JujuData data = getPlayerData(player);
        
        if (!data.isClimbing) {
            return;
        }
        
        // Go higher
        if (input.isJump()) {
            final Location location = player.getLocation();
            final Vector vector = location.getDirection().normalize().multiply(0.25d).setY(0.3d);
            
            player.setVelocity(vector);
            
            // Fx
            player.playWorldSound(Sound.BLOCK_LADDER_STEP, 0.0f);
            player.swingHand(player.random.nextBoolean());
        }
        // DO A FLIP
        else if (input.isSneak()) {
            final Location location = player.getLocation();
            location.setYaw(location.getYaw() + 180);
            player.teleport(location);
            
            final Vector vector = location.getDirection().multiply(1.25d);
            player.setVelocity(vector);
            
            // Fx
            player.playWorldSound(Sound.ENTITY_CAMEL_DASH, 0.75f);
        }
        
    }
    
    @EventHandler()
    public void handleBowShoot(ProjectilePostLaunchEvent ev) {
        final Projectile projectile = ev.getProjectile();
        
        if (!(projectile instanceof Arrow arrow)) {
            return;
        }
        
        final GamePlayer player = ev.getShooter();
        
        if (!validatePlayer(player)) {
            return;
        }
        
        final JujuData data = getPlayerData(player);
        final ArrowType arrowType = data.arrowType();
        
        if (arrowType == null) {
            return;
        }
        
        arrowType.onShoot(player, arrow);
        arrowData.put(arrow, arrowType);
    }
    
    @EventHandler()
    public void handleProjectileHitEvent(ProjectileHitEvent ev) {
        final Projectile projectile = ev.getEntity();
        
        if (!(projectile instanceof Arrow arrow) || !(projectile.getShooter() instanceof Player player)) {
            return;
        }
        
        final GamePlayer gamePlayer = CF.getPlayer(player);
        final ArrowType arrowType = this.arrowData.get(arrow);
        
        if (gamePlayer != null && arrowType != null) {
            arrowType.onHit(gamePlayer, arrow);
        }
    }
    
    @Override
    public void onDeath(@Nonnull GamePlayer player) {
        // Remove all player-owned arrows
        arrowData.keySet().removeIf(arrow -> {
            final ProjectileSource shooter = arrow.getShooter();
            
            return shooter instanceof Player shooterPlayer && shooterPlayer.equals(player.getEntity());
        });
    }
    
    @Override
    public void onStop(@Nonnull GameInstance instance) {
        arrowData.keySet().forEach(Arrow::remove);
        arrowData.clear();
    }
    
    @Override
    public void onStart(@Nonnull GamePlayer player) {
        player.setArrowItem();
    }
    
    @Nullable
    @Override
    public List<String> getStrings(@Nonnull GamePlayer player) {
        final JujuData data = getPlayerData(player);
        final ArrowType type = data.arrowType();
        final int climbCooldown = getPassiveTalent().getCooldownTimeLeft(player);
        
        return List.of(
                type != null ? "&a\uD83C\uDFF9 &l%s".formatted(type.getName()) : "",
                climbCooldown > 0 ? "&2&2\uD83E\uDDD7 &l%s".formatted(CFUtils.formatTick(climbCooldown)) : ""
        );
    }
    
    @Override
    public void onStart(@Nonnull GameInstance instance) {
        final ClimbPassive climbTalent = getPassiveTalent();
        
        new GameTask() {
            @Override
            public void run() {
                getAlivePlayers().forEach(player -> {
                    final JujuData data = getPlayerData(player);
                    
                    if (climbTalent.isOnCooldown(player)) {
                        return;
                    }
                    
                    final boolean isHuggingWall = isHuggingWall(player);
                    
                    // Enter
                    if ((isHuggingWall && isHuggingWall(player.getEyeLocation()))
                            && !data.isClimbing
                            && !player.isOnGround()) {
                        player.playWorldSound(Sound.ENTITY_HORSE_ARMOR, 0.75f);
                        player.playWorldSound(Sound.BLOCK_LADDER_STEP, 0.75f);
                        
                        data.isClimbing = true;
                    }
                    else if (data.isClimbing) {
                        // Loop
                        if (isHuggingWall) {
                            player.addPotionEffect(PotionEffectType.SLOW_FALLING, 1, 2);
                            player.addPotionEffect(PotionEffectType.SLOWNESS, 3, 2);
                            
                            // Fx
                            player.sendSubtitle("&8&l\uD83E\uDE9C&8\uD83E\uDE9C&8&l\uD83E\uDE9C", 0, 2, 0);
                            player.spawnWorldParticle(player.getLocation(), Particle.FALLING_DUST, 3, 0.1, 0, 0.1, 0, climbingBlockData);
                            return;
                        }
                        
                        // Stopped Climbing
                        data.isClimbing = false;
                        climbTalent.startCooldown(player);
                        
                        // Add a little boost
                        final Location location = player.getLocation();
                        final Vector vector = location.getDirection().normalize().multiply(0.4d).add(new Vector(0.0d, 0.5d, 0.0d));
                        
                        player.setVelocity(vector);
                        player.addEffect(EffectType.SLOW_FALLING, 1, 15);
                        player.addEffect(EffectType.FALL_DAMAGE_RESISTANCE, 100);
                        
                        // Fx
                        player.playWorldSound(Sound.ENTITY_HORSE_SADDLE, 0.75f);
                    }
                });
                
                // Arrows
                arrowData.keySet().removeIf(Arrow::isDead);
                arrowData.forEach((arrow, type) -> {
                    // should never ever happen but just in case
                    if (arrow.getShooter() instanceof Player player) {
                        final GamePlayer gamePlayer = CF.getPlayer(player);
                        if (gamePlayer == null) {
                            return;
                        }
                        
                        type.onTick(gamePlayer, arrow);
                    }
                });
            }
        }.runTaskTimer(0, 1);
    }
    
    @Override
    @Nonnull
    public ArrowShieldTalent getFirstTalent() {
        return TalentRegistry.ARROW_SHIELD;
    }
    
    @Override
    @Nonnull
    public TricksOfTheJungle getSecondTalent() {
        return TalentRegistry.TRICKS_OF_THE_JUNGLE;
    }
    
    @Override
    public ClimbPassive getPassiveTalent() {
        return TalentRegistry.JUJU_PASSIVE;
    }
    
    @Nonnull
    @Override
    public PlayerDataMap<JujuData> getDataMap() {
        return playerData;
    }
    
    private boolean isHuggingWall(Location location) {
        final Vector direction = location.getDirection();
        final Location inFront = location.add(direction.normalize().setY(0.0d));
        
        return !inFront.getBlock().isPassable();
    }
    
    private boolean isHuggingWall(GamePlayer player) {
        return isHuggingWall(player.getLocation().add(0, player.getEyeHeight() / 2, 0));
    }
    
    private class JujuUltimate extends UltimateTalent {
        public JujuUltimate() {
            super(JuJu.this, ArrowType.POISON_IVY.getName(), 60);
            
            setType(TalentType.IMPAIR);
            setMaterial(Material.SPIDER_EYE);
            setDurationSec(4);
            
            // Keep below duration
            setDescription("""
                           %s&8&o;;You can only shoot one Poison Ivy arrow.
                           """.formatted(ArrowType.POISON_IVY.getTalentDescription(this))
            );
            
            copyDisplayFieldsFrom(TalentRegistry.POISON_ZONE);
        }
        
        @Nonnull
        @Override
        public UltimateInstance newInstance(@Nonnull GamePlayer player, boolean isFullyCharged) {
            final JujuData data = getPlayerData(player);
            final ArrowType arrowType = data.arrowType();
            
            if (arrowType != null) {
                return error("Already using %s!".formatted(arrowType.getName()));
            }
            
            return execute(() -> {
                data.arrowType(ArrowType.POISON_IVY, getUltimate().getDuration());
                player.snapToWeapon();
            });
        }
    }
}
