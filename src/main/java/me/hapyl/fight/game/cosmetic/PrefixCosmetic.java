package me.hapyl.fight.game.cosmetic;

import me.hapyl.fight.game.profile.PlayerProfile;
import me.hapyl.fight.game.shop.Rarity;
import org.bukkit.entity.Player;

public class PrefixCosmetic extends Cosmetic {

    private final String prefix;

    public PrefixCosmetic(String name, String description, String prefix, long cost, Rarity rarity) {
        super(name, description, cost, Type.PREFIX, rarity);

        this.prefix = prefix;

    }

    public String getPrefix() {
        return prefix;
    }

    public String getPrefixPreview(Player player) {
        return PlayerProfile.getProfile(player).getDisplay().getPrefixPreview(this);
    }

    @Override
    public void onDisplay(Display display) {

    }
}
