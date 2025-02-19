package me.hapyl.fight.game.effect;

import me.hapyl.eterna.module.util.Described;
import me.hapyl.fight.CF;
import me.hapyl.fight.annotate.AutoRegisteredListener;
import me.hapyl.fight.game.Constants;
import me.hapyl.fight.game.entity.GamePlayer;
import me.hapyl.fight.game.entity.LivingGameEntity;
import me.hapyl.fight.game.ui.display.StringDisplay;
import org.bukkit.Location;
import org.bukkit.event.Listener;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

@AutoRegisteredListener
public abstract class Effect implements Described {

    private final String name;
    private final EffectType type;

    private String description;
    private EffectParticle effectParticle;
    private StringDisplay display;

    protected Effect(@Nonnull String name, @Nonnull EffectType type) {
        this.name = name + Constants.DEFAULT_LORE_COLOR;
        this.description = "";
        this.type = type;

        if (this instanceof Listener listener) {
            CF.registerEvents(listener);
        }
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

    @Nonnull
    public EffectType getType() {
        return type;
    }

    /**
     * Called once upon entity gaining this effect.
     *
     * @param entity    - Entity.
     * @param amplifier - Amplifier.
     * @param duration  - Duration.
     */
    public abstract void onStart(@Nonnull LivingGameEntity entity, int amplifier, int duration);

    /**
     * Called once upon entity losing this effect.
     *
     * @param entity    - Entity.
     * @param amplifier - Amplifier.
     */
    public abstract void onStop(@Nonnull LivingGameEntity entity, int amplifier);

    /**
     * Called every tick entity has this effect.
     *
     * @param entity - Entity.
     * @param tick   - Current tick.
     */
    public void onTick(@Nonnull LivingGameEntity entity, int tick) {
    }

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
