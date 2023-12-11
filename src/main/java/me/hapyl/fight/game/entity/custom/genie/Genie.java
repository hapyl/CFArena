package me.hapyl.fight.game.entity.custom.genie;

import me.hapyl.fight.CF;
import me.hapyl.fight.event.io.DamageInput;
import me.hapyl.fight.event.io.DamageOutput;
import me.hapyl.fight.game.attribute.Attributes;
import me.hapyl.fight.game.entity.*;
import me.hapyl.fight.game.entity.EntityMetadata;
import me.hapyl.fight.game.task.GameTask;
import me.hapyl.spigotutils.module.entity.Entities;
import me.hapyl.spigotutils.module.inventory.ItemBuilder;
import me.hapyl.spigotutils.module.reflect.npc.Human;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.entity.Husk;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.ItemStack;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class Genie extends GameEntityType<Husk> {
    public Genie() {
        super("Genie", Husk.class);

        final Attributes attributes = getAttributes();
        attributes.setHealth(500);
        attributes.setAttack(125);

        setType(EntityType.BOSS);
    }

    @Override
    public double getHologramOffset() {
        return 0.35d;
    }

    @Nonnull
    @Override
    public NamedGameEntity<Husk> create(@Nonnull Husk entity) {
        return new GenieEntity(entity);
    }

    protected class GenieEntity extends NamedGameEntity<Husk> {

        private static final ItemStack VEX_HEAD_NORMAL = ItemBuilder.playerHeadUrl(
                "869a7c6f0a7358c594c29d3d42cf7b69638ef3b5b3ec1f9d38150c8b2bff7813").asIcon();
        private static final ItemStack VEX_HEAD_RAGE = ItemBuilder.playerHeadUrl(
                "6b0a6a5f6d0073185c950a1b57444ba6a87f4361cb221d87b0637903dc1c6e52").asIcon();

        private final Human riptide;
        private final GameEntity child;
        private boolean rage = false;

        public GenieEntity(Husk entity) {
            super(Genie.this, entity);

            final EntityMetadata metadata = getMetadata();
            metadata.ccAffect.setValue(false);

            final Location location = entity.getLocation();

            child = CF.createEntity(location, Entities.HUSK, new ConsumerFunction<>() {
                @Nonnull
                @Override
                public LivingGameEntity apply(Husk husk) {
                    husk.setBaby();
                    husk.setInvisible(true);
                    husk.setInvulnerable(true);
                    husk.setSilent(true);

                    return new LivingGameEntity(husk) {
                        public void schedule(Runnable run, int delay) {
                            GameTask.runLater(run, delay);
                        }

                        @Override
                        public DamageOutput onDamageDealt(@Nonnull DamageInput input) {
                            return DamageOutput.CANCEL;
                        }
                    };
                }
            });

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

        @Nullable
        @Override
        public DamageOutput onDamageTaken(@Nonnull DamageInput input) {
            if (!rage && (getHealth() - input.getDamage() <= getMaxHealth() / 2)) {
                rage = true;

                final EntityEquipment equipment = getEquipment();
                equipment.setHelmet(VEX_HEAD_RAGE);
            }

            return null;
        }

        @Override
        public void kill() {
            super.kill();

            riptide.remove();
            child.kill();
        }

        @Override
        public void onTick() {
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
