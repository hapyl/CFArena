package me.hapyl.fight.game.talents.himari;

import com.google.common.collect.Lists;
import me.hapyl.eterna.module.util.CollectionUtils;
import me.hapyl.fight.Message;
import me.hapyl.fight.game.entity.GamePlayer;

import javax.annotation.Nonnull;
import java.util.List;

public class HimariActionList {

    private final List<HimariAction> actions;

    public HimariActionList() {
        this.actions = Lists.newArrayList();
    }

    public void randomActionAndExecute(@Nonnull GamePlayer player) {
        HimariAction action;
        int pickCount = 5;

        while (pickCount-- > 0) {
            action = CollectionUtils.randomElementOrFirst(actions);

            if (action.execute(player)) {
                return;
            }
        }

        player.sendMessage(Message.ERROR, "Unable to pick an action!");
    }

    public void append(@Nonnull HimariAction action) {
        actions.add(action);
    }

}
