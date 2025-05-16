package me.hapyl.fight.game.entity.commission.hypixel;

import me.hapyl.eterna.module.entity.Entities;
import me.hapyl.eterna.module.inventory.ItemBuilder;
import me.hapyl.eterna.module.reflect.npc.Human;
import me.hapyl.eterna.module.registry.Key;
import me.hapyl.fight.CF;
import me.hapyl.fight.event.DamageInstance;
import me.hapyl.fight.game.entity.EntityType;
import me.hapyl.fight.game.entity.GameEntity;
import me.hapyl.fight.game.entity.LivingGameEntity;
import me.hapyl.fight.game.entity.commission.CommissionEntity;
import me.hapyl.fight.game.entity.commission.CommissionEntityType;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.entity.Husk;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.ItemStack;

import javax.annotation.Nonnull;
import java.util.function.Function;

public class Genie extends CommissionEntityType {
    public Genie(@Nonnull Key key) {
        super(key, "Genie");

        attributes.setMaxHealth(500);
        attributes.setAttack(125);

        setType(EntityType.BOSS);
    }

    @Nonnull
    @Override
    public GenieEntity create(@Nonnull Location location) {
        return CF.createEntity(location, Entities.HUSK, GenieEntity::new);
    }

    @Override
    public double getHologramOffset() {
        return 0.35d;
    }

    public class GenieEntity extends CommissionEntity {

        private static final ItemStack VEX_HEAD_NORMAL = ItemBuilder.playerHeadUrl(
                "869a7c6f0a7358c594c29d3d42cf7b69638ef3b5b3ec1f9d38150c8b2bff7813"
        ).asIcon();

        private static final ItemStack VEX_HEAD_RAGE = ItemBuilder.playerHeadUrl(
                "6b0a6a5f6d0073185c950a1b57444ba6a87f4361cb221d87b0637903dc1c6e52"
        ).asIcon();

        private final Human riptide;
        private final GameEntity child;
        private boolean rage = false;

        public GenieEntity(@Nonnull Husk entity) {
            super(Genie.this, entity);

            final Location location = entity.getLocation();

            child = CF.createEntity(
                    location, Entities.HUSK, new Function<>() {
                        @Nonnull
                        @Override
                        public LivingGameEntity apply(@Nonnull Husk husk) {
                            husk.setBaby();
                            husk.setInvisible(true);
                            husk.setInvulnerable(true);
                            husk.setSilent(true);

                            return new LivingGameEntity(husk) {
                                @Override
                                public void onDamageDealt(@Nonnull DamageInstance instance) {
                                }
                            };
                        }
                    }
            );

            location.setYaw(90);
            location.setPitch(90);

            riptide = Human.create(location, "", "");
            riptide.bukkitEntity().setInvisible(true);
            riptide.setCollision(false);
            riptide.showAll();

            riptide.setDataWatcherByteValue(8, (byte) 0x04);
            riptide.updateDataWatcher();

            entity.setInvisible(true);

            final EntityEquipment equipment = getEquipment();
            equipment.setHelmet(VEX_HEAD_NORMAL);
            equipment.setChestplate(new ItemStack(Material.DIAMOND_CHESTPLATE));
        }

        @Nonnull
        @Override
        public Husk entity() {
            return (Husk) super.entity();
        }

        @Override
        public void onDamageTaken(@Nonnull DamageInstance instance) {
            if (!rage && (getHealth() - instance.getDamage() <= getMaxHealth() / 2)) {
                rage = true;

                final EntityEquipment equipment = getEquipment();
                equipment.setHelmet(VEX_HEAD_RAGE);
            }
        }

        @Override
        public void remove(boolean playDeathAnimation) {
            super.remove(playDeathAnimation);

            riptide.remove();
            child.remove();
        }

        @Override
        public void onTick(int tick) {
            final Location location = entity.getLocation();
            location.setYaw(90f);
            location.setPitch(90f);

            riptide.teleport(location);
            entity.teleport(child.getLocation().add(0.0d, 1.0d, 0.0d));

            // Fx
            spawnWorldParticle(location.add(0.0d, 0.75d, 0.0d), Particle.SNOWFLAKE, 1, 0.1d, 0.1d, 0.1d, 0.01f);
        }

    }
}
