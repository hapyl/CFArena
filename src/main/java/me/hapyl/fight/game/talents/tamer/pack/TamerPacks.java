package me.hapyl.fight.game.talents.tamer.pack;

import me.hapyl.spigotutils.module.util.CollectionUtils;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public enum TamerPacks {

    DR_WITCH(new DrWitch()),
    THE_WOLF_PACK(new TheWolfPack()),
    PIGMAN_RUSHER(new PigmanRusher()),
    LASER_ZOMBIE(new LaserZombie()),

    ;

    private final TamerPack pack;

    TamerPacks(TamerPack pack) {
        this.pack = pack;
    }

    @Nonnull
    public TamerPack getPack() {
        return pack;
    }

    @Nonnull
    public static TamerPacks random(@Nullable TamerPack previous) {
        final TamerPacks[] values = values();
        final TamerPacks randomElement = CollectionUtils.randomElement(values, DR_WITCH);

        if (values.length == 1 || previous == null || randomElement.pack != previous) {
            return randomElement;
        }

        return random(previous);
    }

}
