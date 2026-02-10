package me.hapyl.fight.game.reward;

import com.google.common.collect.Lists;
import me.hapyl.fight.util.IterableOver;
import org.bukkit.entity.Player;

import javax.annotation.Nonnull;
import java.util.Iterator;
import java.util.List;

public class RewardDescription implements IterableOver<Player, String> {

    private final List<String> description;

    protected RewardDescription() {
        this.description = Lists.newArrayList();
    }

    public RewardDescription appendNl() {
        this.description.add("");
        return this;
    }

    public RewardDescription append(@Nonnull String string) {
        this.description.add(Reward.BULLET + string);
        return this;
    }

    public RewardDescription append(@Nonnull RewardDescription other) {
        this.description.addAll(other.description);
        return this;
    }

    public RewardDescription appendIf(boolean condition, @Nonnull String s) {
        if (condition) {
            append(s);
        }

        return this;
    }

    @Nonnull
    @Override
    public Iterator<String> iterator() {
        return description.iterator();
    }
}
