package me.hapyl.fight.game.trial;

import me.hapyl.fight.game.entity.LivingGameEntity;
import org.bukkit.entity.LivingEntity;

import javax.annotation.Nonnull;

public class TrialEntity extends LivingGameEntity {

    private final Trial trial;
    private final int spawnStage;

    public TrialEntity(@Nonnull Trial trial, @Nonnull LivingEntity entity) {
        super(entity);

        this.trial = trial;
        this.spawnStage = trial.getStage();
    }

    @Override
    public void onRemove() {
        super.onRemove();

        trial.entities.remove(this);

        if (trial.getStage() == spawnStage && trial.huskCount() == 0) {
            trial.nextObjective();
        }
    }
}
