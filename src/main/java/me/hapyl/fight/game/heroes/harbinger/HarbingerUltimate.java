package me.hapyl.fight.game.heroes.harbinger;

import com.google.common.collect.Sets;
import me.hapyl.eterna.module.block.display.BDEngine;
import me.hapyl.eterna.module.block.display.DisplayData;
import me.hapyl.eterna.module.block.display.DisplayEntity;
import me.hapyl.eterna.module.locaiton.LocationHelper;
import me.hapyl.eterna.module.math.Tick;
import me.hapyl.eterna.module.util.BukkitUtils;
import me.hapyl.eterna.module.util.IndexedTicking;
import me.hapyl.eterna.module.util.Removable;
import me.hapyl.fight.fx.RiptideFx;
import me.hapyl.fight.game.Named;
import me.hapyl.fight.game.damage.DamageCause;
import me.hapyl.fight.game.entity.GamePlayer;
import me.hapyl.fight.game.entity.LivingGameEntity;
import me.hapyl.fight.game.heroes.HeroRegistry;
import me.hapyl.fight.game.heroes.ultimate.EnumResource;
import me.hapyl.fight.game.heroes.ultimate.UltimateInstance;
import me.hapyl.fight.game.heroes.ultimate.UltimateTalent;
import me.hapyl.fight.game.talents.TalentType;
import me.hapyl.fight.game.task.TickingGameTask;
import me.hapyl.fight.terminology.EnumTerm;
import me.hapyl.fight.util.Collect;
import me.hapyl.fight.util.displayfield.DisplayField;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.Entity;
import org.bukkit.util.Vector;

import javax.annotation.Nonnull;
import java.util.Set;

public class HarbingerUltimate extends UltimateTalent {
    
    private final Harbinger harbinger;
    
    @DisplayField(scale = 0.001) private final long flowThreshold = 3_000L;
    @DisplayField private final double arrowSpeed = 250;
    @DisplayField private final int maxFlightDuration = Tick.fromSeconds(10);
    
    @DisplayField private final double typhoonDamageBank = 300;
    @DisplayField private final double typhoonDamage = 20;
    @DisplayField private final double typhoonRadius = 5;
    
    @DisplayField private final int typhoonDamagePeriod = 10;
    @DisplayField private final short maxTyphoon = 1;
    
