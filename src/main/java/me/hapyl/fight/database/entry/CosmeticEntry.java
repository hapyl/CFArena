package me.hapyl.fight.database.entry;

import com.google.common.collect.Lists;
import me.hapyl.fight.database.Database;
import me.hapyl.fight.database.DatabaseEntry;
import me.hapyl.fight.game.cosmetic.Cosmetics;
import me.hapyl.fight.game.cosmetic.Type;
import me.hapyl.spigotutils.module.util.Validate;
import org.bson.Document;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public class CosmeticEntry extends DatabaseEntry {

    public CosmeticEntry(Database database) {
        super(database);
    }

    @Nullable
    public Cosmetics getSelected(Type type) {
        final Document cosmetics = getConfig().get("cosmetics", new Document());
        final Document selected = cosmetics.get("selected", new Document());

        return Validate.getEnumValue(Cosmetics.class, selected.get(type.name(), ""));
    }

    public void unsetSelected(Type type) {
        final Document cosmetics = getConfig().get("cosmetics", new Document());
        final Document selected = cosmetics.get("selected", new Document());

        selected.remove(type.name());
        cosmetics.put("selected", selected);

        getConfig().put("cosmetics", cosmetics);
    }

    public void setSelected(Type type, Cosmetics cosmetic) {
        final Document cosmetics = getConfig().get("cosmetics", new Document());
        final Document selected = cosmetics.get("selected", new Document());

        selected.put(type.name(), cosmetic.name());
        cosmetics.put("selected", selected);

        getConfig().put("cosmetics", cosmetics);
    }

    public boolean hasCosmetic(Cosmetics cosmetic) {
        return getOwnedCosmeticsAsCosmetic().contains(cosmetic);
    }

    public void addOwned(Cosmetics cosmetic) {
        //        final List<String> ownedCosmetics = getOwnedCosmetics();
        //        ownedCosmetics.add(cosmetic.name());

        final Document cosmetics = getConfig().get("cosmetics", new Document());
        final ArrayList<Object> owned = cosmetics.get("owned", Lists.newArrayList());
        owned.add(cosmetic.name());

        cosmetics.put("owned", owned);
        getConfig().put("cosmetics", cosmetics);
    }

    public void removeOwned(Cosmetics cosmetic) {
        //        final List<String> ownedCosmetics = getOwnedCosmetics();
        //        ownedCosmetics.remove(cosmetic.name());

        final Document cosmetics = getConfig().get("cosmetics", new Document());
        final ArrayList<Object> owned = cosmetics.get("owned", Lists.newArrayList());
        owned.remove(cosmetic.name());

        cosmetics.put("owned", owned);
        getConfig().put("cosmetics", cosmetics);
    }

    private List<String> getOwnedCosmetics() {
        return getConfig().get("cosmetics", new Document()).get("owned", Lists.newArrayList());
    }

    public List<Cosmetics> getOwnedCosmeticsAsCosmetic() {
        final List<String> names = getOwnedCosmetics();
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
