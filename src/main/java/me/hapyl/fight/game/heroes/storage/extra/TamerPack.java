package me.hapyl.fight.game.heroes.storage.extra;

import me.hapyl.fight.util.Nulls;
import me.hapyl.spigotutils.module.chat.Chat;
import me.hapyl.spigotutils.module.entity.Entities;
import me.hapyl.spigotutils.module.inventory.ItemBuilder;
import me.hapyl.spigotutils.module.player.PlayerLib;
import me.hapyl.spigotutils.module.util.BukkitUtils;
import org.bukkit.*;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.util.Consumer;

import javax.annotation.Nullable;
import java.util.HashSet;
import java.util.Set;

public abstract class TamerPack {

    private final double[][] relativeOffsets = {
            { -1.0d, 0.0d },
            { 1.0d, 0.0d },
            { 0.0d, -1.0d },
            { 0.0d, 1.0d },
            { 1.0d, 1.0d },
            { -1.0d, 1.0d },
            { 1.0d, -1.0d },
            { -1.0d, -1.0d }
    };

    private final String name;
    private final Set<LivingEntity> entities;

    public TamerPack(String name) {
        this.name = name;
        this.entities = new HashSet<>();
    }

    public Set<LivingEntity> getEntities() {
        return entities;
    }

    public String getName() {
        return name;
    }

    public boolean isInPack(LivingEntity entity) {
        for (final LivingEntity living : entities) {
            if (living == entity) {
                return true;
            }
        }
        return false;
    }

    protected final <T extends LivingEntity> T createEntity(Location location, Entities<T> entities) {
        return createEntity(location, entities, null);
    }

    protected final <T extends LivingEntity> T createEntity(Location location, Entities<T> entities, @Nullable Consumer<T> consumer) {
        return entities.spawn(location, me -> {
            me.setMaxHealth(64.0d);
            me.setHealth(64.0d);
            me.setCustomNameVisible(true);

            // Add helmet if possible
            Nulls.runIfNotNull(me.getEquipment(), equipment -> {
                equipment.setHelmet(
                        new ItemBuilder(Material.LEATHER_HELMET)
                                .setUnbreakable()
                                .setLeatherArmorColor(Color.fromRGB(509659))
                                .build()
                );
            });

            // Apply consumer if not null
            Nulls.runIfNotNull(consumer, cons -> {
                cons.accept(me);
            });

            this.entities.add(me);
        });
    }

    public final void updateEntitiesNames(Player player) {
        entities.forEach(entity -> {
            if (entity.isDead()) {
                return;
            }

            entity.setCustomName(Chat.format(
                    "&a%s's %s &8| &c%s❤&8",
                    player.getName(),
                    Chat.capitalize(entity.getType()),
                    BukkitUtils.decimalFormat(entity.getHealth())
            ));
        });
    }

    public abstract void spawn(Player player);

    public final void remove() {
        entities.forEach(Entity::remove);
        entities.clear();
    }

    public final void recall() {
        entities.forEach(entity -> {
            final Location location = entity.getLocation().add(0.0d, 1.0d, 0.0d);
            PlayerLib.spawnParticle(location, Particle.EXPLOSION_NORMAL, 10, 0.5d, 0.5d, 0.5d, 0.2f);
            PlayerLib.playSound(location, Sound.ENTITY_ELDER_GUARDIAN_AMBIENT_LAND, 0.2f);
            entity.remove();
        });
        entities.clear();
    }

    protected final Location addRelativeOffset(Location location, int offset) {
        final double[] doubles = relativeOffsets[offset >= relativeOffsets.length ? 0 : offset];
        return location.clone().add(doubles[0], 0.0d, doubles[1]);
    }

}