    private final DisplayData display = BDEngine.parse(
            "/summon block_display ~-0.5 ~ ~-0.5 {Passengers:[{id:\"minecraft:block_display\",block_state:{Name:\"minecraft:spruce_fence\",Properties:{}},transformation:[0.1875f,0f,0f,-0.0882338841f,0f,0f,-0.1875f,0.0819838839f,0f,1.5625f,0f,-0.7338382038f,0f,0f,0f,1f]},{id:\"minecraft:item_display\",item:{id:\"minecraft:twisting_vines\",Count:1},item_display:\"none\",transformation:[0.4375f,0f,0f,0.0036411159f,0f,0f,-0.61f,-0.0130161161f,0f,0.439f,0f,-0.6507132038f,0f,0f,0f,1f]},{id:\"minecraft:item_display\",item:{id:\"minecraft:twisting_vines\",Count:1},item_display:\"none\",transformation:[0f,0f,0.61f,0.0036411159f,0.4375f,0f,0f,-0.0130161161f,0f,0.439f,0f,-0.6507132038f,0f,0f,0f,1f]},{id:\"minecraft:item_display\",item:{id:\"minecraft:twisting_vines\",Count:1},item_display:\"none\",transformation:[-0.4375f,0f,0f,0.0036411159f,0f,0f,0.61f,-0.0130161161f,0f,0.439f,0f,-0.6507132038f,0f,0f,0f,1f]},{id:\"minecraft:item_display\",item:{id:\"minecraft:twisting_vines\",Count:1},item_display:\"none\",transformation:[0f,0f,-0.61f,0.0036411159f,-0.4375f,0f,0f,-0.0130161161f,0f,0.439f,0f,-0.6507132038f,0f,0f,0f,1f]},{id:\"minecraft:item_display\",item:{id:\"minecraft:tube_coral_fan\",Count:1},item_display:\"none\",transformation:[0.0921482187f,-0.2388840131f,0f,-0.1676088841f,0f,0f,-0.602f,-0.0130161161f,0.2986050163f,0.073718575f,0f,-0.8138382038f,0f,0f,0f,1f]},{id:\"minecraft:item_display\",item:{id:\"minecraft:tube_coral_fan\",Count:1},item_display:\"none\",transformation:[0.0921482187f,0.2388840131f,0f,0.1705161159f,0f,0f,-0.602f,-0.0130161161f,-0.2986050163f,0.073718575f,0f,-0.8138382038f,0f,0f,0f,1f]},{id:\"minecraft:item_display\",item:{id:\"minecraft:tube_coral_fan\",Count:1},item_display:\"none\",transformation:[9.3e-9f,0f,0.602f,0.0036411159f,0.0921482187f,0.2388840131f,-5.3e-9f,0.1469838839f,-0.2986050163f,0.073718575f,1.71e-8f,-0.8138382038f,0f,0f,0f,1f]},{id:\"minecraft:item_display\",item:{id:\"minecraft:tube_coral_fan\",Count:1},item_display:\"none\",transformation:[9.3e-9f,0f,0.602f,0.0036411159f,0.0921482187f,-0.2388840131f,-5.3e-9f,-0.1911411161f,0.2986050163f,0.073718575f,-1.71e-8f,-0.8138382038f,0f,0f,0f,1f]},{id:\"minecraft:item_display\",item:{id:\"minecraft:tide_armor_trim_smithing_template\",Count:1},item_display:\"none\",transformation:[0.1992636163f,-0.0354360192f,0.0059900401f,0.0248911159f,0.0029918262f,0.0041873881f,-0.4259310568f,-0.0117661161f,0.0168837255f,0.4174778489f,0.0047806173f,0.9280367962f,0f,0f,0f,1f]},{id:\"minecraft:item_display\",item:{id:\"minecraft:tide_armor_trim_smithing_template\",Count:1},item_display:\"none\",transformation:[-0.1999822374f,0.0038885181f,-0.0040747236f,-0.0051088841f,-0.0018936952f,0.0043567456f,0.425957873f,-0.0111411161f,0.0018758025f,0.4189593037f,-0.0043917045f,0.9255367962f,0f,0f,0f,1f]},{id:\"minecraft:block_display\",block_state:{Name:\"minecraft:cyan_candle\",Properties:{candles:\"1\",lit:\"false\"}},transformation:[0.558f,0f,0f,-0.2738588841f,0f,0f,-0.594f,0.2826088839f,0f,0.693f,0f,0.5361617962f,0f,0f,0f,1f]},{id:\"minecraft:item_display\",item:{id:\"minecraft:tide_armor_trim_smithing_template\",Count:1},item_display:\"none\",transformation:[-0.0029918262f,-0.0041873881f,0.4259310568f,0.0055161159f,0.1992636163f,-0.0354360192f,0.0059900401f,0.0032338839f,0.0168837255f,0.4174778489f,0.0047806173f,0.9280367962f,0f,0f,0f,1f]},{id:\"minecraft:item_display\",item:{id:\"minecraft:tide_armor_trim_smithing_template\",Count:1},item_display:\"none\",transformation:[0.0018936952f,-0.0043567456f,-0.425957873f,0.0048911159f,-0.1999822374f,0.0038885181f,-0.0040747236f,-0.0267661161f,0.0018758025f,0.4189593037f,-0.0043917045f,0.9255367962f,0f,0f,0f,1f]},{id:\"minecraft:item_display\",item:{id:\"minecraft:tube_coral_fan\",Count:1},item_display:\"none\",transformation:[0.0551242497f,0.1752087814f,0f,0.0992661159f,0f,0f,-0.602f,-0.0130161161f,-0.3075996864f,0.0313987726f,0f,-0.4213382038f,0f,0f,0f,1f]},{id:\"minecraft:item_display\",item:{id:\"minecraft:tube_coral_fan\",Count:1},item_display:\"none\",transformation:[0.0551242497f,-0.1752087814f,0f,-0.0932338841f,0f,0f,-0.602f,-0.0130161161f,0.3075996864f,0.0313987726f,0f,-0.4213382038f,0f,0f,0f,1f]},{id:\"minecraft:item_display\",item:{id:\"minecraft:tube_coral_fan\",Count:1},item_display:\"none\",transformation:[6.6e-9f,0f,0.602f,0.0030161159f,0.0551242497f,0.1752087814f,-2.2e-9f,0.0832338839f,-0.3075996864f,0.0313987726f,1.25e-8f,-0.4213382038f,0f,0f,0f,1f]},{id:\"minecraft:item_display\",item:{id:\"minecraft:tube_coral_fan\",Count:1},item_display:\"none\",transformation:[0f,0f,0.602f,0.0030161159f,0.0551242497f,-0.1752087814f,0f,-0.1092661161f,0.3075996864f,0.0313987726f,0f,-0.4213382038f,0f,0f,0f,1f]},{id:\"minecraft:item_display\",item:{id:\"minecraft:breeze_rod\",Count:1},item_display:\"none\",transformation:[0.2194652828f,0.3040723428f,0f,0.0036411159f,0f,0f,-0.3125f,-0.0148911161f,-0.3040723428f,0.2194652828f,0f,-0.2963382038f,0f,0f,0f,1f]}]}"
    );
    
