package me.hapyl.fight.game.maps;

import me.hapyl.fight.CF;
import me.hapyl.fight.annotate.AutoRegisteredListener;
import me.hapyl.fight.game.Manager;
import me.hapyl.fight.game.element.PlayerElementHandler;
import me.hapyl.fight.util.Described;
import me.hapyl.fight.util.Lifecycle;
import org.bukkit.event.Listener;

import javax.annotation.Nonnull;

@AutoRegisteredListener
public abstract class LevelFeature implements Described, Lifecycle, PlayerElementHandler {

    private final String name;
    private final String description;

    public LevelFeature(String name, String description) {
        this.name = name;
        this.description = description;

        if (this instanceof Listener listener) {
            CF.registerEvents(listener);
        }
    }

    @Override
    public void onStart() {
    }

    @Override
    public void onStop() {
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

    public void tick(int tick) {
    }

    protected final boolean validateCurrentMap(EnumLevel maps) {
        return Manager.current().getCurrentMap() == maps;
    }

    protected final boolean validateGameAndMap(EnumLevel map) {
        return validateCurrentMap(map) && Manager.current().isGameInProgress();
    }

}
