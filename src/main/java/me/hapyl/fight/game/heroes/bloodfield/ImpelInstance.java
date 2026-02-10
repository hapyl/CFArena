package me.hapyl.fight.game.heroes.bloodfield;

import me.hapyl.eterna.module.util.Removable;
import me.hapyl.fight.game.entity.GamePlayer;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Set;

public class ImpelInstance implements Removable {
    
    protected final BloodfiendData data;
    protected final GamePlayer player;
    protected final Set<GamePlayer> targets;
    
    private Impel impel;
    private int impelCount;
    
    public ImpelInstance(@Nonnull BloodfiendData data, @Nonnull GamePlayer player, @Nonnull Set<GamePlayer> targets) {
        this.data = data;
        this.player = player;
        this.targets = targets;
        
        if (targets.isEmpty()) {
            player.sendMessage("&cNo one to impel!");
        }
        else {
            player.sendMessage("&eImpelled %s enemies!".formatted(targets.size()));
        }
    }
    
    public boolean isPlayer(@Nonnull GamePlayer player) {
        if (impel == null) {
            return false;
        }
        
        return impel.targets.contains(player);
    }
    
    public void nextImpel(int delay) {
        final Bloodfiend.BloodfiendUltimate impelUltimate = data.bloodfiend.getUltimate();
        
        impel = new Impel(this, ImpelType.random()) {
            @Override
            public void onImpelStop() {
                if (++impelCount < impelUltimate.impelTimes) {
                    nextImpel(impelUltimate.impelCd);
                }
            }
        };
        
        impel.runTaskTimer(delay, 1);
    }
    
    @Override
    public void remove() {
        if (impel != null) {
            impel.cancel();
        }
    }
    
    @Nullable
    public Impel getImpel() {
        return impel;
    }
}
