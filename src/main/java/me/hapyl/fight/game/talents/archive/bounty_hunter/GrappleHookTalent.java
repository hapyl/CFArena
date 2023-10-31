package me.hapyl.fight.game.talents.archive.bounty_hunter;

import me.hapyl.fight.CF;
import me.hapyl.fight.game.Response;
import me.hapyl.fight.game.entity.GamePlayer;
import me.hapyl.fight.game.talents.ChargedTalent;
import me.hapyl.fight.util.collection.player.PlayerMap;
import me.hapyl.fight.util.displayfield.DisplayField;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.SlimeSplitEvent;
import org.bukkit.event.player.PlayerToggleSneakEvent;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class GrappleHookTalent extends ChargedTalent implements Listener {

    @DisplayField(suffix = "blocks") protected final double maxDistance = 30.0d;
    @DisplayField private final int cooldown = 200;

    private final PlayerMap<GrappleHook> playerHooks = PlayerMap.newMap();

    public GrappleHookTalent() {
        super("Grappling Hook", 3);

        setDescription("""
                Launch a grappling hook that travels up to &b{maxDistance}&7 blocks.
                                
                Whenever it hits a &bblock&7 or an &bentity&7, it will pull you towards it.
                                
                &6;;Cooldown starts after all charges are used.
                """);

        setNoChargedMaterial(Material.GOAT_HORN);
        setItem(Material.LEAD);
    }

    @Override
    public void onLastCharge(@Nonnull GamePlayer player) {
        grantAllCharges(player, cooldown);
        player.setCooldown(getNoChargedMaterial(), cooldown);
    }

    @EventHandler()
    public void handleSlimeSplit(SlimeSplitEvent ev) {
        ev.setCount(0);
        ev.setCancelled(true);
    }

    @EventHandler()
    public void handleHookRemove(PlayerToggleSneakEvent ev) {
        final GamePlayer player = CF.getPlayer(ev.getPlayer());

        if (player == null) {
            return;
        }

        final GrappleHook hook = getHook(player);

        if (hook == null || hook.isHookBroken()) {
            return;
        }

        hook.remove();

        // Fx
        player.sendTitle("&6∞", "&eHook rope cut!", 0, 10, 10);
    }

    @Override
    public void onDeathCharged(@Nonnull GamePlayer player) {
        final GrappleHook hook = getHook(player);

        if (hook != null) {
            hook.remove();
        }
    }

    @Override
    public void onStopCharged() {
        playerHooks.values().forEach(GrappleHook::remove);
        playerHooks.clear();
    }

    @Nullable
    public GrappleHook getHook(GamePlayer player) {
        return playerHooks.get(player);
    }

    public boolean hasHook(GamePlayer player) {
        return getHook(player) != null;
    }

    @Override
    public Response execute(@Nonnull GamePlayer player) {
        final GrappleHook oldHook = getHook(player);

        if (oldHook != null) {
            oldHook.remove();
        }

        playerHooks.put(player, new GrappleHook(player));
        return Response.OK;
    }
}
