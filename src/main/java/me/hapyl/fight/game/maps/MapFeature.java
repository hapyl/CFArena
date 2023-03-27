package me.hapyl.fight.game.maps;

import me.hapyl.fight.Main;
import me.hapyl.fight.game.GameElement;
import me.hapyl.fight.game.Manager;
import me.hapyl.fight.game.PlayerElement;
import org.bukkit.event.Listener;

public abstract class MapFeature implements GameElement, PlayerElement {

    private final String name;
    private final String info;

    public MapFeature(String name, String info) {
        this.name = name;
        this.info = info;

        if (this instanceof Listener listener) {
            Main.getPlugin().addEvent(listener);
        }
    }

    public String getName() {
        return name;
    }

    public String getInfo() {
        return info;
    }

    @Override
    public void onStart() {

    }

    @Override
    public void onStop() {

    }

    protected final boolean validateCurrentMap(GameMaps maps) {
        return Manager.current().getCurrentMap() == maps;
    }

    protected final boolean validateGameAndMap(GameMaps map) {
        return validateCurrentMap(map) && Manager.current().isGameInProgress();
    }

    /**
     * @param tick a modulo value of 20 of the runnable.
     */
    public abstract void tick(int tick);

}
