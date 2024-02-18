package me.hapyl.fight.game.collectible;

import me.hapyl.fight.Main;
import me.hapyl.fight.game.collectible.relic.RelicHunt;
import me.hapyl.spigotutils.module.util.DependencyInjector;

import javax.annotation.Nonnull;

public class Collectibles extends DependencyInjector<Main> {

    private final RelicHunt relicHunt;

    public Collectibles(Main plugin) {
        super(plugin);

        this.relicHunt = new RelicHunt(plugin);
    }

    @Nonnull
    public RelicHunt getRelicHunt() {
        return relicHunt;
    }
}
