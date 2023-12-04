package me.hapyl.fight.game.talents.archive.engineer;

import me.hapyl.fight.game.entity.GamePlayer;
import me.hapyl.fight.game.heroes.Heroes;
import me.hapyl.fight.game.heroes.archive.engineer.Engineer;
import me.hapyl.fight.game.task.TickingGameTask;
import me.hapyl.spigotutils.module.entity.Entities;
import org.bukkit.Location;
import org.bukkit.entity.ArmorStand;

import javax.annotation.Nonnull;

public abstract class Construct extends TickingGameTask {

    public static final int MAX_LEVEL = 3;
    public static final int MAX_DURATION_SEC = 5;

    protected final GamePlayer player;
    protected final Location location;
    @Nonnull
    protected final ArmorStand stand;
    private final EngineerTalent talent;

    private int level;
    private int cost;

    public Construct(@Nonnull GamePlayer player, @Nonnull Location location, @Nonnull EngineerTalent talent) {
        this.player = player;
        this.location = location;
        this.level = 0;
        this.talent = talent;

        stand = Entities.ARMOR_STAND.spawn(location, self -> {
            final double health = healthScaled().get(0, 10.0d);

            self.setMaxHealth(health);
            self.setHealth(health);
        });

        onCreate();
    }

    public String getName(){
        return talent.getName();
    }

    @Override
    public void run(int tick) {
        final int duration = durationScaled().get(getLevel(), Construct.MAX_DURATION_SEC) * 20;

        if (tick > duration || stand.isDead()) {
            remove();
            Heroes.ENGINEER.getHero(Engineer.class).constructs.remove(player);
            return;
        }

        onTick();
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
        cancel();
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