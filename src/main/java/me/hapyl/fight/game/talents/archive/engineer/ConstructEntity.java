package me.hapyl.fight.game.talents.archive.engineer;

import me.hapyl.fight.CF;
import me.hapyl.fight.game.entity.LivingGameEntity;
import me.hapyl.fight.game.talents.Removable;
import me.hapyl.fight.util.Ticking;
import me.hapyl.spigotutils.module.chat.Chat;
import me.hapyl.spigotutils.module.entity.Entities;
import org.bukkit.Location;
import org.bukkit.entity.ArmorStand;

import javax.annotation.Nonnull;

public class ConstructEntity implements Removable, Ticking {

    private final Construct construct;

    // This entity is used as a "collision" and damage check.
    private final LivingGameEntity entity;

    // Armor stand is used to display health, time, etc.
    private final ArmorStand stand;

    public ConstructEntity(Construct construct) {
        this.construct = construct;

        final Location location = construct.location;

        this.entity = CF.createEntity(location, Entities.SLIME, self -> {
            final double health = construct.healthScaled().get(0, 10.0d);

            self.setInvisible(true);
            self.setAI(false);
            self.setSize(3);
            self.setMaxHealth(health);
            self.setHealth(health);

            return new LivingGameEntity(self);
        });

        this.stand = Entities.ARMOR_STAND.spawn(location, self -> {
            self.setInvisible(true);
        });
    }

    @Nonnull
    public LivingGameEntity getEntity() {
        return entity;
    }

    @Nonnull
    public ArmorStand getStand() {
        return stand;
    }

    @Override
    public void tick() {
        stand.setCustomName(
                Chat.format(
                        "&8[&f&l%s&8] &a%s &c&l%.0f &c❤ &b%ss",
                        construct.getLevelRoman(),
                        construct.getName(),
                        entity.getHealth(),
                        construct.getDurationLeft()
                )
        );
        stand.setCustomNameVisible(true);
    }

    @Override
    public void remove() {
        entity.remove();
        stand.remove();
    }

    public boolean isDead() {
        return entity.isDead();
    }

    @Nonnull
    public Location getLocation() {
        return entity.getLocation();
    }

    public void lookAt(@Nonnull Location location) {
        entity.lookAt(location);
    }
}
