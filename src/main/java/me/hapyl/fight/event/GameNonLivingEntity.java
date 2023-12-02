package me.hapyl.fight.event;

import me.hapyl.fight.game.attribute.EntityAttributes;
import me.hapyl.fight.game.entity.EntityData;
import me.hapyl.fight.game.entity.LivingGameEntity;
import me.hapyl.fight.game.task.GameTask;
import org.bukkit.entity.LivingEntity;

import javax.annotation.Nonnull;

public class GameNonLivingEntity extends LivingGameEntity {
    public GameNonLivingEntity(@Nonnull LivingEntity entity) {
        super(entity);
    }

    @Nonnull
    @Override
    @Deprecated
    public EntityAttributes getAttributes() {
        throw error("attributes");
    }

    @Nonnull
    @Override
    @Deprecated
    public EntityData getData() {
        throw error("entity data");
    }

    public void schedule(Runnable run, int delay) {
        GameTask.runLater(run, delay);
    }

    private IllegalArgumentException error(String string) {
        return new IllegalArgumentException("non living entities cannot have " + string);
    }
}
