package me.hapyl.fight.game.cosmetic.prefix;

import me.hapyl.eterna.module.registry.Key;
import me.hapyl.fight.game.cosmetic.Rarity;
import me.hapyl.fight.util.StaticUUID;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;

import javax.annotation.Nonnull;

public class AdminPrefixCosmetic extends PrefixCosmetic {

    private final StaticUUID uuid;

    public AdminPrefixCosmetic(@Nonnull Key key, @Nonnull String name, @Nonnull String prefix, @Nonnull StaticUUID uuid) {
        super(key, "Admin: " + name, "Exclusive to " + name + "!", prefix, Rarity.COMMON, Material.RED_DYE, true);

        this.uuid = uuid;
    }

    @Override
    public boolean canObtain(@Nonnull OfflinePlayer player) {
        return uuid.matches(player);
    }
}
