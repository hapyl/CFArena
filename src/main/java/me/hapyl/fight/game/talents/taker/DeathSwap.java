package me.hapyl.fight.game.talents.taker;

import me.hapyl.fight.CF;
import me.hapyl.fight.game.Named;
import me.hapyl.fight.game.Response;
import me.hapyl.fight.game.entity.GamePlayer;
import me.hapyl.fight.game.heroes.Heroes;
import me.hapyl.fight.game.heroes.taker.SpiritualBones;
import me.hapyl.fight.game.heroes.taker.Taker;
import me.hapyl.fight.game.talents.TalentType;
import me.hapyl.fight.game.talents.techie.Talent;
import me.hapyl.fight.util.collection.player.PlayerMap;
import me.hapyl.fight.util.displayfield.DisplayField;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerToggleSneakEvent;

import javax.annotation.Nonnull;

public class DeathSwap extends Talent implements Listener {

    @DisplayField(suffix = "blocks") protected final double maxDistance = 20.0d;
    @DisplayField protected final double shift = 0.55d;
    @DisplayField(suffix = "%", suffixSpace = false) protected final double damagePercent = 10.0d;
    @DisplayField private final short spiritualBoneCost = 1;

    private final PlayerMap<TakerHook> playerHooks = PlayerMap.newMap();

    public DeathSwap() {
        super("Hook of Death");

        setDescription("""
                Instantly consume &b{spiritualBoneCost}&7 of your %s to launch a &4chain&7 that travels in a straight line.
                                
                If traveled &b{maxDistance}&7 or a block or an enemy is hit, the &4chain&7 retracts.
                                
                If an opponent is hit, they will be retracted &nwith the chain&7, take &c{damagePercent}&7 of their &c&ncurrent health&7 as &4damage&7, will be &3slowed&7 and &8withered&7 for short duration.
                
                &8;;Additionally, the cooldown is reduced by 50%%.
                """, Named.SPIRITUAL_BONES);

        setType(TalentType.IMPAIR);
        setItem(Material.CHAIN);
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

    public double getMaxDistanceScaled() {
        return maxDistance / shift;
    }

    @Override
    public Response execute(@Nonnull GamePlayer player) {
        final SpiritualBones bones = Heroes.TAKER.getHero(Taker.class).getBones(player);

        if (!player.isOnGround()) {
            return Response.error("You must be grounded to use this!");
        }

        if (bones.getBones() < spiritualBoneCost) {
            return Response.error("Not enough &lSpiritual Bones&c!");
        }

        removeHook(player);
        playerHooks.put(player, new TakerHook(player));

        bones.remove(1);

        return Response.OK;
    }

    public void reduceCooldown(GamePlayer player) {
        player.setCooldown(getMaterial(), getCdTimeLeft(player) / 2);
    }

    private void removeHook(GamePlayer player) {
        final TakerHook hook = playerHooks.get(player);
        if (hook == null) {
            return;
        }

        hook.remove();
        playerHooks.remove(player);
    }
}
