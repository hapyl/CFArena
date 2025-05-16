package me.hapyl.fight.game.heroes.tamer;

import me.hapyl.eterna.module.util.Ticking;
import me.hapyl.fight.game.entity.GamePlayer;
import me.hapyl.fight.game.heroes.PlayerData;
import me.hapyl.fight.game.talents.tamer.pack.ActiveTamerPack;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class TamerData extends PlayerData implements Ticking {
    
    @Nullable public ActiveTamerPack activePack;
    
    public TamerData(@Nonnull GamePlayer player) {
        super(player);
    }
    
    @Override
    public void remove() {
        if (activePack == null) {
            return;
        }
        
        activePack.remove();
        activePack = null;
    }
    
    @Override
    public void tick() {
        if (activePack == null) {
            return;
        }
        
        // Is over
        if (activePack.isOver()) {
            activePack.recall();
            activePack = null;
            return;
        }
        
        activePack.tick();
    }
}
