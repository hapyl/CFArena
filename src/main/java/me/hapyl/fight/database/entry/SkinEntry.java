package me.hapyl.fight.database.entry;

import me.hapyl.fight.database.PlayerDatabase;
import me.hapyl.fight.database.StrictPlayerDatabaseEntry;
import me.hapyl.fight.game.cosmetic.skin.Skins;
import me.hapyl.fight.game.heroes.Heroes;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class SkinEntry extends StrictPlayerDatabaseEntry {

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
    public Skins getSelected(@Nonnull Heroes hero) {
        return getEnumValue("selected." + hero.name().toLowerCase(), Skins.class);
    }

    public void setSelected(@Nonnull Heroes hero, @Nullable Skins skin) {
        setEnumValue("selected." + hero.name().toLowerCase(), skin);
    }

}
