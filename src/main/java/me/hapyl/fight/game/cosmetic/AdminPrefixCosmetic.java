package me.hapyl.fight.game.cosmetic;

import me.hapyl.fight.util.StaticUUID;
import org.bukkit.OfflinePlayer;

import javax.annotation.Nonnull;

public class AdminPrefixCosmetic extends PrefixCosmetic {

    private final StaticUUID uuid;

    public AdminPrefixCosmetic(String name, String prefix, StaticUUID uuid) {
        super(name, "Exclusive to " + name + "!", prefix, Rarity.COMMON);

        this.uuid = uuid;

        setExclusive(true);
    }

    @Override
    public boolean canObtain(@Nonnull OfflinePlayer player) {
        return uuid.matches(player);
    }
}
