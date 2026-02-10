package me.hapyl.fight.game.cosmetic.contrail;

import me.hapyl.eterna.module.inventory.ItemBuilder;
import me.hapyl.eterna.module.registry.Key;
import me.hapyl.eterna.module.util.SmallCaps;
import me.hapyl.fight.game.cosmetic.Cosmetic;
import me.hapyl.fight.game.cosmetic.Display;
import me.hapyl.fight.game.cosmetic.Rarity;
import me.hapyl.fight.game.cosmetic.Type;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;

import javax.annotation.Nonnull;

public abstract class ContrailCosmetic extends Cosmetic implements Listener {

    protected static final ContrailType PARTICLE = ContrailType.of("particle", "It will follow behind you.");
    protected static final ContrailType BLOCK = ContrailType.of("block", "It will convert blocks you're walking on.");

    private final ContrailType type;

    public ContrailCosmetic(@Nonnull Key key, @Nonnull String name, @Nonnull String description, @Nonnull Rarity rarity, @Nonnull ContrailType type) {
        super(key, name, Type.CONTRAIL);

        setDescription(description);
        setRarity(rarity);

        this.type = type;
    }

    @Nonnull
    @Override
    public ItemBuilder createItem(Player player) {
        final ItemBuilder builder = super.createItem(player);

        builder.addLore();
        builder.addLore("&6&nᴛʜɪꜱ ɪꜱ %s ᴄᴏɴᴛʀᴀɪʟ".formatted(SmallCaps.format(type.type())));
        builder.addSmartLore(type.description(), "&e&o");

        return builder;
    }

    public abstract void onMove(@Nonnull Display display, int tick);

    public void onStandingStill(@Nonnull Display display, int tick) {
    }

    @Override
    public final void onDisplay(@Nonnull Display display) {
        onMove(display, 0);
    }
}
