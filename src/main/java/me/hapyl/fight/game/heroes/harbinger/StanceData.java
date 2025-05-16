package me.hapyl.fight.game.heroes.harbinger;

import me.hapyl.fight.game.entity.GamePlayer;
import me.hapyl.fight.game.talents.harbinger.MeleeStance;
import me.hapyl.fight.game.task.GameTask;

import javax.annotation.Nonnull;

public class StanceData extends GameTask {
    
    private final GamePlayer player;
    private final MeleeStance talent;
    private final long usedAt;
    
    public StanceData(@Nonnull GamePlayer player, @Nonnull MeleeStance talent) {
        this.player = player;
        this.talent = talent;
        this.usedAt = System.currentTimeMillis();
        
        runTaskLater(talent.maxDuration);
    }
    
    @Override
    public void run() {
        talent.switchTo(player, false);
    }
    
    public long usedAt() {
        return usedAt;
    }
}