    HarbingerUltimate(Harbinger harbinger) {
        super(harbinger, "Arrow Typhoon", EnumResource.SURGE, 7_000);
        this.harbinger = harbinger;
        
        minimumCost(3_000);
        
        setDescription("""
                       &6%1$s
                       Accumulate %2$s by applying %3$s to charge this ultimate!
                       
                       &6%4$s
                       Shoot an arrow in front of that creates a &3Arrow Typhoon&7 upon collision where arrows constantly circle around it, dealing rapid %6$s to &cenemies&7.
                       &8&o;;Each typhoon lasts indefinitely as long as its damage bank isn't depleted.
                       
                       &8&o;;Only one typhoon may exist at any given time.
                       
                       &6Flow
                       This ultimate has a maximum of &3%7$,.0f&7 cost, but can be executed &aearly&7 after &3%8$,.0f&7 %2$s for a short duration after triggering a &3Riptide Slash&7.
                       
                       &8&o;;This ultimate can only be executed in Range Stage!
                       """.formatted(EnumResource.SURGE.getName(), EnumResource.SURGE, Named.RIPTIDE, getName(), Named.RIPTIDE.getName(), EnumTerm.PIERCING_DAMAGE, cost, minimumCost)
        );
        
        setType(TalentType.DAMAGE);
        setMaterial(Material.DIAMOND);
        
        setCooldownSec(5);
    }
    
    @Override
    public double consumption() {
        return minimumCost();
    }
    
    @Nonnull
    @Override
    public UltimateInstance newInstance(@Nonnull GamePlayer player, boolean isFullyCharged) {
        final HarbingerData data = harbinger.getPlayerData(player);
        
        if (data.stance != null) {
            return error("Must be in Range Stance!");
        }
        
        if (!isFullyCharged) {
            final long lastSlash = System.currentTimeMillis() - data.lastSlash;
            
            if (lastSlash > flowThreshold) {
                return error("You haven't executed Riptide Slash recently!");
            }
        }
        
        return execute(() -> {
            final Location location = player.getEyeLocation();
            final Vector vector = location.getDirection().normalize().multiply(arrowSpeed);
            final Entity entity = display.spawnInterpolated(location);
            
            new TickingGameTask() {
                @Override
                public void run(int tick) {
                    final double percent = (double) tick / maxFlightDuration;
                    
                    if (percent > 1.0) {
                        entity.remove();
                        return;
                    }
                    
                    final double x = vector.getX() * percent;
                    final double y = vector.getY() * percent;
                    final double z = vector.getZ() * percent;
                    
                    location.add(x, y, z);
                    
                    if (!location.getBlock().isPassable()) {
                        makeTyphoon(player, location);
                        entity.remove();
                        cancel();
                        return;
                    }
                    
                    entity.teleport(location);
                    location.subtract(x, y, z);
                }
            }.runTaskTimer(0, 1);
        });
    }
    
    public void makeTyphoon(@Nonnull GamePlayer player, @Nonnull Location location) {
        final HarbingerData data = HeroRegistry.HARBINGER.getPlayerData(player);
        
        if (data.typhoons.size() >= maxTyphoon) {
            final ArrowTyphoon firstTyphoon = data.typhoons.pollFirst();
            
            if (firstTyphoon != null) {
                firstTyphoon.cancel();
            }
        }
        
        data.typhoons.add(new ArrowTyphoon(player, location));
    }
    
    public class ArrowTyphoon extends TickingGameTask {
        private final GamePlayer player;
        private final Location location;
        private final RiptideFx riptide;
        private final Set<TyphoonArrowFx> arrows;
        
        private double damageDealt;
        
