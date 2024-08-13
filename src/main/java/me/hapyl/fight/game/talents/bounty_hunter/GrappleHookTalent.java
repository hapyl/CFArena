package me.hapyl.fight.game.talents.bounty_hunter;

import me.hapyl.fight.CF;
import me.hapyl.fight.game.GameInstance;
import me.hapyl.fight.game.Response;
import me.hapyl.fight.game.entity.GamePlayer;
import me.hapyl.fight.game.talents.ChargedTalent;
import me.hapyl.fight.game.talents.TalentType;
import me.hapyl.fight.util.collection.player.PlayerMap;
import me.hapyl.fight.util.displayfield.DisplayField;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.SlimeSplitEvent;
import org.bukkit.event.player.PlayerToggleSneakEvent;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class GrappleHookTalent extends ChargedTalent implements Listener {

    @DisplayField public final double onHookMultiplier = 1.5d;
    @DisplayField public final short cutsToRemove = 3;
    @DisplayField(suffix = "blocks") protected final double maxDistance = 30.0d;
    @DisplayField private final int cooldown = 200;

    protected final PlayerMap<GrappleHook> playerHooks = PlayerMap.newMap();

    public GrappleHookTalent() {
        super("Grappling Hook", 3);

        setDescription("""
                Launch a &6grappling hook&7 that travels up to &b{maxDistance}&7.
                                
                Whenever it hits a &3block&7 or an &cenemy&7, it will pull &nyou&7 &ntowards&7 it.
                &8;;The hook can be destroyed.
                                
                &8;;The cooldown of this ability stars after all the charges are used.
                """);

        setType(TalentType.MOVEMENT);
        setItem(Material.LEAD);
        setNoChargedMaterial(Material.GOAT_HORN);
    }

    @EventHandler()
    public void handleHookRemove(PlayerToggleSneakEvent ev) {
        final GamePlayer player = CF.getPlayer(ev.getPlayer());

        if (player == null) {
            return;
        }

        // Own hook
        final GrappleHook playerHook = getHook(player);

        if (playerHook != null) {
            if (playerHook.isHookBroken()) {
                return;
            }

            playerHook.remove();

            // Fx
            player.sendTitle("&6\uD83E\uDE9D", "&eHook rope cut!", 0, 10, 10);
            return;
        }

        if (!ev.isSneaking()) {
            return;
        }

        for (GrappleHook hook : playerHooks.values()) {
            if (!player.equals(hook.hookedEntity)) {
                continue;
            }

            // Fx
            player.playSound(Sound.ENTITY_SHEEP_SHEAR, 0.5f + (0.25f * hook.ropeCuts));

            hook.ropeCuts++;

            if (hook.ropeCuts >= cutsToRemove) {
                hook.remove();

                hook.player.sendTitle("&6\uD83E\uDE9D", "&e%s escaped your hook!".formatted(player.getName()), 5, 15, 5);

                player.sendTitle("&a\uD83E\uDE9D".repeat(cutsToRemove), "&aEscaped!", 5, 15, 5);
                return;
            }
        }

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

    @Override
    public void onDeath(@Nonnull GamePlayer player) {
        super.onDeath(player);

        final GrappleHook hook = getHook(player);

        if (hook != null) {
            hook.remove();
        }
    }

    @Override
    public void onStop(@Nonnull GameInstance instance) {
        super.onStop(instance);

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

        playerHooks.put(player, new GrappleHook(this, player));
        return Response.OK;
    }
}
