package me.hapyl.fight.game.talents.engineer;

import me.hapyl.eterna.module.util.BukkitUtils;
import me.hapyl.eterna.module.util.RomanNumber;
import me.hapyl.fight.game.entity.GamePlayer;
import me.hapyl.fight.game.entity.LivingGameEntity;
import me.hapyl.fight.game.heroes.HeroRegistry;
import me.hapyl.fight.game.heroes.engineer.Engineer;
import me.hapyl.fight.game.task.TickingGameTask;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;

import javax.annotation.Nonnull;

public abstract class Construct extends TickingGameTask {

    public static final int MAX_LEVEL = 3;
    public static final int MAX_DURATION_SEC = 5;

    protected final GamePlayer player;
    protected final Location location;
    @Nonnull
    protected final ConstructEntity entity;
    protected final EngineerTalent talent;
    private final Engineer hero;
    private int level;
    private int cost;
    private int upgradeCost;

    public Construct(@Nonnull GamePlayer player, @Nonnull Location location, @Nonnull EngineerTalent talent) {
        this.player = player;
        this.location = location;
        this.level = 0;
        this.upgradeCost = talent.getUpgradeCost();
        this.talent = talent;
        this.hero = HeroRegistry.ENGINEER;

        this.entity = new ConstructEntity(player, this);

        onCreate();
    }

    @Nonnull
    public String getName() {
        return talent.getName();
    }

    public int getDuration() {
        return durationScaled().get(level, Construct.MAX_DURATION_SEC) * 20;
    }

    @Nonnull
    public Location getLocation() {
        return BukkitUtils.newLocation(location);
    }

    @Override
    public void run(int tick) {
        final int duration = getDuration();

        if (tick > duration || entity.isDead()) {
            if (entity.isDead()) {
                player.sendMessage("&6&l\uD83D\uDD27 &cYour %s was destroyed!", getName());
            }

            hero.removeConstruct(player);
            return;
        }

        entity.tick();
        onTick();
    }

    public int getUpgradeCost() {
        return upgradeCost;
    }

    public void setUpgradeCost(int upgradeCost) {
        this.upgradeCost = upgradeCost;
    }

    @Nonnull
    public ImmutableArray<Integer> durationScaled() {
        return ImmutableArray.empty();
    }

    @Nonnull
    public ImmutableArray<Double> healthScaled() {
        return ImmutableArray.empty();
    }

    /**
     * Called once upon creating.
     */
    public abstract void onCreate();

    /**
     * Called once upon destroyed, be it because the entity died, duration runs out or any other cause.
     */
    public abstract void onDestroy();

    /**
     * Called every tick.
     */
    public abstract void onTick();

    /**
     * Called every time the construct successfully levels up.
     */
    public void onLevelUp() {
    }

    /**
     * Removes this construct.
     * <p>
     * Note that this only removes the construct itself; To properly remove the construct, use {@link Engineer#removeConstruct(GamePlayer)}
     */
    public void remove() {
        cancel();
        onDestroy();
        entity.remove();

        // Fx
        player.playWorldSound(location, Sound.ENTITY_ITEM_BREAK, 0.75f);
        player.playWorldSound(location, Sound.ENTITY_IRON_GOLEM_DAMAGE, 0.25f);
    }

    public int getCost() {
        return cost;
    }

    public Construct setCost(int ironCost) {
        this.cost = ironCost;
        return this;
    }

    public boolean levelUp() {
        if (level >= (MAX_LEVEL - 1)) {
            player.sendMessage("&6&l🔧 &cAlready at max level!");
            player.playSound(Sound.BLOCK_ANVIL_LAND, 1.0f);
            return false;
        }

        level++;

        // Update health
        final double health = healthScaled().get(level, 10.0d);
        final double halfHealth = health / 2;

        final LivingGameEntity entity = this.entity.getEntity();

        entity.getAttributes().setHealth(health);

        // If health < than half of the new max health, heal to half.
        if (entity.getHealth() < halfHealth) {
            entity.setHealth(halfHealth);
        }

        // Update display entity
        this.entity.setDisplayEntity(level);

        onLevelUp();

        // Fx
        player.playWorldSound(location, Sound.BLOCK_ANVIL_USE, 0.75f);
        player.playWorldSound(location, Sound.ENTITY_IRON_GOLEM_REPAIR, 1.25f);

        player.spawnWorldParticle(location, Particle.HAPPY_VILLAGER, 10, 0.25d, 0.25d, 0.25, 0f);

        player.sendMessage("&6&l🔧 &eLevelled up %s to level &l%s&e!", getName(), getLevelRoman());
        return true;
    }

    public int getLevel() {
        return level;
    }

    @Nonnull
    public ConstructEntity getEntity() {
        return entity;
    }

    public boolean checkEntity(Entity entity) {
        if (!(entity instanceof LivingEntity livingEntity)) {
            return false;
        }
        return this.entity.getEntity().is(livingEntity);
    }

    @Nonnull
    public String getLevelRoman() {
        return RomanNumber.toRoman(level + 1);
    }

    @Nonnull
    public String getDurationLeft() {
        return BukkitUtils.decimalFormat((getDuration() - getTick()) / 20.0d);
    }
}