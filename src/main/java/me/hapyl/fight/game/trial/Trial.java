package me.hapyl.fight.game.trial;

import me.hapyl.fight.Main;
import me.hapyl.spigotutils.module.locaiton.Trigger;
import me.hapyl.spigotutils.module.util.DependencyInjector;
import org.bukkit.entity.Player;

public class Trial extends DependencyInjector<Main> {

    private final Trigger trigger = null;

    public Trial(Main plugin) {
        super(plugin);
    }

    public boolean isInTrial(Player player) {
        return false;
    }
}
