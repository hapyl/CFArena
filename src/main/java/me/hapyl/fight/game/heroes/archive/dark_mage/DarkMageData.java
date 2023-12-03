package me.hapyl.fight.game.heroes.archive.dark_mage;

import me.hapyl.fight.game.entity.GamePlayer;
import me.hapyl.fight.game.heroes.PlayerData;
import me.hapyl.fight.game.heroes.archive.witcher.WitherData;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class DarkMageData extends PlayerData {

    private final DarkMageSpell darkMageSpell;
    private WitherData witherData;

    public DarkMageData(GamePlayer player) {
        super(player);

        this.darkMageSpell = new DarkMageSpell(player);
    }

    @Nullable
    public WitherData getWitherData() {
        return witherData;
    }

    @Nonnull
    public DarkMageSpell getDarkMageSpell() {
        return darkMageSpell;
    }

    @Override
    public void remove() {
        removeWither();
        darkMageSpell.remove();
    }

    public void newWither() {
        removeWither();
        witherData = new WitherData(player);
    }

    public void removeWither() {
        if (witherData != null) {
            witherData.remove();
        }

        witherData = null;
    }

    public void cast() {
        darkMageSpell.cast(this);
    }
}
