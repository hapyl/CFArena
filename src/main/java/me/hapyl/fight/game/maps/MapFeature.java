package me.hapyl.fight.game.maps;

import me.hapyl.fight.CF;
import me.hapyl.fight.annotate.AutoRegisteredListener;
import me.hapyl.fight.game.GameElement;
import me.hapyl.fight.game.Manager;
import me.hapyl.fight.game.PlayerElement;
import me.hapyl.fight.util.Described;
import org.bukkit.event.Listener;

import javax.annotation.Nonnull;

@AutoRegisteredListener
public abstract class MapFeature implements Described, GameElement, PlayerElement {

    private final String name;
    private final String description;

    public MapFeature(String name, String description) {
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

    protected final boolean validateCurrentMap(GameMaps maps) {
        return Manager.current().getCurrentMap() == maps;
    }

    protected final boolean validateGameAndMap(GameMaps map) {
        return validateCurrentMap(map) && Manager.current().isGameInProgress();
    }

}
