package me.hapyl.fight.game.effect;

import me.hapyl.fight.Main;
import me.hapyl.fight.annotate.AutoRegisteredListener;
import me.hapyl.fight.game.entity.GamePlayer;
import me.hapyl.fight.game.entity.LivingGameEntity;
import me.hapyl.fight.game.ui.display.StringDisplay;
import me.hapyl.fight.util.Described;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.event.Listener;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

@AutoRegisteredListener
public abstract class GameEffect implements Described {

    private final String name;
    private String description;
    private EffectParticle effectParticle;
    private boolean isPositive;

    private StringDisplay display;
    private boolean talentBlocking;

    public GameEffect(String name) {
        this.name = name;
        this.description = "";
        this.isPositive = true;
        this.talentBlocking = false;

        if (this instanceof Listener listener) {
            Bukkit.getPluginManager().registerEvents(listener, Main.getPlugin());
        }
    }

    public boolean isTalentBlocking() {
        return talentBlocking;
    }

    public void setTalentBlocking(boolean talentBlocking) {
        this.talentBlocking = talentBlocking;
    }

    public StringDisplay getDisplay() {
        return display;
    }

    public void setDisplay(StringDisplay display) {
        this.display = display;
    }

    public void setEffectParticle(EffectParticle effectParticle) {
        this.effectParticle = effectParticle;
    }

    @Nonnull
    @Override
    public String getName() {
        return name;
    }

    @Nonnull
    @Override
    public String getDescription() {
        return description;
    }

    public void setDescription(@Nonnull String description) {
        this.description = description;
    }

    public void setDescription(@Nonnull String description, @Nullable Object... objects) {
        setDescription(description.formatted(objects));
    }

    public boolean isPositive() {
        return isPositive;
    }

    public void setPositive(boolean positive) {
        isPositive = positive;
    }

    /**
     * Called once upon entity gaining this effect.
     *
     * @param entity - Entity.
     */
    public abstract void onStart(@Nonnull LivingGameEntity entity);

    /**
     * Called once upon entity losing this effect.
     *
     * @param entity - Entity.
     */
    public abstract void onStop(@Nonnull LivingGameEntity entity);

    /**
     * Called every tick entity has this effect.
     *
     * @param entity - Entity.
     * @param tick   - Current tick.
     */
    public abstract void onTick(@Nonnull LivingGameEntity entity, int tick);

    /**
     * Called whenever this effect has added to the entity when it already had the effect.
     *
     * @param entity - Entity.
     */
    public void onUpdate(@Nonnull LivingGameEntity entity) {
    }

    public void displayParticles(@Nonnull Location location, @Nonnull LivingGameEntity ignore) {
        displayParticles(location, ignore, this.effectParticle);
    }

    public void displayParticles(@Nonnull Location location, @Nonnull LivingGameEntity ignore, @Nonnull EffectParticle particle) {
        particle.display(location, ignore instanceof GamePlayer player ? player : null);
    }

}
