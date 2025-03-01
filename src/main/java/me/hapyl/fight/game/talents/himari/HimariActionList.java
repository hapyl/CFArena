package me.hapyl.fight.game.talents.himari;

import com.google.common.collect.Maps;
import me.hapyl.eterna.module.util.CollectionUtils;
import me.hapyl.fight.game.entity.GamePlayer;

import javax.annotation.Nonnull;
import java.util.Map;

public class HimariActionList {

    private final Map<Integer, HimariAction> actions;

    public HimariActionList() {
        this.actions = Maps.newHashMap();
    }

    public HimariAction getRandomAction(@Nonnull GamePlayer player) {
        HimariAction action;
        int pickCount = 5;

        while (pickCount-- > 0) {
            action = CollectionUtils.randomElementOrFirst(actions.values());

            if (action.canExecute(player)) {
                return action;
            }
        }

        // Default to first
        return actions.get(0);
    }

    public void append(@Nonnull HimariAction action) {
        actions.put(actions.size(), action);
    }

    public int indexOf(@Nonnull HimariAction action) {
        for (Map.Entry<Integer, HimariAction> entry : actions.entrySet()) {
            if (entry.getValue().equals(action)) {
                return entry.getKey();
            }
        }

        return 0;
    }

    @Nonnull
    public HimariAction byIndex(int index) {
        return actions.get(index);
    }
}
