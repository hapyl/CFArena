package me.hapyl.fight.game.cosmetic.contrail;

import me.hapyl.eterna.module.inventory.ItemBuilder;
import me.hapyl.eterna.module.registry.Key;
import me.hapyl.eterna.module.util.SmallCaps;
import me.hapyl.eterna.module.util.Tuple;
import me.hapyl.fight.game.cosmetic.Cosmetic;
import me.hapyl.fight.game.cosmetic.Display;
import me.hapyl.fight.game.cosmetic.Rarity;
import me.hapyl.fight.game.cosmetic.Type;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;

import javax.annotation.Nonnull;

public abstract class ContrailCosmetic extends Cosmetic implements Listener {

    private final Tuple<String, String> contrailDescription;

    public ContrailCosmetic(@Nonnull Key key, @Nonnull String name, @Nonnull String description, @Nonnull Rarity rarity, @Nonnull Tuple<String, String> contrailDescription) {
        super(key, name, Type.CONTRAIL);

        setDescription(description);
        setRarity(rarity);

        this.contrailDescription = contrailDescription;
    }

    @Nonnull
    @Override
    public ItemBuilder createItem(Player player) {
        final ItemBuilder builder = super.createItem(player);

        builder.addLore();
        builder.addLore("&6&nᴛʜɪꜱ ɪꜱ %s ᴄᴏɴᴛʀᴀɪʟ".formatted(SmallCaps.format(contrailDescription.a())));
        builder.addSmartLore(contrailDescription.b(), "&e&o");

        return builder;
    }

    public abstract void onMove(@Nonnull Display display);

    @Override
    public final void onDisplay(@Nonnull Display display) {
        onMove(display);
    }
}
