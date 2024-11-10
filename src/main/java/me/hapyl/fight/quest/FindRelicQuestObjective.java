package me.hapyl.fight.quest;

import me.hapyl.eterna.module.player.quest.QuestData;
import me.hapyl.eterna.module.player.quest.QuestObjectArray;
import me.hapyl.fight.CF;
import me.hapyl.fight.event.custom.RelicFindEvent;
import me.hapyl.fight.game.collectible.relic.Relic;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;

import javax.annotation.Nonnull;

public class FindRelicQuestObjective extends CFQuestObjective {

    public final int relicId;

    public FindRelicQuestObjective(final int relicId) {
        super("Find a relic somewhere in %s.".formatted(getRelic(relicId).getZone().getName()), 1);

        this.relicId = relicId;
    }

    @Nonnull
    @Override
    public Response test(@Nonnull QuestData data, @Nonnull QuestObjectArray object) {
        return object.compareAs(Integer.class, relicId -> {
            return this.relicId == relicId;
        });
    }

    @Nonnull
    @Override
    protected Class<? extends IHandler<?>> handler() {
        return Handler.class;
    }

    private static Relic getRelic(int relicId) {
        final Relic relic = CF.getPlugin().getRelicHunt().byId(relicId);

        if (relic == null) {
            throw new IllegalArgumentException("Relic '%s' is not registered!".formatted(relicId));
        }

        return relic;
    }

    private static class Handler implements IHandler<RelicFindEvent> {
        @Override
        @EventHandler
        public void handle(@Nonnull RelicFindEvent ev) {
            final Player player = ev.getPlayer();
            final int relicId = ev.getRelic().getId();

            getQuestManager().tryIncrementObjective(player, FindRelicQuestObjective.class, relicId);
        }
    }
}
