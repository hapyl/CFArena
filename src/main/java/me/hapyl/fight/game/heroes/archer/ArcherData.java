package me.hapyl.fight.game.heroes.archer;

import me.hapyl.fight.game.entity.GamePlayer;
import me.hapyl.fight.game.heroes.PlayerData;

public class ArcherData extends PlayerData {

    private final Archer.ArcherUltimate ultimate;
    protected float fuse;

    public ArcherData(GamePlayer player, Archer.ArcherUltimate ultimate) {
        super(player);

        this.fuse = ultimate.baseFuse;
        this.ultimate = ultimate;
    }

    @Override
    public void remove() {
    }

    public void decrementFuse() {
        this.fuse = Math.max(0, this.fuse - ultimate.fuseShotCost);
    }
}
