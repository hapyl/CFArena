package me.hapyl.fight.game.heroes.taker;

import com.google.common.collect.Lists;
import me.hapyl.eterna.module.entity.Entities;
import me.hapyl.eterna.module.locaiton.LocationHelper;
import me.hapyl.eterna.module.util.Ticking;
import me.hapyl.fight.game.entity.GamePlayer;
import me.hapyl.fight.game.heroes.PlayerData;
import me.hapyl.fight.game.talents.taker.SpiritualBonesPassive;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.ArmorStand;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.EulerAngle;

import javax.annotation.Nonnull;
import java.util.LinkedList;

public class TakerData extends PlayerData implements Ticking {
    
    private final SpiritualBonesPassive passive;
    private final LinkedList<ArmorStand> armorStands;
    
    private int bones;
    private float theta = 0.0f;
    private float yaw = 0.0f;
    
    public TakerData(@Nonnull SpiritualBonesPassive passive, @Nonnull GamePlayer player) {
        super(player);
        
        this.passive = passive;
        this.armorStands = Lists.newLinkedList();
        
        // Add start bones
        add(passive.startBones);
    }
    
    @Override
    public void remove() {
        this.bones = 0;
        
        this.armorStands.forEach(ArmorStand::remove);
        this.armorStands.clear();
    }
    
    public int getBones() {
        return bones;
    }
    
    public void add(int amount) {
        add(amount, true);
    }
    
    public void add(int amount, boolean playFx) {
        bones = Math.min(bones + amount, passive.maxBones);
        createBoneEntity(amount);
        
        if (playFx) {
            player.playSound(Sound.ENTITY_SKELETON_AMBIENT, 0.0f);
        }
    }
    
    public void remove(int amount) {
        this.bones = Math.max(this.bones - amount, 0);
        
        for (int i = 0; i < amount; i++) {
            final ArmorStand last = armorStands.pollLast();
            
            // Illegal call
            if (last == null) {
                return;
            }
            
            last.remove();
            player.spawnWorldParticle(last.getLocation().add(0, 1.25, 0), Particle.SMOKE, 5, 0.2, 0.2, 0.2, 0.015f);
        }
    }
    
    @Override
    public void tick() {
        if (bones == 0 || !player.isAlive() || armorStands.isEmpty()) {
            return;
        }
        
        // Fly bones
        final double offset = (Math.PI * 2 / armorStands.size());
        final Location location = player.getLocation();
        
        // Move a little lower to not disturb the vision
        location.subtract(0.0d, 0.3d, 0.0d);
        
        location.setYaw(yaw += 3.0f);
        location.setPitch(0.0f);
        
        for (int index = 0; index < armorStands.size(); index++) {
            final ArmorStand entity = armorStands.get(index);
            
            final double x = Math.sin(theta + offset * index);
            final double z = Math.cos(theta + offset * index);
            final double y = -0.25d;
            
            LocationHelper.offset(
                    location, x, y, z, () -> {
                        location.setYaw(location.getYaw() + 1.0f);
                        entity.teleport(location);
                    }
            );
        }
        
        theta += 0.1f;
    }
    
    public double getDamageMultiplier() {
        return passive.damageAmplifierPerBone * bones;
    }
    
    public double getDamageReduction() {
        return passive.damageReductionPerBone * bones;
    }
    
    public double getHealing() {
        return passive.healingPerBone * bones;
    }
    
    public void createBoneEntity(int amount) {
        for (int i = 0; i < amount; i++) {
            if (armorStands.size() >= passive.maxBones) {
                return;
            }
            
            armorStands.offerLast(Entities.ARMOR_STAND_MARKER.spawn(
                    player.getLocation(), self -> {
                        self.setInvisible(true);
                        self.setSilent(true);
                        self.setHeadPose(new EulerAngle(0.0d, 0.0d, Math.toRadians(90.0d)));
                        
                        self.getEquipment().setHelmet(new ItemStack(Material.BONE));
                    }
            ));
        }
    }
    
}