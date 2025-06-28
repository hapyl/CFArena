package me.hapyl.fight.game.cosmetic.gadget.wordle;

import me.hapyl.fight.game.reward.Reward;
import org.bukkit.entity.Player;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class WordlePracticeInstance extends WordleInstance {
    public WordlePracticeInstance(@Nonnull Player player) {
        super(player, Wordle.random());
    }
    
    @Nonnull
    @Override
    public String type() {
        return "Practice";
    }
    
    @Override
    public void onQuit() {
        onLose();
    }
    
    @Nullable
    @Override
    protected Reward reward() {
        return null;
    }
    
    @Override
    protected void saveInstance() {
        // Don't save practice runs
    }
}
