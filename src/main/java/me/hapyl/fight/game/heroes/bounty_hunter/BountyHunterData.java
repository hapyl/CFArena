package me.hapyl.fight.game.heroes.bounty_hunter;

import me.hapyl.fight.game.entity.GamePlayer;
import me.hapyl.fight.game.heroes.PlayerData;
import me.hapyl.fight.game.talents.bounty_hunter.GrappleHook;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class BountyHunterData extends PlayerData {
    
    private final BountyHunter hunter;
    
    @Nullable public GrappleHook hook;
    @Nullable public BloodBounty bounty;
    
    public boolean hasUsedSmokeBomb;
    
    public BountyHunterData(@Nonnull BountyHunter hunter, @Nonnull GamePlayer player) {
        super(player);
        
        this.hunter = hunter;
    }
    
    @Override
    public void remove() {
        if (hook != null) {
            hook.remove();
        }
        
        if (bounty != null) {
            bounty.remove(BloodBounty.RemoveCause.PLAYER_DIED);
            bounty = null;
        }
    }
    
}
