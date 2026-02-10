package me.hapyl.fight.database.entry;

import me.hapyl.fight.database.PlayerDatabase;
import me.hapyl.fight.database.PlayerDatabaseEntry;
import me.hapyl.fight.game.skin.Skins;
import me.hapyl.fight.game.heroes.Hero;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class SkinEntry extends PlayerDatabaseEntry {

    public SkinEntry(@Nonnull PlayerDatabase database) {
        super(database, "skins");
    }

    public boolean isOwned(@Nonnull Skins skin) {
        return getValue("owned." + skin.name().toLowerCase(), false);
    }

    public void setOwned(@Nonnull Skins skin, boolean value) {
        setValue("owned." + skin.name().toLowerCase(), value ? true : null);
    }

    @Nullable
    public Skins getSelected(@Nonnull Hero hero) {
        return getEnumValue(Skins.class, "selected." + hero.getKeyAsString());
    }

    public void setSelected(@Nonnull Hero hero, @Nullable Skins skin) {
        setValue("selected." + hero.getKeyAsString(), skin != null ? skin.getKeyAsString() : null);
    }

}
