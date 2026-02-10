package me.hapyl.fight.game.talents.bounty_hunter;

import me.hapyl.eterna.module.math.Tick;
import me.hapyl.eterna.module.registry.Key;
import me.hapyl.fight.CF;
import me.hapyl.fight.game.Response;
import me.hapyl.fight.game.entity.GamePlayer;
import me.hapyl.fight.game.heroes.HeroRegistry;
import me.hapyl.fight.game.heroes.bounty_hunter.BountyHunter;
import me.hapyl.fight.game.heroes.bounty_hunter.BountyHunterData;
import me.hapyl.fight.game.talents.ChargedTalent;
import me.hapyl.fight.game.talents.TalentType;
import me.hapyl.fight.util.CFUtils;
import me.hapyl.fight.util.displayfield.DisplayField;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerToggleSneakEvent;

import javax.annotation.Nonnull;

public class GrappleHookTalent extends ChargedTalent implements Listener {
    
    @DisplayField(suffix = " blocks") protected final double maxDistance = 50;
    @DisplayField protected final double hookExtendSpeed = 0.75;
    @DisplayField protected final double hookPullSpeed = 0.8;
    
    @DisplayField protected final double strafeSmoothFactor = 0.5;
    @DisplayField protected final double strafeStrength = 0.5;
    
    @DisplayField protected final short cutsToRemove = 3;
    @DisplayField protected final int maxAirTicks = Tick.fromSeconds(8);
    
    @DisplayField private final int cooldown = 200;
    
    public GrappleHookTalent(@Nonnull Key key) {
        super(key, "Grappling Hook", 3);
        
        setDescription("""
                       Launch a &6grappling hook&7 forward.
                       
                       Whenever it hits a &3block&7 or an &cenemy&7, it attaches and pull your towards it.
                       &8&o;;Other players can clear the hook attached to them.
                       
                       You can also &dstrafe&7 while on the hook using the &bhorizontal&7 movement keys.
                       &8&o;;Defaults to A and D.
                       
                       &8&o;;This talent can be used %s consecutively before recharging.
                       """.formatted(CFUtils.toWordCount(maxCharges()))
        );
        
        setType(TalentType.MOVEMENT);
        setMaterial(Material.LEAD);
    }
    
    @EventHandler()
    public void handleHookRemove(PlayerToggleSneakEvent ev) {
        final GamePlayer player = CF.getPlayer(ev);
        
        if (player == null) {
            return;
        }
        
        final BountyHunter bountyHunter = HeroRegistry.BOUNTY_HUNTER;
        
        // Break own hook
        final BountyHunterData data = bountyHunter.getPlayerData(player);
        
        if (data.hook != null) {
            data.hook.remove();
            
            player.sendTitle("&6&l🪝", "&eRope cut!", 0, 10, 10);
            player.playWorldSound(Sound.ENTITY_SHEEP_SHEAR, 0.75f);
            return;
        }
        
        // Try to escape hook
        
        // Only cut the hook when sneaking
        if (!ev.isSneaking()) {
            return;
        }
        
        for (BountyHunterData otherData : bountyHunter.getDataMap().values()) {
            if (otherData.hook == null) {
                continue;
            }
            
            otherData.hook.tryEscape(player);
        }
    }
    
    @Nonnull
    @Override
    public Response execute(@Nonnull GamePlayer player, int charges) {
        final BountyHunterData data = HeroRegistry.BOUNTY_HUNTER.getPlayerData(player);
        
        if (data.hook != null) {
            data.hook.remove();
        }
        
        data.hook = new GrappleHook(this, data);
        
        // Fx
        player.playWorldSound(Sound.ENTITY_BAT_TAKEOFF, 1.0f);
        player.playWorldSound(Sound.ITEM_LEAD_BREAK, 0.0f);
        
        return Response.OK;
    }
    
    @Override
    public void onLastCharge(@Nonnull GamePlayer player) {
        rechargeAll(player, cooldown);
    }
    
}
