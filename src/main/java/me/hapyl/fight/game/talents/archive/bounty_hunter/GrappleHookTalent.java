package me.hapyl.fight.game.talents.archive.bounty_hunter;

import com.google.common.collect.Maps;
import me.hapyl.fight.game.GamePlayer;
import me.hapyl.fight.game.Response;
import me.hapyl.fight.game.talents.ChargedTalent;
import me.hapyl.fight.util.displayfield.DisplayField;
import me.hapyl.spigotutils.module.chat.Chat;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.SlimeSplitEvent;
import org.bukkit.event.player.PlayerToggleSneakEvent;

import javax.annotation.Nullable;
import java.util.Map;

public class GrappleHookTalent extends ChargedTalent implements Listener {

    @DisplayField(suffix = "blocks") protected final double maxDistance = 30.0d;
    @DisplayField private final int cooldown = 200;

    private final Map<Player, GrappleHook> playerHooks = Maps.newHashMap();

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
    public void onLastCharge(Player player) {
        grantAllCharges(player, cooldown);
        GamePlayer.setCooldown(player, getNoChargedMaterial(), cooldown);
    }

    @EventHandler()
    public void handleSlimeSplit(SlimeSplitEvent ev) {
        ev.setCount(0);
        ev.setCancelled(true);
    }

    @EventHandler()
    public void handleHookRemove(PlayerToggleSneakEvent ev) {
        final Player player = ev.getPlayer();
        final GrappleHook hook = getHook(player);

        if (hook == null || hook.isHookBroken()) {
            return;
        }

        hook.remove();

        // Fx
        Chat.sendTitle(player, "&6∞", "&eHook rope cut!", 0, 10, 10);
    }

    @Override
    public void onDeathCharged(Player player) {
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
    public GrappleHook getHook(Player player) {
        return playerHooks.get(player);
    }

    public boolean hasHook(Player player) {
        return getHook(player) != null;
    }

    @Override
    public Response execute(Player player) {
        final GrappleHook oldHook = getHook(player);

        if (oldHook != null) {
            oldHook.remove();
        }

        playerHooks.put(player, new GrappleHook(player));
        return Response.OK;
    }
}
