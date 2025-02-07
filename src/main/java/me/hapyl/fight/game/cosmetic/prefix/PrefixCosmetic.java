package me.hapyl.fight.game.cosmetic.prefix;

import me.hapyl.eterna.module.inventory.ItemBuilder;
import me.hapyl.eterna.module.registry.Key;
import me.hapyl.fight.CF;
import me.hapyl.fight.game.cosmetic.Cosmetic;
import me.hapyl.fight.game.cosmetic.Display;
import me.hapyl.fight.game.cosmetic.Rarity;
import me.hapyl.fight.game.cosmetic.Type;
import me.hapyl.fight.game.profile.PlayerProfile;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import javax.annotation.Nonnull;

public class PrefixCosmetic extends Cosmetic {

    private final String prefix;

    public PrefixCosmetic(@Nonnull Key key, @Nonnull String name, @Nonnull String description, @Nonnull String prefix, @Nonnull Rarity rarity, @Nonnull Material icon, boolean exclusive) {
        super(key, name, Type.PREFIX);

        setDescription(description);

        setRarity(rarity);
        setIcon(icon);
        setExclusive(exclusive);

        this.prefix = prefix;
    }

    @Nonnull
    @Override
    public ItemBuilder createItem(Player player) {
        final ItemBuilder builder = super.createItem(player);

        builder.addLore();
        builder.addLore("&bPreview: ");
        builder.addLore(" " + getPrefixPreview(player));

        return builder;
    }

    public String getPrefix() {
        return prefix;
    }

    public String getPrefixPreview(Player player) {
        final PlayerProfile profile = CF.getProfile(player);

        return profile.getDisplay().getPrefixPreview(this);
    }

    @Override
    public final void onDisplay(@Nonnull Display display) {
    }
}
