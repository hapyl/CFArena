package me.hapyl.fight.game.talents.archive.engineer;

import me.hapyl.spigotutils.module.entity.Entities;
import org.bukkit.Location;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Player;

import javax.annotation.Nonnull;

public abstract class Construct {

    public static final int MAX_LEVEL = 3;
    public static final int MAX_DURATION_SEC = 5;

    protected final Player player;
    protected final Location location;
    @Nonnull
    protected final ArmorStand stand;

    private int level;
    private int cost;

    public Construct(@Nonnull Player player, @Nonnull Location location) {
        this.player = player;
        this.location = location;
        this.level = 0;

        stand = Entities.ARMOR_STAND.spawn(location, self -> {
            final double health = healthScaled().get(0, 10.0d);

            self.setMaxHealth(health);
            self.setHealth(health);
        });

        onCreate();
    }

    public ImmutableArray<Integer> durationScaled() {
        return ImmutableArray.empty();
    }

    public ImmutableArray<Double> healthScaled() {
        return ImmutableArray.empty();
    }

    public abstract void onCreate();

    public abstract void onDestroy();

    public abstract void onTick();

    public void onLevelUp(int newLevel) {
    }

    public void remove() {
        onDestroy();
        stand.remove();
    }

    public int getCost() {
        return cost;
    }

    public Construct setCost(int ironCost) {
        this.cost = ironCost;
        return this;
    }

    public void levelUp() {
        level = Math.min(level + 1, MAX_LEVEL);

        // Update health
        final double health = healthScaled().get(level, 10.0d);

        stand.setMaxHealth(health);

        if (stand.getHealth() < health / 2) {
            stand.setHealth(health / 2);
        }

        onLevelUp(level);
    }

    public int getLevel() {
        return level;
    }

    @Nonnull
    public ArmorStand getStand() {
        return stand;
    }
}