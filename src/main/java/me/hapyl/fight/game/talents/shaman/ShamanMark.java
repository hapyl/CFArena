package me.hapyl.fight.game.talents.shaman;

import me.hapyl.fight.game.Constants;
import me.hapyl.fight.game.Debug;
import me.hapyl.fight.game.attribute.AttributeType;
import me.hapyl.fight.game.attribute.EntityAttributes;
import me.hapyl.fight.game.entity.GamePlayer;
import me.hapyl.fight.game.entity.LivingGameEntity;
import me.hapyl.fight.game.task.TickingGameTask;
import me.hapyl.eterna.module.math.Geometry;
import me.hapyl.eterna.module.math.geometry.WorldParticle;
import org.bukkit.Particle;
import org.bukkit.Sound;

public class ShamanMark extends TickingGameTask {

    private final ShamanMarkTalent talent;
    private final GamePlayer player;
    private final LivingGameEntity entity;

    private int noLosTicks;

    public ShamanMark(ShamanMarkTalent talent, GamePlayer player, LivingGameEntity entity) {
        this.talent = talent;
        this.player = player;
        this.entity = entity;

        runTaskTimer(0, 1);
    }

    @Override
    public void onTaskStart() {
        talent.temperInstance.temper(entity, Constants.INFINITE_DURATION);

        // Fx
        entity.playWorldSound(Sound.ENTITY_FROG_HURT, 0.5f);
        entity.playWorldSound(Sound.ENTITY_FROG_DEATH, 0.0f);
    }

    @Override
    public void onTaskStop() {
        talent.temperInstance.untemper(entity);

        // Fx
        entity.playWorldSound(Sound.ENTITY_GOAT_HORN_BREAK, 0.0f);
    }

    @Override
    public void run(int tick) {
        // Los
        if (player.hasLineOfSight(entity)) {
            noLosTicks = 0;
        }
        else {
            noLosTicks++;
        }

        // Break
        if (entity.isDeadOrRespawning() || noLosTicks >= talent.outOfSightDuration) {
            talent.remove(player);
            return;
        }

        // Fx
        Geometry.drawLine(player.getMidpointLocation(), entity.getMidpointLocation(), 0.5d, new WorldParticle(Particle.WITCH));
    }

    private void debug() {
        final EntityAttributes attributes = entity.getAttributes();
        Debug.info(attributes.get(AttributeType.SPEED));
        Debug.info(attributes.get(AttributeType.ATTACK));
        Debug.info(attributes.get(AttributeType.ATTACK_SPEED));
    }
}
