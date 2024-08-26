package me.hapyl.fight.game.reward;

import com.google.common.collect.Lists;
import me.hapyl.eterna.module.inventory.ItemBuilder;
import me.hapyl.fight.Notifier;
import me.hapyl.fight.util.IterableOver;
import org.bukkit.entity.Player;

import javax.annotation.Nonnull;
import java.util.Iterator;
import java.util.List;

public class RewardDescription implements IterableOver<Player, String> {

    public static final RewardDescription EMPTY = new RewardDescription();

    private final List<String> description;

    public RewardDescription() {
        this.description = Lists.newArrayList();
    }

    public RewardDescription addEmptyLine() {
        this.description.add("");
        return this;
    }

    public RewardDescription add(@Nonnull String string) {
        this.description.add(Reward.BULLET + string);
        return this;
    }

    public RewardDescription addIf(boolean condition, @Nonnull String s) {
        if (condition) {
            add(s);
        }

        return this;
    }

    @Nonnull
    @Override
    public Iterator<String> iterator() {
        return description.iterator();
    }

    @Nonnull
    public static RewardDescription of(@Nonnull String... strings) {
        final RewardDescription display = new RewardDescription();

        for (String string : strings) {
            display.add(string);
        }

        return display;
    }
}
