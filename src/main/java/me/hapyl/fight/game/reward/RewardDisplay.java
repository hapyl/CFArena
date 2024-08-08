package me.hapyl.fight.game.reward;

import com.google.common.collect.Lists;
import me.hapyl.fight.util.ChatUtils;
import me.hapyl.eterna.module.chat.Chat;
import org.bukkit.entity.Player;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Iterator;
import java.util.List;

public class RewardDisplay implements Iterable<String> {

    public static final RewardDisplay EMPTY = new RewardDisplay();

    private final List<String> strings;

    public RewardDisplay() {
        strings = Lists.newArrayList();
    }

    // note that this does not add the "+" bullet
    public RewardDisplay add() {
        strings.add("");
        return this;
    }

    public RewardDisplay add(@Nonnull String s, @Nullable Object... format) {
        strings.add(Reward.BULLET + s.formatted(format));
        return this;
    }

    public RewardDisplay addIf(boolean condition, @Nonnull String s) {
        if (condition) {
            add(s);
        }

        return this;
    }

    public RewardDisplay addIfElse(boolean condition, @Nonnull String ifTrue, @Nonnull String ifFalse) {
        return add(condition ? ifTrue : ifFalse);
    }

    @Nonnull
    @Override
    public Iterator<String> iterator() {
        return strings.iterator();
    }

    // Fixme -> Mojang does not support custom hover colors???
    //  They do, the bukkit way of §x does not work I think idk
    public void sendHoverMessage(@Nonnull Player player, @Nonnull String message, @Nullable Object... format) {
        Chat.sendHoverableMessage(player, ChatUtils.showText(strings.toArray(new String[0])), message.formatted(format));
    }

    public void sendMessage(@Nonnull Player player) {
        for (String string : strings) {
            Chat.sendMessage(player, " " + string);
        }
    }

    @Nonnull
    public static RewardDisplay of(@Nonnull String... strings) {
        final RewardDisplay display = new RewardDisplay();

        for (String string : strings) {
            display.add(string);
        }

        return display;
    }
}
