package me.hapyl.fight.game.heroes.doctor;

import me.hapyl.eterna.module.entity.Entities;
import me.hapyl.eterna.module.inventory.ItemBuilder;
import me.hapyl.eterna.module.particle.ParticleBuilder;
import me.hapyl.eterna.module.util.Located;
import me.hapyl.fight.game.damage.DamageCause;
import me.hapyl.fight.game.entity.GamePlayer;
import me.hapyl.fight.game.entity.LivingGameEntity;
import me.hapyl.fight.util.Collect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;

import javax.annotation.Nonnull;
import java.util.Random;

public class BlockShield implements Located {

    private final static Material DEFAULT_MATERIAL = Material.MAGMA_BLOCK;

    private final GamePlayer player;
    private final Entity entity;
    private final Material material;
    private final ElementType type;

    private double theta;

    public BlockShield(@Nonnull GamePlayer player) {
        this.player = player;
        this.material = randomBlock();
        this.type = ElementType.getElement(material);
        this.entity = Entities.ARMOR_STAND_MARKER.spawn(
                player.getLocation(), self -> {
                    self.setSmall(true);
                    self.setInvisible(true);

                    self.getEquipment().setHelmet(new ItemBuilder(material).toItemStack());
                }
        );

        // Fx
        player.playSound(Sound.ENTITY_CHICKEN_EGG, 0.0f);
    }

    @Nonnull
    @Override
    public Location getLocation() {
        return entity.getLocation();
    }

    public boolean update() {
        if (material == null) {
            return false;
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

        theta += Math.PI / 20;

        // Offset marker
        location.add(0.0d, 0.75d, 0.0d);

        // Collision detection
        final LivingGameEntity nearest = Collect.nearestEntity(location, 0.3d, player::isNotSelfOrTeammate);

        if (nearest != null) {
            nearest.damage(type.getElement().getDamage(), player, DamageCause.BLOCK_SHIELD);

            // Fx
            player.playWorldSound(location, material.createBlockData().getSoundGroup().getBreakSound(), 0.0f);
            return true;
        }

        // Fx
        ParticleBuilder.blockDust(material).display(location, 1, 0.25d, 0.25d, 0.25d, 1.0f);
        ParticleBuilder.blockBreak(material).display(location, 1, 0.1d, 0.1d, 0.1d, 0.1f);

        return false;
    }

    public void remove() {
        entity.remove();
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

    public Material randomBlock() {
        return randomBlock(0);
    }

    private Material randomBlock(int count) {
        if (count > 10) {
            return DEFAULT_MATERIAL;
        }

        final Location location = player.getLocation().add(
                new Random().nextInt(-3, 3),
                new Random().nextInt(-3, -1),
                new Random().nextInt(-3, 3)
        );

        final Block block = location.getBlock();
        final Material type = block.getType();

        if (!type.isOccluding()) {
            return randomBlock(++count);
        }

        return type;
    }
}
