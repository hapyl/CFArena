package me.hapyl.fight.game.talents.archive.taker;

import me.hapyl.fight.game.Response;
import me.hapyl.fight.game.entity.GamePlayer;
import me.hapyl.fight.game.heroes.Heroes;
import me.hapyl.fight.game.heroes.archive.taker.SpiritualBones;
import me.hapyl.fight.game.heroes.archive.taker.Taker;
import me.hapyl.fight.game.talents.Talent;
import me.hapyl.fight.util.collection.player.PlayerMap;
import me.hapyl.fight.util.displayfield.DisplayField;
import org.bukkit.Material;

import javax.annotation.Nonnull;

public class DeathSwap extends Talent {

    @DisplayField(suffix = "blocks") protected final double maxDistance = 20.0d;
    @DisplayField protected final double shift = 0.55d;
    @DisplayField(suffix = "%", suffixSpace = false) protected final double damagePercent = 10.0d;
    @DisplayField private final short spiritualBoneCost = 1;

    private final PlayerMap<TakerHook> playerHooks = PlayerMap.newMap();

    public DeathSwap() {
        super("Hook of Death");

        setDescription("""
                Instantly consume {spiritualBoneCost} &eSpiritual Bone&7 to launch a chain that travels in straight line up to &b{maxDistance}&7 blocks or until opponent is hit.
                                
                After, retracts back to you.
                                
                If opponent is hit, they will be retracted with chains, take &c{damagePercent}&7 of their current health as damage will be slowed and withered for short duration.
                                
                Additionally, the cooldown is reduced by &b50%%&7.
                """);

        setItem(Material.CHAIN);
        setCooldownSec(16);
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
