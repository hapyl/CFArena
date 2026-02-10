package me.hapyl.fight.game.talents.engineer;

import me.hapyl.eterna.module.annotate.EventLike;
import me.hapyl.eterna.module.util.BukkitUtils;
import me.hapyl.eterna.module.util.Removable;
import me.hapyl.eterna.module.util.RomanNumber;
import me.hapyl.eterna.module.util.Ticking;
import me.hapyl.fight.game.entity.GamePlayer;
import me.hapyl.fight.util.hitbox.Hitbox;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Sound;

import javax.annotation.Nonnull;
import java.util.Objects;

public abstract class Construct implements Ticking, Removable {
    
    public static final int MAX_LEVEL = 3;
    
    public static final int HEALING_COST = 2;
    public static final double HEALING = 0.2;
    
    protected final GamePlayer player;
    protected final Location location;
    
    @Nonnull
    protected final ConstructEntity constructEntity;
    protected final EngineerConstructTalent talent;
    
    protected int tick;
    protected int level;
    protected int charges;
    
    Construct(@Nonnull GamePlayer player, @Nonnull Location location, @Nonnull EngineerConstructTalent talent) {
        this.player = player;
        this.location = location;
        this.level = 0;
        this.talent = talent;
        
        this.constructEntity = Hitbox.create(
                location,
                healthScaled().get(0),
                3.5,
                (slime, scale) -> new ConstructEntity(slime, scale, player, this)
        );
        
        this.charges = chargesScaled().get(0);
        
        onCreate();
    }
    
    @Nonnull
    public String getName() {
        return talent.getName();
    }
    
    @Nonnull
    public String toString() {
        return "&c&l%.0f &c❤ &6&l%s &6●".formatted(constructEntity.getHealth(), charges);
    }
    
    public int getDuration() {
        return chargesScaled().get(level) * 20;
    }
    
    @Nonnull
    public Location getLocation() {
        return BukkitUtils.newLocation(location);
    }
    
    @Override
    public final void tick() {
        if (onTick()) {
            charges--;
        }
        
        tick++;
    }
    
    @Override
    public boolean shouldRemove() {
        return charges == 0 || constructEntity.isDead();
    }
    
    @Nonnull
    public abstract ImmutableInt3Array chargesScaled();
    
    @Nonnull
    public abstract ImmutableInt3Array healthScaled();
    
    /**
     * Called once upon creating.
     */
    @EventLike
    public abstract void onCreate();
    
    /**
     * Called once upon destroyed, be it because the entity died, duration runs out or any other cause.
     */
    @EventLike
    public abstract void onDestroy();
    
    /**
     * Called every tick.
     *
     * @return {@code true} if a charge should be decremented, {@code false} otherwise.
     */
    @EventLike
    public abstract boolean onTick();
    
    /**
     * Called every time the construct successfully levels up.
     */
    @EventLike
    public void onLevelUp() {
    }
    
    @Override
    public void remove() {
        onDestroy();
        constructEntity.remove();
        
        // Fx
        player.playWorldSound(location, Sound.ENTITY_ITEM_BREAK, 0.75f);
        player.playWorldSound(location, Sound.ENTITY_IRON_GOLEM_DAMAGE, 0.25f);
    }
    
    public void levelUp() {
        if (isMaxLevel()) {
            message("&cAlready at max level!");
            player.playSound(Sound.BLOCK_ANVIL_LAND, 1.0f);
            return;
        }
        
        level++;
        
        // Update health
        final double healthPercentBeforeUpgrade = constructEntity.getHealthToMaxHealthPercent();
        final double newHealth = healthScaled().get(level);
        
        constructEntity.getAttributes().setMaxHealth(newHealth);
        constructEntity.setHealth(newHealth * Math.min(1, healthPercentBeforeUpgrade));
        
        // Update charges
        final int previousCharge = chargesScaled().get(level - 1);
        
        this.charges = chargesScaled().get(level) - (previousCharge - charges);
        
        onLevelUp();
        
        // Fx
        player.playWorldSound(location, Sound.BLOCK_ANVIL_USE, 0.75f);
        player.playWorldSound(location, Sound.ENTITY_IRON_GOLEM_REPAIR, 1.25f);
        
        player.spawnWorldParticle(location, Particle.HAPPY_VILLAGER, 10, 0.25d, 0.25d, 0.25, 0f);
        
        message("Levelled up %s to level &l%s&e!".formatted(getName(), getLevelRoman()));
    }
    
    public void message(@Nonnull String message) {
        player.sendMessage("&6&l🔧 &e" + message);
    }
    
    public int getLevel() {
        return level;
    }
    
    @Nonnull
    public ConstructEntity getEntity() {
        return constructEntity;
    }
    
    @Nonnull
    public String getLevelRoman() {
        return RomanNumber.toRoman(level + 1);
    }
    
    @Override
    public final boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        
        final Construct that = (Construct) o;
        return Objects.equals(this.talent, that.talent);
    }
    
    @Override
    public final int hashCode() {
        return Objects.hashCode(talent);
    }
    
    public boolean isMaxLevel() {
        return level + 1 >= MAX_LEVEL;
    }
    
    public boolean isFullHealth() {
        return constructEntity.isFullHealth();
    }
    
    public void heal() {
        constructEntity.healRelativeToMaxHealth(HEALING, player);
        
        // Fx
        player.playWorldSound(location, Sound.ENTITY_IRON_GOLEM_REPAIR, 1.25f);
        player.playWorldSound(location, Sound.ENTITY_ILLUSIONER_CAST_SPELL, 1.25f);
    }
    
}