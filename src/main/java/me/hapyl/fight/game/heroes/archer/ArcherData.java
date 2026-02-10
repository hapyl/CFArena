package me.hapyl.fight.game.heroes.archer;

import me.hapyl.fight.game.entity.GamePlayer;
import me.hapyl.fight.game.heroes.PlayerData;

public class ArcherData extends PlayerData {
    
    private final Archer archer;
    protected float fuse;
    
    public ArcherData(GamePlayer player, Archer archer) {
        super(player);
        
        this.archer = archer;
        this.fuse = archer.getUltimate().baseFuse;
    }
    
    @Override
    public void remove() {
    }
    
    public void decrementFuse() {
        this.fuse = Math.max(0, this.fuse - archer.getUltimate().fuseShotCost);
    }
}
