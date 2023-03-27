package me.hapyl.fight.game.heroes.storage.extra;

import me.hapyl.fight.game.talents.storage.extra.ElementType;
import me.hapyl.fight.util.Nulls;
import me.hapyl.spigotutils.module.entity.Entities;
import me.hapyl.spigotutils.module.inventory.ItemStackBuilder;
import me.hapyl.spigotutils.module.particle.ParticleBuilder;
import me.hapyl.spigotutils.module.player.PlayerLib;
import me.hapyl.spigotutils.module.util.BukkitUtils;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import java.util.Random;

public class BlockShield {

    private static final Block DEFAULT_BLOCK = BukkitUtils.defLocation(0, 48, 0).getBlock();
    private final Player player;

    private Entity entity;
    private Material material;
    private ElementType element;
    private double theta;

    public BlockShield(Player player) {
        this.player = player;
    }

    public void newElement() {
        if (exists()) {
            return;
        }

        // Get random block around the player
        final Block block = randomBlock();
        setElement(block.getType());

        PlayerLib.playSound(player, Sound.ENTITY_CHICKEN_EGG, 0.0f);
    }

    private void setElement(Material material) {
        this.material = material;
        this.element = ElementType.getElement(material);
        this.theta = 0;

        this.entity = Entities.ARMOR_STAND_MARKER.spawn(player.getLocation(), self -> {
            self.setSmall(true);
            self.setInvisible(true);

            Nulls.runIfNotNull(self.getEquipment(), equipment -> {
                equipment.setHelmet(ItemStackBuilder.of(material).build());
            });
        });
    }

    public void update() {
        if (material == null) {
            return;
        }

        final Location location = player.getLocation();

        final double x = Math.cos(theta) * 1.5;
        final double z = Math.sin(theta) * 1.5;

        location.add(x, 0.0, z);

        // Face player
        location.setDirection(player.getLocation().toVector().subtract(location.toVector()).normalize().setY(0.0d));

        if (this.entity != null) {
            this.entity.teleport(location);
        }

        if (theta > Math.PI * 2) {
            theta = 0;
        }

        theta += Math.PI / 20;

        // Offset marker
        location.add(0.0d, 0.75d, 0.0d);

        // Fx
        ParticleBuilder.blockDust(material).setOffX(0.25d).setOffY(0.25d).setOffZ(0.25).display(location);
        ParticleBuilder.blockBreak(material)
                .setOffX(0.1d)
                .setOffY(0.1d)
                .setOffZ(0.1)
                .setSpeed(0.1f)
                .display(location);
    }

    public void remove() {
        if (entity != null) {
            entity.remove();
            entity = null;
        }

        theta = 0;
        material = null;
        element = null;
    }

    public boolean exists() {
        return entity != null;
    }

    public Entity getEntity() {
        return entity;
    }

    public ElementType getElement() {
        return element;
    }

    public Material getMaterial() {
        return material;
    }

    private Block randomBlock() {
        final Location location = player.getLocation().add(
                new Random().nextInt(-3, 3),
                new Random().nextInt(-3, -1),
                new Random().nextInt(-3, 3)
        );

        final Block block = location.getBlock();

        try {
            if (!block.getType().isOccluding()) {
                return randomBlock();
            }
        } catch (StackOverflowError error) {
            return DEFAULT_BLOCK;
        }

        return block;
    }
}
