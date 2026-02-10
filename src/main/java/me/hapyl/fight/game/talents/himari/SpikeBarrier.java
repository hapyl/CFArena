package me.hapyl.fight.game.talents.himari;

import com.google.common.collect.Lists;
import me.hapyl.eterna.module.entity.Entities;
import me.hapyl.eterna.module.locaiton.LocationHelper;
import me.hapyl.eterna.module.registry.Key;
import me.hapyl.eterna.module.util.Vectors;
import me.hapyl.fight.game.Response;
import me.hapyl.fight.game.damage.DamageCause;
import me.hapyl.fight.game.entity.GamePlayer;
import me.hapyl.fight.game.entity.LivingGameEntity;
import me.hapyl.fight.game.entity.Shield;
import me.hapyl.fight.game.talents.TalentType;
import me.hapyl.fight.game.task.TickingGameTask;
import me.hapyl.fight.util.CFUtils;
import me.hapyl.fight.util.displayfield.DisplayField;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.ArmorStand;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.EulerAngle;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

public class SpikeBarrier extends HimariTalent {
    
    @DisplayField(percentage = true) private final float shieldStrength = 0.5f;
    
    public SpikeBarrier(@Nonnull Key key) {
        super(key, "Spike Barrier");
        setDescription("""
                       %s
                       
                       Instantly create a spike barrier for {duration}.
                       
                       &6Spike Barrier
                       A spiky shield that absorbs &e{shieldStrength}&7 of incoming damage and reflects &c{shieldStrength}&7 of damage back to the &cattacker&7.
                       """.formatted(howToGetString));
        
        setMaterial(Material.SHIELD);
        setType(TalentType.DEFENSE);
        setDurationSec(20);
    }
    
    @Nonnull
    @Override
    public Response executeHimari(@NotNull GamePlayer player) {
        player.setShield(new SpikeShield(player));
        player.schedule(
                () -> {
                    player.setShield(null);
                    
                    player.sendMessage("&eSpike Barrier&6 is gone!");
                    player.playWorldSound(Sound.ENTITY_ZOMBIE_VILLAGER_CONVERTED, 1.25f);
                }, getDuration()
        );
        
        // Fx
        player.playWorldSound(Sound.ITEM_SHIELD_BLOCK, 0.0f);
        
        final List<ArmorStand> fxArmorStands = Lists.newArrayList();
        final Location location = player.getLocation();
        location.setYaw(0.0f);
        location.setPitch(0.0f);
        
        for (int i = 0; i < 3; i++) {
            fxArmorStands.add(spawnArmorStand(location));
        }
        
        new TickingGameTask() {
            private double distance = 2.0d;
            
            @Override
            public void run(int tick) {
                if (distance < 0.2d) {
                    CFUtils.clearCollectionAnd(fxArmorStands, ArmorStand::remove);
                    cancel();
                    return;
                }
                
                final Location location = player.getLocation();
                final double spread = Math.PI * 2 / fxArmorStands.size();
                final double r = Math.toRadians(tick) * 18;
                
                int i = 0;
                
                for (ArmorStand armorStand : fxArmorStands) {
                    final double x = Math.sin(r + i * spread) * distance;
                    final double z = Math.cos(r + i * spread) * distance;
                    
                    // Always face the player
                    final Vector vector = location.toVector().subtract(armorStand.getLocation().toVector()).normalize().multiply(-1);
                    location.setDirection(vector.lengthSquared() > 0 ? vector : Vectors.UP);
                    
                    LocationHelper.offset(location, x, 0, z, () -> armorStand.teleport(location));
                    ++i;
                }
                
                distance -= Math.PI / 30;
            }
        }.runTaskTimer(0, 1);
        
        return Response.ok();
    }
    
    private ArmorStand spawnArmorStand(@Nonnull Location location) {
        return Entities.ARMOR_STAND_MARKER.spawn(
                location, self -> {
                    self.setInvisible(true);
                    self.setSmall(true);
                    
                    self.getEquipment().setHelmet(new ItemStack(Material.SHIELD));
                    self.setHeadPose(new EulerAngle(0.0d, Math.toRadians(180), 0.0d));
                }
        );
    }
    
    private class SpikeShield extends Shield {
        
        public SpikeShield(@Nonnull GamePlayer player) {
            super(player, INFINITE_SHIELD, builder -> builder.strength(shieldStrength));
        }
        
        @Override
        public void onHit(double amount, @Nullable LivingGameEntity damager) {
            if (damager == null || entity.isSelfOrTeammate(damager)) {
                return;
            }
            
            damager.damage(amount, entity, DamageCause.SPIKE_SHIELD);
            
            // Fx
            final Location location = damager.getLocation();
            
            entity.playWorldSound(location, Sound.ITEM_SHIELD_BLOCK, 1.25f);
            entity.playWorldSound(location, Sound.ENCHANT_THORNS_HIT, 0.75f);
        }
    }
    
}
