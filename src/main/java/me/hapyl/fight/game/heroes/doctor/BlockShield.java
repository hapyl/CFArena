package me.hapyl.fight.game.heroes.doctor;

import me.hapyl.fight.game.entity.GamePlayer;
import me.hapyl.fight.util.Nulls;
import me.hapyl.spigotutils.module.entity.Entities;
import me.hapyl.spigotutils.module.inventory.ItemBuilder;
import me.hapyl.spigotutils.module.particle.ParticleBuilder;
import me.hapyl.spigotutils.module.util.BukkitUtils;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;

import java.util.Random;

public class BlockShield {

    private static final Location DEFAULT_BLOCK_LOCATION = BukkitUtils.defLocation(0, 48, 0);

    private final GamePlayer player;

    private Entity entity;
    private Material material;
    private ElementType type;
    private double theta;

    public BlockShield(GamePlayer player) {
        this.player = player;
    }

    public void newElement() {
        if (exists()) {
            return;
        }

        // Get random block around the player
        final Block block = randomBlock();
        setType(block.getType());

        player.playSound(Sound.ENTITY_CHICKEN_EGG, 0.0f);
    }

    private void setType(Material material) {
        this.material = material;
        this.type = ElementType.getElement(material);
        this.theta = 0;

        this.entity = Entities.ARMOR_STAND_MARKER.spawn(player.getLocation(), self -> {
            self.setSmall(true);
            self.setInvisible(true);

            Nulls.runIfNotNull(self.getEquipment(), equipment -> {
                equipment.setHelmet(ItemBuilder.of(material).build());
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
        ParticleBuilder.blockDust(material).display(location, 1, 0.25d, 0.25d, 0.25d, 1.0f);
        ParticleBuilder.blockBreak(material).display(location, 1, 0.1d, 0.1d, 0.1d, 0.1f);
    }

    public void remove() {
        if (entity != null) {
            entity.remove();
            entity = null;
        }

        theta = 0;
        material = null;
        type = null;
    }

    public boolean exists() {
        return entity != null;
    }

    public Entity getEntity() {
        return entity;
    }

    public ElementType getType() {
        return type;
    }

    public Material getMaterial() {
        return material;
    }

    public Block randomBlock() {
        return randomBlock(0);
    }

    private final static Material DEFAULT_MATERIAL = Material.MAGMA_BLOCK;

    private Block defaultBlock() {
        final Block block = DEFAULT_BLOCK_LOCATION.getBlock();

        if (block.getType() != DEFAULT_MATERIAL) {
            block.setType(DEFAULT_MATERIAL);
        }

        return block;
    }

    private Block randomBlock(int count) {
        if (count > 10) {
            return defaultBlock();
        }

        final Location location = player.getLocation().add(
                new Random().nextInt(-3, 3),
                new Random().nextInt(-3, -1),
                new Random().nextInt(-3, 3)
        );

        final Block block = location.getBlock();

        if (!block.getType().isOccluding()) {
            return randomBlock(++count);
        }

        return block;
    }
}
