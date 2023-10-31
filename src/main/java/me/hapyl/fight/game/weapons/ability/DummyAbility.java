package me.hapyl.fight.game.weapons.ability;

import me.hapyl.fight.game.Response;
import me.hapyl.fight.game.entity.GamePlayer;
import org.bukkit.inventory.ItemStack;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class DummyAbility extends Ability {
    public DummyAbility(@Nonnull String name, @Nonnull String description) {
        super(name, description);
    }

    @Nullable
    @Override
    public Response execute(@Nonnull GamePlayer player, @Nonnull ItemStack item) {
        return Response.OK;
    }

    public static DummyAbility of(@Nonnull String name, @Nonnull String description) {
        return new DummyAbility(name, description);
    }
}
