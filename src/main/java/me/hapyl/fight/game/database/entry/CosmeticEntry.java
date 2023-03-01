package me.hapyl.fight.game.database.entry;

import com.google.common.collect.Lists;
import me.hapyl.fight.game.cosmetic.Cosmetics;
import me.hapyl.fight.game.cosmetic.Type;
import me.hapyl.fight.game.database.Database;
import me.hapyl.fight.game.database.DatabaseEntry;
import me.hapyl.spigotutils.module.util.Validate;

import javax.annotation.Nullable;
import java.util.List;

public class CosmeticEntry extends DatabaseEntry {

    public CosmeticEntry(Database database) {
        super(database);
    }

    @Nullable
    public Cosmetics getSelected(Type type) {
        final String name = getConfig().getString("cosmetics.selected." + type.name());
        return name == null ? null : Validate.getEnumValue(Cosmetics.class, name);
    }

    public void unsetSelected(Type type) {
        getConfig().set("cosmetics.selected." + type.name(), null);
    }

    public void setSelected(Type type, Cosmetics cosmetic) {
        getConfig().set("cosmetics.selected." + type.name(), cosmetic.name());
    }

    public boolean hasCosmetic(Cosmetics cosmetic) {
        return getOwnedCosmetics().contains(cosmetic);
    }

    public void addOwned(Cosmetics cosmetic) {
        final List<String> cosmetics = getOwnedCosmeticsNames();
        cosmetics.add(cosmetic.name());
        getConfig().set("cosmetics.owned", cosmetics);
    }

    public void removeOwned(Cosmetics cosmetic) {
        final List<String> cosmetics = getOwnedCosmeticsNames();
        cosmetics.remove(cosmetic.name());
        getConfig().set("cosmetics.owned", cosmetics);
    }

    private List<String> getOwnedCosmeticsNames() {
        return getConfig().getStringList("cosmetics.owned");
    }

    public List<Cosmetics> getOwnedCosmetics() {
        final List<String> names = getOwnedCosmeticsNames();
        final List<Cosmetics> cosmetics = Lists.newArrayList();

        for (String name : names) {
            if (name == null) {
                throw new IllegalArgumentException("Null cosmetic read.");
            }

            cosmetics.add(Validate.getEnumValue(Cosmetics.class, name));
        }

        return cosmetics;
    }


}
