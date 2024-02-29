package me.hapyl.fight.game.entity;

import me.hapyl.fight.game.team.GameTeam;
import org.bukkit.entity.LivingEntity;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * Taunt entities, well, taunt enemies.
 */
public class TauntLivingGameEntity extends LivingGameEntity {

    protected final GameTeam tauntTeam;

    protected double tauntRadius;

    public TauntLivingGameEntity(@Nonnull LivingEntity entity, @Nullable GameTeam tauntTeam) {
        super(entity);

        this.tauntTeam = tauntTeam;
        this.tauntRadius = 5.0d;
    }

    @Override
    public void tick() {
    }
}
