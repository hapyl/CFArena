package me.hapyl.fight.game.skin.trait;

import me.hapyl.fight.game.entity.GamePlayer;
import org.bukkit.Location;

import javax.annotation.Nonnull;

public abstract class SkinTraitOnMove extends SkinTrait {

    public SkinTraitOnMove(@Nonnull String name, @Nonnull String description) {
        super(name, description);
    }

    public abstract void onMove(@Nonnull GamePlayer player, @Nonnull Location to);

}
