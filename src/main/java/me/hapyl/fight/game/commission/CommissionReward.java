package me.hapyl.fight.game.commission;

import me.hapyl.fight.game.reward.RepeatableReward;
import org.bukkit.inventory.ItemStack;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class CommissionReward extends RepeatableReward {

    @Nullable private ItemStack icon;

    public CommissionReward(@Nonnull String name) {
        super(name);

        this.icon = null;
    }

    @Nullable
    public ItemStack icon() {
        return icon;
    }

    public void icon(@Nonnull ItemStack icon) {
        this.icon = icon;
    }
}
