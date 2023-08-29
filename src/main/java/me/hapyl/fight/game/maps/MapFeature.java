package me.hapyl.fight.game.maps;

import me.hapyl.fight.Main;
import me.hapyl.fight.annotate.AutoRegisteredListener;
import me.hapyl.fight.game.GameElement;
import me.hapyl.fight.game.Manager;
import me.hapyl.fight.game.PlayerElement;
import org.bukkit.Bukkit;
import org.bukkit.event.Listener;

@AutoRegisteredListener
public abstract class MapFeature implements GameElement, PlayerElement {

    private final String name;
    private final String info;

    public MapFeature(String name, String info) {
        this.name = name;
        this.info = info;

        if (this instanceof Listener listener) {
            Bukkit.getPluginManager().registerEvents(listener, Main.getPlugin());
        }
    }

    @Override
    public void onStart() {
    }

    @Override
    public void onStop() {
    }

    public String getName() {
        return name;
    }

    public String getInfo() {
        return info;
    }

    /**
     * @param tickMod20 a modulo value of 20 of the runnable.
     */
    public abstract void tick(int tickMod20);

    protected final boolean validateCurrentMap(GameMaps maps) {
        return Manager.current().getCurrentMap() == maps;
    }

    protected final boolean validateGameAndMap(GameMaps map) {
        return validateCurrentMap(map) && Manager.current().isGameInProgress();
    }

}