        ArrowTyphoon(GamePlayer player, Location location) {
            this.player = player;
            this.location = location;
            this.riptide = new RiptideFx(location);
            this.arrows = Sets.newHashSet();
            
            this.runTaskTimer(0, 1);
        }
        
        @Override
        public void onTaskStop() {
            riptide.remove();
            arrows.forEach(TyphoonArrowFx::remove);
        }
        
        @Override
        public void run(int tick) {
            if (player.isDeadOrRespawning() || damageDealt >= typhoonDamageBank) {
                cancel();
                return;
            }
            
            // Damage
            if (modulo(typhoonDamagePeriod)) {
                for (LivingGameEntity entity : Collect.nearbyEntities(location, typhoonRadius, player::isNotSelfOrTeammate)) {
                    entity.damageNoKnockback(typhoonDamage, player, DamageCause.TYPHOON);
                    
                    // Fx before math because we're returning
                    entity.playWorldSound(Sound.ENTITY_EVOKER_FANGS_ATTACK, 1.25f);
                    entity.playWorldSound(Sound.ENTITY_ARROW_HIT, 0.75f);
                    
                    // Increment damage dealt
                    damageDealt += typhoonDamage;
                    
                    // Add a return check to prevent damage overflowing the bank
                    if (damageDealt >= typhoonDamageBank) {
                        return;
                    }
                }
                
                // Sfx
                player.playWorldSound(location, Sound.ENTITY_PLAYER_ATTACK_STRONG, 0.75f);
            }
            
            // Fx
            if (tick % 10 == 0) {
                final Location fxLocation = BukkitUtils.newLocation(location);
                fxLocation.setYaw(0f);
                fxLocation.setPitch(-25f);
                
                // Randomize location
                fxLocation.add(
                        player.random.nextDoubleBool(typhoonRadius * 0.3),
                        0,
                        player.random.nextDoubleBool(typhoonRadius * 0.3)
                );
                
                arrows.add(new TyphoonArrowFx(player, fxLocation));
            }
            
            arrows.removeIf(TyphoonArrowFx::removeIfShould);
            arrows.forEach(t -> t.tick(tick));
            
            // Sfx
            if (modulo(15)) {
                player.playWorldSound(location, Sound.ITEM_HONEY_BOTTLE_DRINK, 0.0f);
            }
        }
    }
    
    public class TyphoonArrowFx implements IndexedTicking, Removable {
        
        private static final double MAX_RESOLVE = Math.PI * 2 * 3;
        private static final double INCREMENT = Math.PI * 0.1;
        
        private final GamePlayer player;
        private final DisplayEntity entity;
        private final Location location;
        private final Location centre;
        private final double radiusIncrement;
        
        private double theta;
        private double radius;
        
        TyphoonArrowFx(GamePlayer player, Location location) {
            this.player = player;
            this.entity = display.spawnInterpolated(location);
            this.location = location;
            this.centre = BukkitUtils.newLocation(location);
            
            // Calculate radius
            final double radiusStart = typhoonRadius * 0.2;
            this.radiusIncrement = (typhoonRadius - radiusStart) / (MAX_RESOLVE / INCREMENT);
            
            this.radius = radiusStart;
        }
        
        @Override
        public void tick(int tick) {
            final double progress = theta / MAX_RESOLVE;
            final double x = Math.sin(theta) * radius;
            final double y = Math.sin(Math.PI / 2 * progress) * 5;
            final double z = Math.cos(theta) * radius;
            
            LocationHelper.offset(
                    location, x, y, z, () -> {
                        final Vector towardsCentre = centre.toVector().subtract(location.toVector()).normalize();
                        
                        // Make the arrow face the direction it's moving by making it first look at the centre and then add 90°.
                        // I'm sure there is an easier version of doing this with theta, but math is hard and this works.
                        location.setDirection(towardsCentre);
                        location.setYaw(location.getYaw() + 90f);
                        location.setPitch(-25f);
                        
                        entity.teleport(location);
                        
                        // Fx
                        player.spawnWorldParticle(location, Particle.DRIPPING_WATER, 1, 0,0,0, 1f);
                        player.spawnWorldParticle(location, Particle.FALLING_WATER, 1, 0,0,0, 1f);
                    }
            );
            
            theta += INCREMENT;
            radius += radiusIncrement;
        }
        
        @Override
        public void remove() {
            entity.remove();
        }
        
        @Override
        public boolean shouldRemove() {
            return theta >= MAX_RESOLVE;
        }
    }
    
}
