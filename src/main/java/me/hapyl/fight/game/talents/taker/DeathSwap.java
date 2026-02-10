package me.hapyl.fight.game.talents.taker;

import me.hapyl.eterna.module.registry.Key;
import me.hapyl.fight.CF;
import me.hapyl.fight.game.Named;
import me.hapyl.fight.game.Response;
import me.hapyl.fight.game.entity.GamePlayer;
import me.hapyl.fight.game.heroes.HeroRegistry;
import me.hapyl.fight.game.heroes.taker.TakerData;
import me.hapyl.fight.game.talents.Talent;
import me.hapyl.fight.game.talents.TalentType;
import me.hapyl.fight.util.collection.player.PlayerMap;
import me.hapyl.fight.util.displayfield.DisplayField;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerToggleSneakEvent;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class DeathSwap extends Talent implements Listener {
    
    @DisplayField(suffix = " blocks") protected final double maxDistance = 20;
    @DisplayField protected final double step = 1;
    @DisplayField(percentage = true) protected final double damagePercent = 0.1;
    @DisplayField protected final int impairDuration = 60;
    @DisplayField protected final short witherStacks = 3;
    @DisplayField protected final short speedReduction = 50;
    
    @DisplayField private final short spiritualBoneCost = 1;
    @DisplayField(percentage = true) private final double cooldownReduction = 0.5;
    
    private final PlayerMap<TakerHook> playerHooks = PlayerMap.newMap();
    
    public DeathSwap(@Nonnull Key key) {
        super(key, "Hook of Death");
        
        setDescription("""
                       Consume &f{spiritualBoneCost}&7 %s to launch a &4chain&7 that travels in a straight line.
                       &8&o;;The chain retracts after hitting a block or hooking an enemy.
                       
                       If an &cenemy&7 was hooked, they retract with the chain, take &c{damagePercent}&7 of their current health as &cdamage&7 and are &eimpaired&7.
                       &8&o;;Additionally the cooldown is reduced by {cooldownReduction}.
                       
                       &6&lSNEAK&7 to break the chain early.
                       """.formatted(Named.SPIRITUAL_BONES)
        );
        
        setType(TalentType.IMPAIR);
        setMaterial(Material.IRON_CHAIN);
        setCooldownSec(16);
    }
    
    @EventHandler()
    public void handleChainBreak(PlayerToggleSneakEvent ev) {
        final GamePlayer player = CF.getPlayer(ev.getPlayer());
        
        if (player == null) {
            return;
        }
        
        final TakerHook hook = playerHooks.remove(player);
        
        if (hook != null) {
            hook.breakChains();
        }
    }
    
    @Override
    public void onDeath(@Nonnull GamePlayer player) {
        removeHook(player);
    }
    
    @Override
    public @Nullable Response execute(@Nonnull GamePlayer player) {
        final TakerData data = HeroRegistry.TAKER.getPlayerData(player);
        
        if (data.getBones() < spiritualBoneCost) {
            return Response.error("Not enough Spiritual Bones!");
        }
        
        removeHook(player);
        playerHooks.put(player, new TakerHook(this, player));
        
        data.remove(1);
        
        return Response.OK;
    }
    
    public void reduceCooldown(GamePlayer player) {
        player.cooldownManager.setCooldown(this, (int) (getCooldownTimeLeft(player) * cooldownReduction));
    }
    
    private void removeHook(GamePlayer player) {
        final TakerHook hook = playerHooks.remove(player);
        
        if (hook != null) {
            hook.remove();
        }
    }
}
