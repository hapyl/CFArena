package me.hapyl.fight.game.entity.commission;

import me.hapyl.eterna.module.player.PlayerSkin;
import me.hapyl.eterna.module.registry.Key;
import me.hapyl.fight.CF;
import me.hapyl.fight.game.attribute.BaseAttributes;
import me.hapyl.fight.game.attribute.LowAttributes;
import org.bukkit.Location;

import javax.annotation.Nonnull;
import java.util.function.BiFunction;

public abstract class CommissionOverlayEntityType extends CommissionEntityType {

    private final PlayerSkin skin;

    public CommissionOverlayEntityType(@Nonnull Key key, @Nonnull String name, @Nonnull PlayerSkin skin) {
        this(key, name, skin, new LowAttributes());
    }

    public CommissionOverlayEntityType(@Nonnull Key key, @Nonnull String name, @Nonnull PlayerSkin skin, @Nonnull BaseAttributes attributes) {
        super(key, name, attributes);

        this.skin = skin;
    }

    @Nonnull
    public PlayerSkin skin() {
        return skin;
    }

    /**
     * {@link CF#createOverlayEntity(Location, BiFunction)}
     */
    @Nonnull
    @Override
    public abstract CommissionOverlayEntity create(@Nonnull Location location);
}
