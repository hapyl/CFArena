package me.hapyl.fight.database.legacy;

import com.google.common.collect.Lists;
import me.hapyl.fight.database.DatabaseLegacy;
import me.hapyl.fight.database.entry.CosmeticEntry;
import me.hapyl.fight.game.cosmetic.Cosmetics;
import me.hapyl.fight.game.cosmetic.Type;
import me.hapyl.spigotutils.module.util.Validate;
import org.bukkit.Bukkit;

import javax.annotation.Nullable;
import java.util.List;

public class CosmeticEntryLegacy extends CosmeticEntry {
    public CosmeticEntryLegacy(DatabaseLegacy database) {
        super(database);
    }

    @Nullable
    @Override
    public Cosmetics getSelected(Type type) {
        final String selected = getConfigLegacy().getString("cosmetics.selected." + type.name(), "");
        return Validate.getEnumValue(Cosmetics.class, selected);
    }

    @Override
    public void unsetSelected(Type type) {
        getConfigLegacy().set("cosmetics.selected." + type.name(), null);
    }

    @Override
    public void setSelected(Type type, Cosmetics cosmetic) {
        getConfigLegacy().set("cosmetics.selected." + type.name(), cosmetic.name());
    }

    @Override
    public boolean hasCosmetic(Cosmetics cosmetic) {
        return getOwnedCosmeticsAsCosmetic().contains(cosmetic);
    }

    @Override
    public void addOwned(Cosmetics cosmetic) {
        final List<String> owned = getConfigLegacy().getStringList("cosmetics.owned");
        owned.add(cosmetic.name());

        getConfigLegacy().set("cosmetics.owned", owned);
    }

    private List<String> getOwnedCosmetics() {
        return getConfigLegacy().getStringList("cosmetics.owned");
    }

    @Override
    public void removeOwned(Cosmetics cosmetic) {
        final List<String> owned = getConfigLegacy().getStringList("cosmetics.owned");
        owned.remove(cosmetic.name());

        getConfigLegacy().set("cosmetics.owned", owned);
    }

    @Override
    public List<Cosmetics> getOwnedCosmeticsAsCosmetic() {
        final List<Cosmetics> cosmetics = Lists.newArrayList();
        for (String ownedCosmetic : getOwnedCosmetics()) {
            final Cosmetics cosmetic = Validate.getEnumValue(Cosmetics.class, ownedCosmetic);

            if (cosmetic == null) {
                Bukkit.getLogger().warning("Null cosmetic in legacy database read: " + ownedCosmetic);
                continue;
            }

            cosmetics.add(cosmetic);
        }

        return cosmetics;
    }
}
