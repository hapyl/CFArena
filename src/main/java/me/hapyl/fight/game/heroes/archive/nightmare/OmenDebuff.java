package me.hapyl.fight.game.heroes.archive.nightmare;

import me.hapyl.fight.game.entity.GamePlayer;
import me.hapyl.fight.game.entity.LivingGameEntity;
import me.hapyl.fight.game.entity.Ticking;

public class OmenDebuff implements Ticking {

    private final GamePlayer player;
    private final LivingGameEntity entity;
    private int tick;

    public OmenDebuff(GamePlayer player, LivingGameEntity entity) {
        this.player = player;
        this.entity = entity;
    }

    public void addOmen(int tick) {
        this.tick += tick;
    }

    public void setOmen(int tick) {
        this.tick = tick;
    }

    public boolean isAffected() {
        return tick > 0;
    }

    @Override
    public void tick() {
        if (tick > 0) {
            tick--;
        }
    }

}
