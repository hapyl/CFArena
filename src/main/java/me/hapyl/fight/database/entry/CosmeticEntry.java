package me.hapyl.fight.database.entry;

import com.google.common.collect.Lists;
import me.hapyl.fight.database.PlayerDatabase;
import me.hapyl.fight.database.PlayerDatabaseEntry;
import me.hapyl.fight.game.cosmetic.Cosmetics;
import me.hapyl.fight.game.cosmetic.Type;
import me.hapyl.spigotutils.module.util.Validate;
import org.bson.Document;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public class CosmeticEntry extends PlayerDatabaseEntry {

    public CosmeticEntry(PlayerDatabase playerDatabase) {
        super(playerDatabase);
    }

    @Nullable
    public Cosmetics getSelected(Type type) {
        final Document cosmetics = getDocument().get("cosmetics", new Document());
        final Document selected = cosmetics.get("selected", new Document());

        return Validate.getEnumValue(Cosmetics.class, selected.get(type.name(), ""));
    }

    public void unsetSelected(Type type) {
        final Document cosmetics = getDocument().get("cosmetics", new Document());
        final Document selected = cosmetics.get("selected", new Document());

        selected.remove(type.name());
        cosmetics.put("selected", selected);

        getDocument().put("cosmetics", cosmetics);
    }

    public void setSelected(Type type, Cosmetics cosmetic) {
        final Document cosmetics = getDocument().get("cosmetics", new Document());
        final Document selected = cosmetics.get("selected", new Document());

        selected.put(type.name(), cosmetic.name());
        cosmetics.put("selected", selected);

        getDocument().put("cosmetics", cosmetics);
    }

    public boolean hasCosmetic(Cosmetics cosmetic) {
        return getOwnedCosmeticsAsCosmetic().contains(cosmetic);
    }

    public void addOwned(Cosmetics cosmetic) {
        if (hasCosmetic(cosmetic)) {
            return;
        }

        final Document cosmetics = getDocument().get("cosmetics", new Document());
        final ArrayList<Object> owned = cosmetics.get("owned", Lists.newArrayList());
        owned.add(cosmetic.name());

        cosmetics.put("owned", owned);
        getDocument().put("cosmetics", cosmetics);
    }

    public void removeOwned(Cosmetics cosmetic) {
        final Document cosmetics = getDocument().get("cosmetics", new Document());
        final ArrayList<Object> owned = cosmetics.get("owned", Lists.newArrayList());
        owned.remove(cosmetic.name());

        cosmetics.put("owned", owned);
        getDocument().put("cosmetics", cosmetics);
    }

    private List<String> getOwnedCosmetics() {
        return getDocument().get("cosmetics", new Document()).get("owned", Lists.newArrayList());
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
