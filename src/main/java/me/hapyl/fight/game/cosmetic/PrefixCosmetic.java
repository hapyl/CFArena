package me.hapyl.fight.game.cosmetic;

import me.hapyl.fight.game.profile.PlayerProfile;
import me.hapyl.spigotutils.module.inventory.ItemBuilder;
import org.bukkit.entity.Player;

import javax.annotation.Nonnull;

public class PrefixCosmetic extends Cosmetic {

    private final String prefix;

    public PrefixCosmetic(String name, String description, String prefix, Rarity rarity) {
        super(name, description, Type.PREFIX, rarity);

        this.prefix = prefix;
    }

    @Override
    public void setHandle(@Nonnull Cosmetics cosmetics) {
        super.setHandle(cosmetics);
        cosmetics.setCollectionAndAdd(CosmeticCollection.PREFIX);
    }

    @Override
    public void addExtraLore(@Nonnull ItemBuilder builder, @Nonnull Player player) {
        builder.addLore();
        builder.addLore("&bPreview: ");
        builder.addLore(" " + getPrefixPreview(player));
    }

    public String getPrefix() {
        return prefix;
    }

    public String getPrefixPreview(Player player) {
        final PlayerProfile profile = PlayerProfile.getProfile(player);
        return profile != null ? profile.getDisplay().getPrefixPreview(this) : "NO_PROFILE";
    }

    @Override
    public void onDisplay(Display display) {
    }
}
