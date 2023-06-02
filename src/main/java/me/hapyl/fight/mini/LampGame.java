package me.hapyl.fight.mini;

import me.hapyl.fight.Main;
import me.hapyl.spigotutils.module.util.DependencyInjector;
import org.bukkit.event.Listener;
import org.bukkit.util.BoundingBox;

public class LampGame extends DependencyInjector<Main> implements Listener {

    private final BoundingBox boundingBox = new BoundingBox(-3, 63, -19, 4, 67, -18);

    public LampGame(Main plugin) {
        super(plugin);
    }
}
