package me.hapyl.fight.game.cosmetic;

import me.hapyl.fight.game.profile.PlayerProfile;
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

    public String getPrefix() {
        return prefix;
    }

    public String getPrefixPreview(Player player) {
        return PlayerProfile.getOrCreateProfile(player).getDisplay().getPrefixPreview(this);
    }

    @Override
    public void onDisplay(Display display) {

    }
}
