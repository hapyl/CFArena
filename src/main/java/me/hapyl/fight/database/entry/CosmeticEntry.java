package me.hapyl.fight.database.entry;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import me.hapyl.fight.Message;
import me.hapyl.fight.database.PlayerDatabase;
import me.hapyl.fight.database.PlayerDatabaseEntry;
import me.hapyl.fight.game.cosmetic.Cosmetic;
import me.hapyl.fight.game.cosmetic.CosmeticRegistry;
import me.hapyl.fight.game.cosmetic.Type;
import me.hapyl.fight.registry.Registries;
import org.bukkit.entity.Player;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class CosmeticEntry extends PlayerDatabaseEntry {

    public CosmeticEntry(PlayerDatabase playerDatabase) {
        super(playerDatabase, "cosmetics");
    }

    @Nullable
    public Cosmetic getSelected(@Nonnull Type type) {
        return getRegistryValue(registry(), "selected.%s".formatted(type.getKeyAsString()));
    }

    public void unsetSelected(@Nonnull Type type) {
        final Cosmetic selected = getSelected(type);
        final Player player = getOnlinePlayer();

        setValue("selected.%s".formatted(type.getKeyAsString()), null);

        if (selected != null && player != null) {
            selected.onUnequip(player);
        }
    }

    public void setSelected(@Nonnull Type type, @Nonnull Cosmetic cosmetic) {
        final Player player = getOnlinePlayer();

        if (player != null && !cosmetic.canObtain(player)) {
            Message.error(player, "You cannot select this cosmetic!");
            return;
        }

        setValue("selected.%s".formatted(type.getKeyAsString()), cosmetic.getKeyAsString());

        if (player != null) {
            cosmetic.onEquip(player);
        }
    }

    public boolean isUnlocked(@Nonnull Cosmetic cosmetic) {
        return getOwnedCosmeticsAsCosmetic().contains(cosmetic);
    }

    public void addOwned(@Nonnull Cosmetic cosmetic) {
        if (isUnlocked(cosmetic)) {
            return;
        }

        final Player player = getOnlinePlayer();

        if (player != null && !cosmetic.canObtain(player)) {
            Message.error(player, "You cannot own this cosmetic!");
            return;
        }

        fetchDocumentValue("owned", new ArrayList<>(), list -> {
            list.add(cosmetic.getKeyAsString());
        });
    }

    public void removeOwned(@Nonnull Cosmetic cosmetic) {
        fetchDocumentValue("owned", new ArrayList<>(), list -> {
            list.remove(cosmetic.getKeyAsString());
        });
    }

    @Nonnull
    public Set<Cosmetic> getOwnedCosmeticsAsCosmetic() {
        final Set<Cosmetic> cosmetics = Sets.newHashSet();
        final List<String> ownedCosmetics = getOwnedCosmetics();

        for (String stringKey : ownedCosmetics) {
            cosmetics.add(registry().get(stringKey));
        }

        return cosmetics;
    }

    public void setUnlocked(@Nonnull Cosmetic cosmetic, boolean unlocked) {
        if (unlocked) {
            addOwned(cosmetic);
        }
        else {
            removeOwned(cosmetic);

            final Type type = cosmetic.getType();

            // Unset selected if was selected
            final Cosmetic selected = getSelected(type);

            if (selected != null && selected.equals(cosmetic)) {
                unsetSelected(type);
            }
        }
    }

    private CosmeticRegistry registry() {
        return Registries.cosmetics();
    }

    @Nonnull
    private List<String> getOwnedCosmetics() {
        return getValue("owned", Lists.newArrayList());
    }


}
