package me.hapyl.fight.game.heroes.vortex;

import me.hapyl.eterna.module.util.Ticking;
import me.hapyl.fight.game.Named;
import me.hapyl.fight.game.entity.GamePlayer;
import me.hapyl.fight.game.heroes.PlayerData;
import org.bukkit.ChatColor;
import org.bukkit.Sound;

import javax.annotation.Nonnull;

public class VortexData extends PlayerData implements Ticking {
    
    private static final int STACK_DURATION = 100;
    
    protected int dreams;
    private int tick;
    
    public VortexData(@Nonnull GamePlayer player) {
        super(player);
    }
    
    @Override
    public void tick() {
        if (dreams == 0 || tick-- != 0) {
            return;
        }
        
        if (--dreams > 0) {
            player.playSound(Sound.BLOCK_RESPAWN_ANCHOR_CHARGE, 0.75f);
            tick = STACK_DURATION;
            
            showStacks();
        } else {
            player.playSound(Sound.BLOCK_RESPAWN_ANCHOR_DEPLETE, 0.75f);
            player.sendSubtitle(ChatColor.DARK_GRAY + Named.ASTRAL_SPARK.getPrefix() + " depleted!", 5, 25, 10);
        }
    }
    
    @Override
    public void remove() {
        this.dreams = 0;
        this.tick = 0;
    }
    
    public void incrementDream() {
        this.dreams++;
        this.tick = STACK_DURATION;
        
        // Fx
        player.playSound(Sound.BLOCK_RESPAWN_ANCHOR_CHARGE, 1.75f);
        player.schedule(this::showStacks, 1);
    }
    
    private void showStacks() {
        player.sendSubtitle(ChatColor.YELLOW + Named.ASTRAL_SPARK.getPrefix().repeat(this.dreams), 3, 20, 10);
    }
}
